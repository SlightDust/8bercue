# 8bercue 0.19.0 Beta 1

A new card-focused interface, capability-based connection checks and one-command computer setup. This is a **Beta prerelease**, not a stable or universally compatible release.

APK version: `0.19.0-beta.1` · versionCode `41` · ARM64 Android 9+ installation.

## Changes

- Manage one selected eUICC from the home screen. Settings stays in the top-right app bar, without bottom navigation.
- Open a Profile for its on/off switch, Rename and Delete actions. Installation/deletion retain confirmation; enabled Profiles cannot be deleted.
- Probe usable Secure Element HAL channels first, then Telephony, with OMAPI as another fallback. Compatibility check explains the selected route and unavailable conditions; there is no phone-brand or Android 16 admission list. Actual card access still depends on system interfaces, permissions and the card.
- Copy one fixed ADB command. It resolves the installed APK automatically, reports a confirmed connection or a recovery message, and detects an already connected session. No downloaded Python script, Shizuku or root is required.
- Show the two-minute connection timer and a direct retry action. This release **retains** the two-minute App-created authorization window. A page-scoped replacement was discussed but is not implemented.
- Preserve full card identity binding, explicit operation intent, one-use execution confirmation, transaction journals, unknown-result recovery and deletion safeguards.

## Validation and limits

This exact APK received a same-production-signer upgrade, launch, connection-window/error/retry checks, a confirmed Shell-to-App handoff and a fresh HAL SIM2 Profile read on a locally USB-connected Motorola edge 70, Android API 37. Independent native/Java pre/post readbacks compared complete card and Profile fields; both existing Profiles and their enabled state were unchanged.

Release build/lint and 434 offline assertions passed. The signer, release surface, metadata, native payload and 16KB ZIP alignment were checked. The included readiness record distinguishes exact-build checks from untested functions.

**No Profile writes were submitted on this APK.** Download, activation/deactivation, saved nickname changes, notification delivery/cleanup and deletion remain experimental and are not newly qualified by this release. Earlier Xiaomi/Pixel/Moto write tests are documented against their earlier APKs; they do not count as version 41 write acceptance. This exact build has not received a fresh-install test, Android 13 live qualification, other-ROM qualification, a Windows terminal-quoting test or actual 16KB-page runtime testing. Card-enabled state does not establish network registration, data, calls or SMS service.

If a result is unknown, stop further writes and use recovery verification. Do not resubmit an activation code or erase transaction records merely to retry.

## Install and connect

Download the APK from this Release. It uses the existing production identity (certificate SHA-256 `24aa7984b0d0977ff1e87b57beca91b2e36a05c04a5443f1a47aa23d90a6ef1c`). Same-signer installs can upgrade with:

```sh
adb -s YOUR_SERIAL install -r 8bercue-0.19.0-beta.1-41.apk
```

If Android reports a signing conflict, preserve app/Shell records and review migration before uninstalling or clearing data. This release does not require those destructive steps for a same-signer upgrade.

Open **Connect using computer**, copy the command and run it while the timer is active:

```sh
adb shell 'p=$(pm path dev.sid.fiveberinspector | head -n 1); CLASSPATH=${p#package:} app_process / dev.sid.fiveberinspector.Connect'
```

With multiple devices attached, use `adb -s YOUR_SERIAL shell`. The terminal must confirm connection; then check the selected card and a fresh Profile list. If the window expires, choose **Start connection again** and rerun the same command. Reconnect after a reboot, app update or service termination.

## Source, privacy and feedback

Complete corresponding source, build scripts, dependency sources/patches, notices, the public certificate and checksums accompany the APK. Original code is AGPL-3.0-only; third-party terms remain in force. Private signing material, activation credentials and private device evidence are excluded. The previous v0.18.0-beta.1 tag and assets are preserved.

Publisher: sid (independent project; not affiliated with 5ber). Report issues through GitHub Issues or support@sidnvy.com. Include app/device/Android versions and redacted steps. Do not post activation/confirmation codes, full EID/ICCID, phone numbers or raw protocol logs. See `PRIVACY.txt` and `LICENSING.md`.
