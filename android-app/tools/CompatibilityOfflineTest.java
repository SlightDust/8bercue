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

 public static final class Request {
  public String aid,callingPackage;public int slotIndex,portIndex,subId,p2,channel;public Object binder;
 }
 public static final class Result {
  int status=1,channel=2;
  public int getStatus(){return status;}public int getChannel(){return channel;}
 }
 public static final class Phone {
  Request last;Result result=new Result();int frames,cla,command,p3;String data;String reply="9000";boolean failSend,closed,closeOk=true;
  public Result iccOpenLogicalChannel(Request request){last=request;return result;}
  public String iccTransmitApduLogicalChannelByPort(int slot,int port,int channel,int cla,int command,int p1,int p2,int p3,String data){
   check(slot==1&&port==0&&channel==2);frames++;this.cla=cla;this.command=command;this.p3=p3;this.data=data;if(failSend)throw new IllegalStateException("Synthetic lost result");return reply;
  }
  public boolean iccCloseLogicalChannel(Request request){check(request.slotIndex==1&&request.portIndex==0&&request.channel==2);closed=true;return closeOk;}
 }
 static void renameTests()throws Exception{
  String id="8900000000000000001",other="8900000000000000002";
  for(ProfileLifecycleGuard.Operation op:ProfileLifecycleGuard.Operation.values()){
   check(ReaderDescriptor.lifecycleAllowed("telephony.1.0",op,false)==(op==ProfileLifecycleGuard.Operation.RENAME));
   check(!ReaderDescriptor.lifecycleAllowed("telephony.1.0",op,true));
  }
  check(!ReaderDescriptor.lifecycleAllowed("telephony.8.0",ProfileLifecycleGuard.Operation.RENAME,false));
  for(int channel=1;channel<=3;channel++){
   final int c=channel;TelephonyRenamePolicy policy=new TelephonyRenamePolicy(id,"SMC test");
   rejects(()->policy.authorize(ProfileLifecycleGuard.command(ProfileLifecycleGuard.Operation.RENAME,other,"SMC test",c),c));
   rejects(()->policy.authorize(ProfileLifecycleGuard.command(ProfileLifecycleGuard.Operation.RENAME,id,"wrong",c),c));
   for(ProfileLifecycleGuard.Operation op:ProfileLifecycleGuard.Operation.values())if(op!=ProfileLifecycleGuard.Operation.RENAME)
    rejects(()->policy.authorize(ProfileLifecycleGuard.command(op,id,"",c),c));
   byte[] exact=ProfileLifecycleGuard.command(ProfileLifecycleGuard.Operation.RENAME,id,"SMC test",c);
   policy.authorize(exact,c);rejects(()->policy.authorize(exact,c));
   policy.acceptResponse(new byte[]{0x61,2});
   rejects(()->policy.authorize(new byte[]{(byte)(0x80|c),(byte)0xC0,0,0,3},c));
   for(int i=0;i<6;i++){policy.authorize(new byte[]{(byte)(0x80|c),(byte)0xC0,0,0,2},c);policy.acceptResponse(new byte[]{0x61,2});}
   rejects(()->policy.authorize(new byte[]{(byte)(0x80|c),(byte)0xC0,0,0,2},c));
  }
  Phone phone=new Phone();TelephonySecureElementTransport transport=new TelephonySecureElementTransport(phone,Phone.class,Request.class,1,0,new Object(),id,"SMC test");
  byte[] aid=TelephonyChannelCodec.unhex(TelephonyChannelCodec.ISDR);transport.openLogicalChannel(aid,(byte)0);
  rejects(()->transport.transmit(ProfileLifecycleGuard.command(ProfileLifecycleGuard.Operation.DELETE,id,"",2)));check(phone.frames==0);
  byte[] exact=ProfileLifecycleGuard.command(ProfileLifecycleGuard.Operation.RENAME,id,"SMC test",2);
  transport.transmit(exact);check(phone.frames==1&&phone.cla==0x80&&phone.data.equals(TelephonyChannelCodec.hex(exact,5)));
  rejects(()->transport.transmit(exact));check(phone.frames==1);transport.closeChannel(2);
  rejects(()->transport.openLogicalChannel(aid,(byte)0));
  Phone lost=new Phone();lost.failSend=true;TelephonySecureElementTransport uncertain=new TelephonySecureElementTransport(lost,Phone.class,Request.class,1,0,new Object(),id,"SMC test");
  uncertain.openLogicalChannel(aid,(byte)0);
  try{uncertain.transmit(exact);throw new AssertionError("Expected lost response");}catch(InvocationTargetException expected){check(lost.frames==1);}
  rejects(()->uncertain.transmit(exact));check(lost.frames==1);uncertain.closeChannel(2);
  List<ProfileLifecycleGuard.Row> before=List.of(new ProfileLifecycleGuard.Row(id,false,"SMC","S","N"),new ProfileLifecycleGuard.Row(other,true,"povo","K","N"));
  List<ProfileLifecycleGuard.Row> renamed=List.of(new ProfileLifecycleGuard.Row(id,false,"SMC test","S","N"),before.get(1));
  check(ProfileLifecycleGuard.exactTransition(ProfileLifecycleGuard.Operation.RENAME,before,renamed,id,"SMC test"));
  check(ProfileLifecycleGuard.exactTransition(ProfileLifecycleGuard.Operation.RENAME,renamed,before,id,"SMC"));
  for(ProfileLifecycleGuard.Row changed:List.of(new ProfileLifecycleGuard.Row(other,false,"povo","K","N"),new ProfileLifecycleGuard.Row(other,true,"changed","K","N"),new ProfileLifecycleGuard.Row(other,true,"povo","other","N")))
   check(!ProfileLifecycleGuard.exactTransition(ProfileLifecycleGuard.Operation.RENAME,before,List.of(renamed.get(0),changed),id,"SMC test"));
 }
 static void telephonyTests()throws Exception{
  byte[] canonical=java.util.HexFormat.of().parseHex(TelephonyChannelCodec.ISDR);
  String encoded=TelephonyChannelCodec.aid(canonical);
  check(Arrays.equals(canonical,java.util.HexFormat.of().parseHex(encoded)));
  rejects(()->TelephonyChannelCodec.aid(new byte[]{0,0}));
  rejects(()->TelephonyChannelCodec.unhex("900"));
  rejects(()->TelephonyChannelCodec.unhex("90xz"));
  check(Arrays.equals(ReaderDescriptor.telephonyIndices("telephony.1.0"),new int[]{1,0}));
  rejects(()->ReaderDescriptor.telephonyIndices("telephony.8.0"));
  rejects(()->ReaderDescriptor.telephonyIndices("telephony.1.-1"));
  rejects(()->ReaderDescriptor.requireWritable("telephony.1.0"));
  check(ReaderDescriptor.writable("SIM1")&&ReaderDescriptor.writable("omapi.SIM1"));
  Phone phone=new Phone();Object token=new Object();
  TelephonySecureElementTransport transport=new TelephonySecureElementTransport(phone,Phone.class,Request.class,1,0,token);
  check(transport.openLogicalChannel(TelephonyChannelCodec.unhex(TelephonyChannelCodec.ISDR),(byte)0)==2);
  check(phone.last.subId==-1&&phone.last.callingPackage.equals("com.android.shell")&&phone.last.binder==token);
  check(phone.last.aid.equals("a0000005591010ffffffff8900000100"));
  rejects(()->transport.openLogicalChannel(TelephonyChannelCodec.unhex(TelephonyChannelCodec.ISDR),(byte)0));
  rejects(()->transport.transmit(new byte[]{(byte)0x82,(byte)0xE2,(byte)0x91,0,3,(byte)0xBF,0x31,0}));
  check(phone.frames==0);
  transport.transmit(new byte[]{(byte)0x82,(byte)0xE2,(byte)0x91,0,3,(byte)0xBF,0x2D,0});
  check(phone.frames==1&&phone.cla==0x80&&phone.command==0xE2&&phone.p3==3&&phone.data.equals("bf2d00"));
  rejects(()->transport.transmit(new byte[]{(byte)0x82,(byte)0xC0,0,0,0}));
  rejects(()->transport.closeChannel(1));check(!phone.closed);
  transport.closeChannel(2);check(phone.closed);rejects(()->transport.transmit(new byte[]{(byte)0x82,0,0,0,0}));
  Phone closeDenied=new Phone();closeDenied.closeOk=false;
  TelephonySecureElementTransport closeTransport=new TelephonySecureElementTransport(closeDenied,Phone.class,Request.class,1,0,token);
  closeTransport.openLogicalChannel(canonical,(byte)0);
  try{closeTransport.closeChannel(2);throw new AssertionError();}
  catch(SecureElementFailure expected){check(expected.reason==SecureElementFailure.Reason.CHANNEL_CLEANUP_FAILED);}
  Phone unavailable=new Phone();unavailable.result.status=3;
  try{new TelephonySecureElementTransport(unavailable,Phone.class,Request.class,1,0,token).openLogicalChannel(TelephonyChannelCodec.unhex(TelephonyChannelCodec.ISDR),(byte)0);throw new AssertionError();}
  catch(java.util.NoSuchElementException expected){check(!unavailable.closed);}
  SystemHalAdapter.transports.clear();SystemHalAdapter.fallback.clear();
  Card denied=new Card(1);denied.denied=true;SystemHalAdapter.transports.put("omapi.SIM1",denied);
  SystemHalAdapter.fallback.put("telephony.1.0",new Card(1));
  check(FiveberReaderSelector.discover().reader.equals("telephony.1.0"));
  SystemHalAdapter.transports.clear();SystemHalAdapter.transports.put("SIM1",new Card(1));SystemHalAdapter.transports.put("SIM2",new Card(2));
  int calls=SystemHalAdapter.fallbackCalls;
  try{FiveberReaderSelector.discover();throw new AssertionError();}catch(SecureElementFailure failure){check(failure.reason==SecureElementFailure.Reason.AMBIGUOUS_EUICC);}
  check(SystemHalAdapter.fallbackCalls==calls);SystemHalAdapter.fallback.clear();SystemHalAdapter.transports.clear();
 }

 public static void main(String[] args)throws Exception {
  telephonyTests();renameTests();
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
