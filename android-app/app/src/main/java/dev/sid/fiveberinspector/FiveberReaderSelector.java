package dev.sid.fiveberinspector;

import android.os.Process;
import android.util.Log;
import java.lang.reflect.InvocationTargetException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Read-only capability-based reader discovery. Ambiguity never selects a default slot. */
public final class FiveberReaderSelector {
    private static final String ISDR_AID = "A0000005591010FFFFFFFF8900000100";
    static boolean isKnownCardHash(String digest){return ReaderAdmission.known(digest);}
    static String knownCardLabel(String digest){return ReaderAdmission.label(digest);}
    private static final byte[] GET_EID = {(byte)0xBF,0x3E,3,0x5C,1,0x5A};
    private static final int MAX_RESPONSE_BYTES = 256;
    private static final int MAX_FRAMES = 6;

    public static final class Target {
        public final String reader;
        public final String sha256Prefix;
        public final String identityHash;
        private Target(String reader,String prefix) {
            this.reader=reader;this.identityHash=prefix.toLowerCase(Locale.ROOT);this.sha256Prefix=prefix.substring(0,16);
        }
    }

    private FiveberReaderSelector() {}
    private static String upperHex(byte[] data) {
        StringBuilder sb=new StringBuilder(data.length*2);
        for(byte b:data) sb.append(String.format(Locale.ROOT,"%02X",b&255));
        return sb.toString();
    }
    private static byte[] fromHex(String str) {
        if(str==null || (str.length()%2)!=0)throw new SecurityException("Bad AID");
        byte[] r=new byte[str.length()/2];
        for(int i=0;i<r.length;i++)r[i]=(byte)Integer.parseInt(str.substring(2*i,2*i+2),16);
        return r;
    }
    private static Throwable unwrap(Throwable e) {
        while(e instanceof InvocationTargetException &&
                ((InvocationTargetException)e).getTargetException()!=null)
            e=((InvocationTargetException)e).getTargetException();
        return e;
    }
    private static byte[] readEid(SecureElementTransport hal,int ch) throws Exception {
        byte[] cmd = new byte[5+GET_EID.length];
        cmd[0]=(byte)(0x80|ch);cmd[1]=(byte)0xE2;cmd[2]=(byte)0x91;
        cmd[3]=0;cmd[4]=(byte)GET_EID.length;
        System.arraycopy(GET_EID,0,cmd,5,GET_EID.length);
        java.io.ByteArrayOutputStream body=new java.io.ByteArrayOutputStream();
        for(int i=0;i<MAX_FRAMES;i++) {
            byte[] reply=hal.transmit(cmd);
            if(reply==null || reply.length<2 || body.size()+reply.length>MAX_RESPONSE_BYTES)
                throw new SecurityException("Unexpected GetEID reply length");
            body.write(reply,0,reply.length-2);
            int sw1=reply[reply.length-2]&255, sw2=reply[reply.length-1]&255;
            if(sw1==0x90&&sw2==0)break;
            if(sw1!=0x61 || i==MAX_FRAMES-1)
                throw new SecurityException("GetEID status/continuation rejected");
            cmd=new byte[]{(byte)(0x80|ch),(byte)0xC0,0,0,(byte)sw2};
        }
        byte[] data=body.toByteArray();
        if(data.length!=21 ||
            (data[0]&255)!=0xBF || (data[1]&255)!=0x3E ||
            (data[2]&255)!=0x12 || (data[3]&255)!=0x5A ||
            (data[4]&255)!=0x10)
            throw new SecurityException("Invalid BER eUICC GetEID response");
        return Arrays.copyOfRange(data,5,21);
    }
    private static String digestPrefix(byte[] eid) throws Exception {
        byte[] h=MessageDigest.getInstance("SHA-256").digest(eid);
        return upperHex(h);
    }
    private static Target inspectOne(String reader) throws Exception {
        SecureElementTransport hal=null;
        int channel=-1;
        String phase="service_lookup";
        try {
            hal=SystemHalAdapter.connect(reader);
            phase="card_presence";
            if(!hal.isCardPresent())return null;
            phase="open_channel";
            channel=hal.openLogicalChannel(fromHex(ISDR_AID),(byte)0);
            if(channel<1 || channel>3)
                throw new SecurityException("Unexpected logical channel");
            phase="eid_read";
            String digest=digestPrefix(readEid(hal,channel));
            if(ReaderAdmission.accepts(digest))
                return new Target(reader,digest);
            Log.w("EightberReaderPilot", "reader="+reader+" phase=invalid_eid_identity");
            return null;
        }catch(Throwable ignored) {
            // Reader class / stage only: never log APDUs, full EIDs or
            // exception messages. This distinguishes denial from slot absence.
            Throwable cause=unwrap(ignored);
            Log.w("EightberReaderPilot", "reader="+reader+
                " phase="+phase+" error="+cause.getClass().getSimpleName());
            SecureElementFailure failure=SecureElementFailure.find(ignored);
            if(failure!=null)throw failure;
            if(cause instanceof SecurityException && !"eid_read".equals(phase))
                throw new SecureElementFailure(SecureElementFailure.Reason.ACCESS_DENIED);
            if(cause instanceof ClassNotFoundException||cause instanceof NoSuchMethodException)
                throw new SecureElementFailure(SecureElementFailure.Reason.SYSTEM_INTERFACE_UNAVAILABLE);
            return null;
        }finally{
            if(channel>0 && hal!=null) {
                try {hal.closeChannel(channel);}
                catch(Throwable ignored) {
                    throw new SecureElementFailure(SecureElementFailure.Reason.CHANNEL_CLEANUP_FAILED);
                }
            }
        }
    }

    // Discovery only. Every mutation separately verifies caller, intent and full identity.
    public static Target discover() throws Exception { return discoverInternal(false); }

    // Compatibility entry point for the independent read-only lab inventory.
    // It shares generic discovery; the lab inventory may further restrict its display.
    public static Target discoverKnownCardReadOnly() throws Exception {
        return discoverInternal(true);
    }

    private static Target discoverInternal(boolean crossDeviceReadOnly) throws Exception {
        if(Process.myUid()!=Process.SHELL_UID)
            throw new SecurityException("Requires authorized ADB Shell UID2000");
        List<Target> matches=new ArrayList<>();
        SecureElementFailure failure=null;
        for(String reader:SystemHalAdapter.readers()) {
            try {
                Target found=inspectOne(reader);
                if(found!=null)matches.add(found);
            } catch(SecureElementFailure denied) {
                if(denied.reason==SecureElementFailure.Reason.CHANNEL_CLEANUP_FAILED)throw denied;
                failure=denied;
            }
        }
        if(matches.size()>1)
            throw new SecureElementFailure(SecureElementFailure.Reason.AMBIGUOUS_EUICC);
        if(matches.isEmpty()) {
            if(failure!=null)throw failure;
            throw new SecureElementFailure(SecureElementFailure.Reason.NO_READABLE_EUICC);
        }
        return matches.get(0);
    }

}
