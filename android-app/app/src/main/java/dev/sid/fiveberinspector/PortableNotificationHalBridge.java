package dev.sid.fiveberinspector;
import android.os.Process;
/** No caller-supplied APDU: only exact notification retrieve and acknowledged removal. */
final class PortableNotificationHalBridge implements AutoCloseable {
  private final String reader,hash;
  private final NotificationSessionPolicy policy;
  private SecureElementTransport hal;private int channel=-1;
  PortableNotificationHalBridge(String reader,String hash,long sequence){this.reader=reader;this.hash=hash;policy=new NotificationSessionPolicy(sequence);}
  private void open() throws Exception {
    if(channel>0)return;
    if(Process.myUid()!=Process.SHELL_UID)throw new SecurityException("Shell identity required");
    FiveberReaderSelector.Target card=FiveberReaderSelector.discover();
    if(!reader.equals(card.reader)||!hash.equals(card.identityHash))throw new SecurityException("Notification card changed");
    ReaderDescriptor.requireWritable(reader);
    hal=SystemHalAdapter.connect(reader);
    if(!hal.isCardPresent())throw new SecurityException("Card unavailable");
    channel=hal.openLogicalChannel(PortableDownloadHalBridge.unhex("A0000005591010FFFFFFFF8900000100"),(byte)0);
    if(channel<1||channel>3)throw new SecurityException("Invalid notification channel");
  }
  byte[] retrieve() throws Exception {open();return exchange();}
  byte[] removeAfterAcknowledgement() throws Exception {
    DownloadWorkerSupervisor.checkCancelled();
    FiveberReaderSelector.Target current=FiveberReaderSelector.discover();
    if(!reader.equals(current.reader)||!hash.equals(current.identityHash))throw new SecurityException("Notification card changed before cleanup");
    policy.acknowledged();return exchange();
  }
  private byte[] exchange() throws Exception {
    for(;;) {
      DownloadWorkerSupervisor.checkCancelled();byte[] command=policy.next(channel);
      byte[] response=hal.transmit(command);
      DownloadWorkerSupervisor.checkCancelled();byte[] complete=policy.accept(response);if(complete!=null)return complete;
    }
  }
  @Override public void close(){if(channel>0&&hal!=null)try{hal.closeChannel(channel);}catch(Exception ignored){}channel=-1;}
}
