package dev.sid.fiveberinspector;
/** Fixed, APK-bound ADB entrypoint. No downloaded helper or host Python dependency. */
final class ConnectionCommand {
  static String forApk(String path) {
    if(path==null||path.contains("/../")||path.contains("/./")||!path.matches("/data/app/[A-Za-z0-9_~./+=-]+/base\\.apk")||!path.contains("/dev.sid.fiveberinspector-"))throw new IllegalArgumentException("Unexpected installed APK path");
    return "adb shell 'test \"$(am get-current-user)\" = 0 || exit 1; CLASSPATH="+path+" nohup app_process / --nice-name=8bercue-management-v1 dev.sid.fiveberinspector.CompanionManagementStarter --connect-management > /dev/null 2>&1 < /dev/null &'";
  }
  private ConnectionCommand(){}
}
