package dev.sid.fiveberinspector;
import java.util.*;
/** Test-only registry. It is never included in the application source set. */
final class SystemHalAdapter {
 static final Map<String,SecureElementTransport> transports=new LinkedHashMap<>();
 static List<String> readers(){return new ArrayList<>(transports.keySet());}
 static SecureElementTransport connect(String reader){return transports.get(reader);}
}
