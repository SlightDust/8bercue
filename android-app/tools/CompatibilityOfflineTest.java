package dev.sid.fiveberinspector;
import java.util.*;
import java.lang.reflect.*;

/** Synthetic card identities and APIs only. No device, account, network or activation code. */
public final class CompatibilityOfflineTest {
 private static int assertions;
 static void check(boolean value){assertions++;if(!value)throw new AssertionError("Assertion "+assertions);}
 interface Throwing {void run()throws Exception;}
 static void rejects(Throwing action)throws Exception {
  try{action.run();throw new AssertionError("Expected rejection");}
  catch(SecurityException expected){assertions++;}
 }
 public static final class Opened {public int channelNumber=2;}
 public static final class FakeAidl {
  int closed,transmitted;byte[] last;
  public boolean isCardPresent(){return true;}
  public Opened openLogicalChannel(byte[] aid,byte p2){return new Opened();}
  public byte[] transmit(byte[] command){transmitted++;last=command.clone();return new byte[]{(byte)0x90,0};}
  public void closeChannel(byte channel){closed=channel;}
 }
 public interface Listener {}
 public static final class FakeChannel {
  boolean closed,failClose;byte[] last;
  public byte[] transmit(byte[] command){last=command.clone();return new byte[]{(byte)0x90,0};}
  public void close(){closed=true;if(failClose)throw new SecurityException("Synthetic close denial");}
 }
 public static final class FakeSession {
  boolean closed,denied,nullChannel;FakeChannel channel=new FakeChannel();
  public FakeChannel openLogicalChannel(byte[] aid,byte p2,Listener listener){
   if(denied)throw new SecurityException("Never expose this vendor message");
   return nullChannel?null:channel;
  }
  public void close(){closed=true;}
 }
 public static final class FakeReader {
  FakeSession session=new FakeSession();
  public boolean isSecureElementPresent(){return true;}
  public FakeSession openSession(){return session;}
 }
 static OmapiSecureElementTransport omapi(FakeReader reader){
  return new OmapiSecureElementTransport(reader,new Listener(){},FakeReader.class,FakeSession.class,FakeChannel.class,Listener.class);
 }
 static final class Card implements SecureElementTransport {
  int closed;byte[] eid=new byte[16];boolean denied,bad,fragmented,endless;int frames;
  Card(int identity){eid[0]=(byte)identity;}
  public boolean isCardPresent(){return true;}
  public int openLogicalChannel(byte[] aid,byte p2){if(denied)throw new SecureElementFailure(SecureElementFailure.Reason.ACCESS_DENIED);return 1;}
  public byte[] transmit(byte[] cmd){
   frames++;
   if(endless)return new byte[]{0x61,0x15};
   if(bad)return new byte[]{0x6A,(byte)0x82};
   byte[] body=new byte[21];System.arraycopy(new byte[]{(byte)0xBF,0x3E,0x12,0x5A,0x10},0,body,0,5);System.arraycopy(eid,0,body,5,16);
   if(fragmented&&frames==1)return new byte[]{0x61,0x15};
   if(fragmented)check(Arrays.equals(cmd,new byte[]{(byte)0x81,(byte)0xC0,0,0,0x15}));
   return java.nio.ByteBuffer.allocate(23).put(body).put((byte)0x90).put((byte)0).array();
  }
  public void closeChannel(int channel){closed++;}
 }
 public static void main(String[] args)throws Exception {
  check(ManagementAvailability.reason(33,new String[]{"arm64-v8a"}).isEmpty());
  check(ManagementAvailability.reason(36,new String[]{"arm64-v8a"}).isEmpty());
  check(!ManagementAvailability.reason(33,new String[]{"armeabi-v7a"}).isEmpty());
  check(!ManagementAvailability.reason(27,new String[]{"arm64-v8a"}).isEmpty());
  check(ReaderDescriptor.omapiInstances(new String[]{"SIM1"}).equals(List.of("omapi.SIM1")));
  rejects(()->ReaderDescriptor.omapiInstances(new String[]{"SIM1","SIM1"}));
  rejects(()->ReaderDescriptor.omapiInstances(new String[]{"../SIM1"}));
  rejects(()->ReaderDescriptor.instances(new String[]{ReaderDescriptor.PREFIX+"omapi.SIM1"}));
  FakeAidl fake=new FakeAidl();SecureElementTransport aidl=new AidlSecureElementTransport(fake,FakeAidl.class);
  check(aidl.isCardPresent());check(aidl.openLogicalChannel(new byte[]{1},(byte)0)==2);
  aidl.transmit(new byte[]{(byte)0x82,0,0,0,0});aidl.closeChannel(2);check(fake.closed==2&&fake.transmitted==1);
  FakeReader reader=new FakeReader();SecureElementTransport se=omapi(reader);
  check(se.isCardPresent());check(se.openLogicalChannel(new byte[]{1},(byte)0)==1);
  rejects(()->se.openLogicalChannel(new byte[]{1},(byte)0));
  byte[] command={(byte)0x81,(byte)0xE2,(byte)0x91,0,0};se.transmit(command);
  check(command[0]==(byte)0x81&&reader.session.channel.last[0]==(byte)0x80);
  rejects(()->se.transmit(new byte[]{(byte)0x82,0,0,0,0}));
  rejects(()->se.closeChannel(2));check(!reader.session.closed);
  se.closeChannel(1);check(reader.session.closed&&reader.session.channel.closed);
  rejects(()->se.transmit(command));se.closeChannel(1);
  FakeReader denied=new FakeReader();denied.session.denied=true;
  try{omapi(denied).openLogicalChannel(new byte[]{1},(byte)0);throw new AssertionError();}
  catch(SecureElementFailure failure){check(failure.reason==SecureElementFailure.Reason.ACCESS_DENIED);check(!failure.getMessage().contains("vendor"));}
  check(denied.session.closed);
  FakeReader nullChannel=new FakeReader();nullChannel.session.nullChannel=true;
  try{omapi(nullChannel).openLogicalChannel(new byte[]{1},(byte)0);throw new AssertionError();}catch(IllegalStateException expected){check(nullChannel.session.closed);}
  FakeReader cleanup=new FakeReader();OmapiSecureElementTransport cleanupSe=omapi(cleanup);cleanupSe.openLogicalChannel(new byte[]{1},(byte)0);cleanup.session.channel.failClose=true;
  try{cleanupSe.closeChannel(1);throw new AssertionError();}catch(InvocationTargetException expected){check(cleanup.session.closed);}
  SystemHalAdapter.transports.clear();
  try{FiveberReaderSelector.discover();throw new AssertionError();}catch(SecureElementFailure failure){check(failure.reason==SecureElementFailure.Reason.NO_READABLE_EUICC);}
  Card one=new Card(1);one.fragmented=true;SystemHalAdapter.transports.put("omapi.SIM1",one);
  FiveberReaderSelector.Target target=FiveberReaderSelector.discover();check(target.reader.equals("omapi.SIM1"));check(target.identityHash.length()==64&&one.closed==1);
  one.fragmented=false;Card two=new Card(2);SystemHalAdapter.transports.put("omapi.SIM2",two);
  try{FiveberReaderSelector.discover();throw new AssertionError();}catch(SecureElementFailure failure){check(failure.reason==SecureElementFailure.Reason.AMBIGUOUS_EUICC);}
  check(two.closed==1);SystemHalAdapter.transports.clear();
  Card blocked=new Card(1);blocked.denied=true;SystemHalAdapter.transports.put("omapi.SIM1",blocked);
  try{FiveberReaderSelector.discover();throw new AssertionError();}catch(SecureElementFailure failure){check(failure.reason==SecureElementFailure.Reason.ACCESS_DENIED);}
  SystemHalAdapter.transports.clear();Card malformed=new Card(1);malformed.bad=true;SystemHalAdapter.transports.put("SIM1",malformed);
  try{FiveberReaderSelector.discover();throw new AssertionError();}catch(SecureElementFailure failure){check(failure.reason==SecureElementFailure.Reason.NO_READABLE_EUICC);}
  check(malformed.closed==1);
  SystemHalAdapter.transports.clear();Card endless=new Card(1);endless.endless=true;SystemHalAdapter.transports.put("SIM1",endless);
  try{FiveberReaderSelector.discover();throw new AssertionError();}catch(SecureElementFailure failure){check(failure.reason==SecureElementFailure.Reason.NO_READABLE_EUICC);}
  check(endless.frames==6&&endless.closed==1);android.os.Process.uid=10001;rejects(()->FiveberReaderSelector.discover());android.os.Process.uid=2000;
  CardReadApduPolicy policy=new CardReadApduPolicy(1,false);
  rejects(()->policy.authorize(new byte[]{(byte)0x81,(byte)0xE2,(byte)0x91,0,3,(byte)0xBF,0x31,0}));
  policy.authorize(new byte[]{(byte)0x81,(byte)0xE2,(byte)0x91,0,3,(byte)0xBF,0x2D,0});policy.acceptResponse(new byte[]{(byte)0x90,0});
  rejects(()->policy.authorize(new byte[]{(byte)0x81,(byte)0xC0,0,0,0}));
  List<ProfileLifecycleGuard.Row> rows=List.of(new ProfileLifecycleGuard.Row("8900000000000000001",true,"","", ""),new ProfileLifecycleGuard.Row("8900000000000000002",false,"","",""));
  rejects(()->ProfileLifecycleGuard.validate(ProfileLifecycleGuard.Operation.DELETE,rows,rows.get(0).id,""));
  rejects(()->ProtectedProfilePolicy.validate(ProfileLifecycleGuard.Operation.ENABLE,rows,rows.get(1).id,rows.get(0).id));
  check(!ProfileLifecycleGuard.selectionFingerprint(rows).equals(ProfileLifecycleGuard.selectionFingerprint(List.of(rows.get(1),rows.get(0)))));
  check(SecureElementFailure.message("ACCESS_DENIED").contains("ROM"));check(SecureElementFailure.message("untrusted vendor text").isEmpty());
  System.out.println("PASS compatibility and safety regressions: "+assertions+" assertions (synthetic; no live success claim)");
 }
}
