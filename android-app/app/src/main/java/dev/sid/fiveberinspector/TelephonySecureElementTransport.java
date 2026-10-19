package dev.sid.fiveberinspector;

/** Shell transport: read-only by default, or one exact confirmed nickname command. */
final class TelephonySecureElementTransport implements SecureElementTransport {
 private final Object phone,token;
 private final Class<?> api,requestApi;
 private final int slot,port;
 private int channel=-1;
 private CardReadApduPolicy policy;
 private TelephonyRenamePolicy rename;
 private boolean opened;
 TelephonySecureElementTransport(Object phone,Class<?> api,Class<?> requestApi,int slot,int port,Object token){
  this.phone=phone;this.api=api;this.requestApi=requestApi;this.slot=slot;this.port=port;this.token=token;
 }
 TelephonySecureElementTransport(Object phone,Class<?> api,Class<?> requestApi,int slot,int port,Object token,String id,String nickname){
  this(phone,api,requestApi,slot,port,token);rename=new TelephonyRenamePolicy(id,nickname);
 }
 private Object request()throws Exception{
  Object request=requestApi.getConstructor().newInstance();
  requestApi.getField("subId").setInt(request,-1);
  requestApi.getField("slotIndex").setInt(request,slot);requestApi.getField("portIndex").setInt(request,port);
  requestApi.getField("callingPackage").set(request,"com.android.shell");
  requestApi.getField("binder").set(request,token);
  return request;
 }
 public boolean isCardPresent() {return true;} // Inventoried active slot; SELECT and GetEID verify the card.
 public int openLogicalChannel(byte[] aid,byte p2)throws Exception{
  if(channel>0||p2!=0||(rename!=null&&opened))throw new SecurityException("Unexpected logical channel open");
  Object request=request();requestApi.getField("aid").set(request,TelephonyChannelCodec.aid(aid));
  requestApi.getField("p2").setInt(request,0);
  Object result=api.getMethod("iccOpenLogicalChannel",requestApi).invoke(phone,request);
  if(result==null)throw new IllegalStateException("No channel result");
  int status=(Integer)result.getClass().getMethod("getStatus").invoke(result);
  if(status!=1)throw new java.util.NoSuchElementException("ISD-R channel unavailable");
  channel=(Integer)result.getClass().getMethod("getChannel").invoke(result);
  if(channel<1||channel>3){
   if(channel>0)closeChannel(channel);
   throw new SecurityException("Unsupported logical channel");
  }
  opened=true;policy=new CardReadApduPolicy(channel,true);
  return channel;
 }
 public byte[] transmit(byte[] command)throws Exception{
  if(channel<1||policy==null)throw new SecurityException("No owned channel");
  if(rename==null)policy.authorize(command);else rename.authorize(command,channel); // Consumed before Binder; never retry an uncertain send.
  int lc=command[4]&255;
  if(command.length!=5&&command.length!=5+lc)throw new SecurityException("Unsupported APDU shape");
  String result=(String)api.getMethod("iccTransmitApduLogicalChannelByPort",int.class,int.class,int.class,int.class,int.class,int.class,int.class,int.class,String.class)
   .invoke(phone,slot,port,channel,command[0]&0xFC,command[1]&255,command[2]&255,command[3]&255,lc,TelephonyChannelCodec.hex(command,5));
  byte[] response=TelephonyChannelCodec.unhex(result);
  if(rename==null)policy.acceptResponse(response);else rename.acceptResponse(response);return response;
 }
 public void closeChannel(int number)throws Exception{
  if(channel<1)return;
  if(number!=channel)throw new SecurityException("Foreign logical channel");
  Object request=request();requestApi.getField("channel").setInt(request,channel);
  if(!(Boolean)api.getMethod("iccCloseLogicalChannel",requestApi).invoke(phone,request))
   throw new SecureElementFailure(SecureElementFailure.Reason.CHANNEL_CLEANUP_FAILED);
  channel=-1;policy=null;
 }
}
