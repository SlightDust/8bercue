package dev.sid.fiveberinspector;
import java.io.*;
import java.util.concurrent.TimeUnit;
/** System AIDL or framework OMAPI, selected by actual service availability. */
final class SystemHalAdapter {
 static String dexPath()throws Exception {
  java.lang.Process process=new ProcessBuilder("/system/bin/pm","path","com.android.se").redirectError(ProcessBuilder.Redirect.to(new File("/dev/null"))).start();
  try {
   if(!process.waitFor(5,TimeUnit.SECONDS)||process.exitValue()!=0)throw new SecurityException("System HAL package unavailable");
   try(Reader in=new InputStreamReader(process.getInputStream(),java.nio.charset.StandardCharsets.UTF_8)){
    String line=BoundedLines.read(in,2048);if(line==null||!line.startsWith("package:")||BoundedLines.read(in,2048)!=null)throw new SecurityException("Ambiguous HAL package");
    String path=line.substring(8);File file=new File(path);
    if(!path.matches("/(system|system_ext|product|vendor|apex)/[A-Za-z0-9_./@+-]+\\.apk")||!file.isFile()||!file.getCanonicalPath().equals(path))throw new SecurityException("Untrusted HAL class source");
    return path;
   }
  }finally{if(process.isAlive())process.destroyForcibly();}
 }
 static void requireShell() {
  if(android.os.Process.myUid()!=android.os.Process.SHELL_UID)
   throw new SecurityException("Authorized Shell identity required");
 }
 private static Object service(String name)throws Exception {
  return Class.forName("android.os.ServiceManager").getMethod("getService",String.class).invoke(null,name);
 }
 private static Object asInterface(String name,Object binder)throws Exception {
  return Class.forName(name+"$Stub").getMethod("asInterface",android.os.IBinder.class).invoke(null,binder);
 }
 static java.util.List<String> readers()throws Exception {
  requireShell();
  try {
   String[] services=(String[])Class.forName("android.os.ServiceManager").getMethod("listServices").invoke(null);
   java.util.List<String> aidl=ReaderDescriptor.instances(services);
   if(!aidl.isEmpty())return aidl;
   Object binder=service("secure_element");
   if(binder==null)throw new SecureElementFailure(SecureElementFailure.Reason.NO_SERVICE);
   Object omapi=asInterface("android.se.omapi.ISecureElementService",binder);
   String[] names=(String[])Class.forName("android.se.omapi.ISecureElementService").getMethod("getReaders").invoke(omapi);
   return ReaderDescriptor.omapiInstances(names);
  } catch(ClassNotFoundException|NoSuchMethodException error) {
   throw new SecureElementFailure(SecureElementFailure.Reason.SYSTEM_INTERFACE_UNAVAILABLE);
  } catch(java.lang.reflect.InvocationTargetException error) {
   if(error.getTargetException() instanceof SecurityException)
    throw new SecureElementFailure(SecureElementFailure.Reason.ACCESS_DENIED);
   throw error;
  }
 }
 static SecureElementTransport connect(String reader)throws Exception {
  requireShell();
  if(!ReaderDescriptor.valid(reader))throw new SecurityException("Invalid reader");
  try {
   if(reader.startsWith(ReaderDescriptor.OMAPI_PREFIX)) {
    Object binder=service("secure_element");
    if(binder==null)throw new SecureElementFailure(SecureElementFailure.Reason.NO_SERVICE);
    String name="android.se.omapi.ISecureElementService";
    Object omapi=asInterface(name,binder);
    Object selected=Class.forName(name).getMethod("getReader",String.class).invoke(omapi,reader.substring(ReaderDescriptor.OMAPI_PREFIX.length()));
    if(selected==null)throw new SecureElementFailure(SecureElementFailure.Reason.NO_SERVICE);
    Class<?> listener=Class.forName("android.se.omapi.ISecureElementListener");
    android.os.Binder token=new android.os.Binder();
    Object callback=java.lang.reflect.Proxy.newProxyInstance(listener.getClassLoader(),new Class<?>[]{listener},(proxy,method,args)->{
     if(method.getName().equals("asBinder"))return token;
     throw new UnsupportedOperationException("Unexpected listener method");
    });
    return new OmapiSecureElementTransport(selected,callback,
     Class.forName("android.se.omapi.ISecureElementReader"),Class.forName("android.se.omapi.ISecureElementSession"),
     Class.forName("android.se.omapi.ISecureElementChannel"),listener);
   }
   Object binder=service(ReaderDescriptor.PREFIX+reader);
   if(binder==null)throw new SecureElementFailure(SecureElementFailure.Reason.NO_SERVICE);
   ClassLoader loader=new dalvik.system.PathClassLoader(dexPath(),SystemHalAdapter.class.getClassLoader());
   String name="android.hardware.secure_element.ISecureElement";
   Class<?> api=Class.forName(name,true,loader);
   Object hal=Class.forName(name+"$Stub",true,loader).getMethod("asInterface",android.os.IBinder.class).invoke(null,binder);
   return new AidlSecureElementTransport(hal,api);
  } catch(ClassNotFoundException|NoSuchMethodException error) {
   throw new SecureElementFailure(SecureElementFailure.Reason.SYSTEM_INTERFACE_UNAVAILABLE);
  } catch(java.lang.reflect.InvocationTargetException error) {
   if(error.getTargetException() instanceof SecurityException)
    throw new SecureElementFailure(SecureElementFailure.Reason.ACCESS_DENIED);
   throw error;
  }
 }
 private SystemHalAdapter(){}
}
