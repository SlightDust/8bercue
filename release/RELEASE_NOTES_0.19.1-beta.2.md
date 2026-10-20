# 8bercue 0.19.1 Beta 2

Simplified Chinese and a follow-up fix for **Verify card and recovery**. APK `0.19.1-beta.2`, versionCode **47**, existing production signing identity. ARM64 Android 9+ installation; card access remains capability-dependent.

Beta 1 (46) was downloaded from its Release and tested on a Motorola edge 70 / API37. Same-signer upgrade, authenticated ADB connection, Chinese/English switching and fresh SIM2 Profile reads passed. Its explicit recovery button exposed a nested caller-scope error; this release fixes that composition. Beta 1's APK/source remain unchanged.

## Changes and checks

- Retain the real authenticated Binder caller while reconciliation delegates to the inventory method's own Shell scope. Do not nest or weaken caller authentication.
- Keep the original unknown transaction, quarantine marker and exclusive card lock. Read-only recovery does not replay an operation or authorize changes.
- Retain the 477-string Simplified Chinese translation and language selector, capability-based card channels, stable ADB command and privacy-first feedback/export code from main.
- GlitchTip SDK is included, but this APK has **no DSN**. Remote reporting is unavailable; manual email and local diagnostic export remain available. Optional crash reporting is off by default. No reports were submitted in qualification.
- Release build/lint: passed, 0 errors / 55 warnings. Four new composition assertions execute the production reconciliation method with simulated identity/card I/O; the test reproduces Beta 1's failure and passes the fix. Prior unchanged recovery/bootstrap/UI/compatibility checks total 474 assertions; bilingual and privacy contracts passed again. Signed artifact and independent corresponding-source rebuild verification accompany this release.

The owner requested publication before device testing. **APK47 live results are pending at publication** and will be added in a separate post-release validation report. APK46 results above do not qualify APK47. No Profile writes are authorized for this round. Download, notification delivery/cleanup, activation/deactivation, rename and deletion remain unqualified on this exact build. Fresh install, other ROMs/Android versions, camera scan, large text/TalkBack, Windows quoting and actual 16KB runtime are not newly qualified.

## Upgrade and connect

```sh
adb -s YOUR_SERIAL install -r 8bercue-0.19.1-beta.2-47.apk
```

Keep app data and Shell safety records; do not uninstall or clear records to bypass a signature or recovery problem. Open **Connect using computer**, then run the displayed command during its explicit connection window:

```sh
adb shell 'p=$(pm path dev.sid.fiveberinspector | head -n 1); CLASSPATH=${p#package:} app_process / dev.sid.fiveberinspector.Connect'
```

Use `adb -s YOUR_SERIAL shell` with several phones attached. No Python companion, Shizuku or root is required. An unknown result keeps changes paused even after observation is restored.

Production certificate SHA-256: `24aa7984b0d0977ff1e87b57beca91b2e36a05c04a5443f1a47aa23d90a6ef1c`. Corresponding source, notices, privacy, certificate and checksums are included. This is a limited Beta prerelease, not stable/production qualification.
