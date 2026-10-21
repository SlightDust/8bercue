package dev.sid.fiveberinspector;

import java.util.*;

/** Verifies the post-REFRESH settling loop tolerates a slow reader.
 * A full inventory read costs seconds on real hardware, so a fixed read count bounded by a
 * short supervisor timeout made every switch time out and quarantine the card. */
public final class LifecyclePostflightOfflineTest {
    static int checks;
    static void check(boolean ok,String label){checks++;if(!ok)throw new AssertionError(label);}

    static List<ProfileLifecycleGuard.Row> rows(boolean firstEnabled){
        return List.of(new ProfileLifecycleGuard.Row("8900000000000000001",firstEnabled,"","A","a"),
                       new ProfileLifecycleGuard.Row("8900000000000000002",!firstEnabled,"","B","b"));
    }

    /** Stands in for the reported device: each read is slow, and the post-switch inventory only
     * becomes visible once the REFRESH silence has passed. */
    static final long READ_MS=600L, REFRESH_SILENT_MS=4_000L;
    static final class SlowCard implements LifecyclePostflight.Reader {
        final long start=System.currentTimeMillis();
        final List<ProfileLifecycleGuard.Row> before, after;
        int reads;
        SlowCard(List<ProfileLifecycleGuard.Row> before,List<ProfileLifecycleGuard.Row> after){this.before=before;this.after=after;}
        public List<ProfileLifecycleGuard.Row> read() throws Exception {
            Thread.sleep(READ_MS);
            reads++;
            return System.currentTimeMillis()-start>=REFRESH_SILENT_MS?after:before;
        }
    }

    public static void main(String[] args) throws Exception {
        List<ProfileLifecycleGuard.Row> before=rows(true), after=rows(false);

        // A budget shorter than the REFRESH silence cannot observe the transition.
        SlowCard early=new SlowCard(before,after);
        LifecyclePostflight.Result missed=LifecyclePostflight.observe(ProfileLifecycleGuard.Operation.ENABLE,
            before,"8900000000000000002","",early,millis->Thread.sleep(millis),System.currentTimeMillis()+2_500L);
        check(!missed.verified,"a budget shorter than REFRESH silence observes no transition");

        // The same card settles once the budget covers the silence and several slow reads.
        SlowCard card=new SlowCard(before,after);
        LifecyclePostflight.Result settled=LifecyclePostflight.observe(ProfileLifecycleGuard.Operation.ENABLE,
            before,"8900000000000000002","",card,millis->Thread.sleep(millis),System.currentTimeMillis()+15_000L);
        check(settled.verified,"slow reader settles within the observation budget");
        check(settled.reads>=3,"verification spans several slow reads");

        // An already-expired deadline performs no card I/O at all.
        LifecyclePostflight.Reader silent=new LifecyclePostflight.Reader(){
            public List<ProfileLifecycleGuard.Row> read(){return before;}
        };
        LifecyclePostflight.Result expired=LifecyclePostflight.observe(ProfileLifecycleGuard.Operation.ENABLE,
            before,"8900000000000000002","",silent,millis->{},System.currentTimeMillis());
        check(!expired.verified&&expired.reads==0,"an expired deadline performs no read");

        // Non-state operations keep their single observation.
        LifecyclePostflight.Result rename=LifecyclePostflight.observe(ProfileLifecycleGuard.Operation.RENAME,
            before,"8900000000000000001","x",silent,millis->{},0L);
        check(rename.reads==1,"rename keeps a single read");

        // A zero deadline means "unbounded", not "already expired".
        LifecyclePostflight.Result unbounded=LifecyclePostflight.observe(ProfileLifecycleGuard.Operation.RENAME,
            before,"8900000000000000001","x",silent,millis->{},0L);
        check(unbounded.reads==1,"zero deadline still observes");

        System.out.println("PASS "+checks+" post-REFRESH settling assertions (slow reader, deadline, single-read)");
    }
    private LifecyclePostflightOfflineTest(){}
}
