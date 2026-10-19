package dev.sid.fiveberinspector;
import java.util.Arrays;
/** One confirmed nickname command, then at most six response continuations. No retry. */
final class TelephonyRenamePolicy {
 private final String id,nickname;
 private boolean submitted,awaiting,continuation;
 private int expectedLe=-1,frames,bytes;
 TelephonyRenamePolicy(String id,String nickname){
  ProfileLifecycleGuard.command(ProfileLifecycleGuard.Operation.RENAME,id,nickname,1);
  this.id=id;this.nickname=nickname;
 }
 void authorize(byte[] command,int channel){
  if(awaiting||frames>=7)throw new SecurityException("Rename response budget exhausted");
  byte[] expected=!submitted?ProfileLifecycleGuard.command(ProfileLifecycleGuard.Operation.RENAME,id,nickname,channel):
   new byte[]{(byte)(0x80|channel),(byte)0xC0,0,0,(byte)expectedLe};
  if((submitted&&!continuation)||!Arrays.equals(expected,command))throw new SecurityException("Command outside confirmed rename");
  submitted=true;awaiting=true;continuation=false;frames++;
 }
 void acceptResponse(byte[] reply){
  if(!awaiting||reply==null||reply.length<2||reply.length>8192||bytes+reply.length-2>32768)throw new SecurityException("Malformed rename response");
  awaiting=false;bytes+=reply.length-2;continuation=(reply[reply.length-2]&255)==0x61;
  expectedLe=continuation?reply[reply.length-1]&255:-1;
 }
}
