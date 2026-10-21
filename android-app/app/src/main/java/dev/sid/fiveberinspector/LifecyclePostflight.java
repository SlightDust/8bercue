package dev.sid.fiveberinspector;
import java.util.List;
/** Read-only settling after REFRESH. This helper has no write/rollback capability. */
final class LifecyclePostflight {
  interface Reader {List<ProfileLifecycleGuard.Row> read() throws Exception;}
  interface Pause {void sleep(long millis) throws InterruptedException;}
  static final class Result {
    final boolean verified,unchanged;final int reads;
    Result(boolean verified,boolean unchanged,int reads){this.verified=verified;this.unchanged=unchanged;this.reads=reads;}
  }
  /** Poll schedule for a state transition. A REFRESH can keep the card silent for tens
   * of seconds, and each full inventory read costs seconds on slow readers, so the loop
   * is bounded by a wall-clock deadline (see PortableProfileLifecycle) rather than by a
   * fixed read count. The backoff below is deliberately patient: the early reads right
   * after REFRESH are expected to fail. */
  private static final long[] STATE_DELAYS={0,2500,3000,4000,5000,6000,7000,8000,9000,10000};
  private static final long[] SINGLE_DELAYS={0};
  static Result observe(ProfileLifecycleGuard.Operation op,List<ProfileLifecycleGuard.Row> before,String target,String nickname,Reader reader,Pause pause,long deadlineMillis) throws Exception {
    String original=ProfileLifecycleGuard.fingerprint(before);
    boolean stateOp=op==ProfileLifecycleGuard.Operation.ENABLE||op==ProfileLifecycleGuard.Operation.DISABLE;
    long[] delays=stateOp?STATE_DELAYS:SINGLE_DELAYS;
    boolean unchanged=false;int reads=0;
    for(int i=0;i<delays.length;i++) {
      if(expired(deadlineMillis))break;
      DownloadWorkerSupervisor.checkCancelled();if(delays[i]>0)pause.sleep(delays[i]);DownloadWorkerSupervisor.checkCancelled();
      // Re-check after the backoff so a long wait cannot push the read past the deadline.
      if(expired(deadlineMillis))break;
      unchanged=false;
      try {
        List<ProfileLifecycleGuard.Row> after=reader.read();reads++;DownloadWorkerSupervisor.checkCancelled();
        boolean verified=ProfileLifecycleGuard.exactTransition(op,before,after,target,nickname);
        unchanged=original.equals(ProfileLifecycleGuard.fingerprint(after));
        if(verified)return new Result(true,false,reads);
      } catch(InterruptedException interrupted){throw interrupted;}
      catch(Exception temporarilyUnavailable) { /* Only another read, never another mutation. */ }
    }
    return new Result(false,unchanged,reads);
  }
  private static boolean expired(long deadlineMillis){return deadlineMillis>0&&System.currentTimeMillis()>=deadlineMillis;}
  private LifecyclePostflight(){}
}
