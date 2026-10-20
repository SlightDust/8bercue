# 8bercue 0.19.1 Beta 1

Simplified Chinese and read-only recovery for an unresolved card operation. This is a **Beta prerelease**, not full stable/production qualification.

APK: `0.19.1-beta.1` · versionCode **46** · ARM64 Android 9+ installation. Card access remains capability-dependent.

## Changes

- Add Simplified Chinese throughout the app, including connection, safety/recovery, Profile actions, QR/camera, accessibility labels and a local privacy translation. Settings → Language offers **Follow system**, **简体中文** and **English**. System language, operator/Profile names, identifiers and the copied ADB command are unchanged.
- Separate read-only recovery from permission to modify profiles. Computer reconnection can retire verified old 8bercue backends, confirm exclusive access, recheck the same full card identity and compare two inventories. The quarantine marker, original unknown transaction and write block remain intact. No operation is replayed.
- Explain paused access, changed cards, unstable inventory and unverifiable recovery evidence, instead of treating these conditions as an unavailable card. A failed verification no longer claims the card was read successfully.
- Preserve real Binder caller authentication across nested journal checks while HAL work executes as Shell. Explicit recovery windows retain artifact verification and single-use handoff; an already connected ordinary rerun remains idempotent.

- Include the already-merged privacy-first feedback/export feature, with Chinese UI. Crash reporting remains off by default. This APK has no reporting DSN: remote submission is unavailable; manual email/export remain available. No feedback is sent during device validation.

## Validation at publication

Source review, release build/lint, 35 recovery assertions, 39 actual-provider authentication/state assertions, 332 compatibility/safety assertions, 68 UI/command assertions and 477-pair localization coverage passed. Lint findings are disclosed in the readiness record. Signing and corresponding-source payload verification accompany the Release.

**The owner requested publication before testing this exact APK on the phone.** Version-46 device installation, connection, fresh card/Profile reading, recovery and language UI results are pending at publication; any later result must identify this downloaded APK and device. Earlier candidates do not qualify this release. No exact-build Profile writes have been tested or authorized in this validation round.

Activation/deactivation, rename, download, notification delivery/cleanup and deletion remain experimental and unqualified on APK46. Fresh install, Android 13/other-ROM runtime, Android 9–12 language fallback, camera permission/scan, TalkBack/large-font behavior, Windows terminal quoting and actual 16KB-page runtime are not newly qualified. Card-enabled state is not evidence of network registration or data service.

## Install and connect

The existing production certificate is retained: SHA-256 `24aa7984b0d0977ff1e87b57beca91b2e36a05c04a5443f1a47aa23d90a6ef1c`.

```sh
adb -s YOUR_SERIAL install -r 8bercue-0.19.1-beta.1-46.apk
```

Keep application data and Shell safety records. A signing conflict requires a reviewed migration; do not uninstall or clear data merely to upgrade.

Open **Connect using computer** and run the displayed command during its two-minute window:

```sh
adb shell 'p=$(pm path dev.sid.fiveberinspector | head -n 1); CLASSPATH=${p#package:} app_process / dev.sid.fiveberinspector.Connect'
```

Use `adb -s YOUR_SERIAL shell` when several phones are attached. No Python companion, Shizuku or root is required. Reconnect after app updates, reboot or helper termination. An unknown result keeps changes paused even after read-only access is restored. See [recovery behavior](https://github.com/sidnvy/8bercue/blob/main/docs/READ_ONLY_RECOVERY.md).

Corresponding source, dependency sources/patches, notices, privacy, public certificate and checksums are included. Private keys, credentials and private device evidence are excluded. The existing v0.18.0-beta.1 and v0.19.0-beta.1 assets are preserved. Report redacted issues through GitHub Issues or support@sidnvy.com.
