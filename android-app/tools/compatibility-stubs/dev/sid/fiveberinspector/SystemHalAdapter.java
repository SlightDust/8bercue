package dev.sid.fiveberinspector;
import java.util.*;
/** Test-only registry. It is never included in the application source set. */
final class SystemHalAdapter {
 static final Map<String,SecureElementTransport> transports=new LinkedHashMap<>();
 static final Map<String,SecureElementTransport> fallback=new LinkedHashMap<>();
 static void requireShell(){if(android.os.Process.myUid()!=2000)throw new SecurityException("Shell required");}
 static SecureElementTransport connectForNotificationMetadata(String reader){return connect(reader);}
 static int fallbackCalls;
 static SecureElementTransport notification,removal;
 static SecureElementTransport connectForNotification(String reader,long sequence){return notification;}
 static SecureElementTransport connectForNotificationRemoval(String reader,long sequence){return removal;}
 static List<String> telephonyReaders(){fallbackCalls++;return new ArrayList<>(fallback.keySet());}
 static List<String> readers(){return new ArrayList<>(transports.keySet());}
 static SecureElementTransport connect(String reader){return transports.containsKey(reader)?transports.get(reader):fallback.get(reader);}
}
