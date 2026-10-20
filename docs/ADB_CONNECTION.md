# Computer connection

Unpublished candidate 0.19.0-rc7, versionCode 40. No public release is authorized.

## User flow

Open **Connect using computer**, copy the command, and run it while the visible two-minute timer is active. The terminal prints a confirmed connection or a fixed recovery message and nonzero status. An expired window requires **Start connection again**, then the same command. With more than one phone attached, select the intended serial using `adb -s SERIAL shell`.

```sh
adb shell 'p=$(pm path dev.sid.fiveberinspector | head -n 1); CLASSPATH=${p#package:} app_process / dev.sid.fiveberinspector.Connect'
```

This command resolves the package-manager-owned APK on the phone each time. It contains no installation-specific path, activation code or connection nonce, and downloads nothing. The current distribution is a single base APK. A future change to split distribution must review APK resolution and class loading.

## Startup and feedback

`Connect` runs as Shell UID 2000 in the main Android user. It verifies that CLASSPATH names the currently installed package APK, checks for a live authenticated session, and otherwise retrieves the App-created short-lived challenge. It starts the existing typed management service with nohup, detached input and a unique shell-private temporary output file. The foreground status client waits up to 25 seconds for the App provider to confirm a live accepted Binder. It does not infer success from a process name or launch exit status.

A successful or already-connected result is separate from card qualification. The App continues recovery and read-only capability checks; discovery, permissions, full card identity, explicit multi-card selection and a fresh profile read establish actual card access.

Errors are mapped to fixed messages; raw stack traces, nonces, identifiers and activation data are not printed. The temporary output file is removed when the foreground check ends. App feedback is limited to an allowlist of progress/failure codes and requires the same real Shell caller, live nonce, protocol and version. Feedback cannot assert success or establish a service. Expired, replaced and consumed challenges cannot change the connection status. An unconfirmed timeout does not kill the service or rule out a late handoff.

The existing artifact hash, Binder descriptor, App lifetime, one-use bootstrap and operation authorization checks remain in place. No TCP broker, root, Shizuku or downloaded companion is added. The legacy developer Python tool remains optional; the user flow has no Python dependency. Only connection entrypoints are changed, not profile lifecycle or native card components.

## Why the old command was confusing

The previous command embedded the installation directory and redirected both output streams to `/dev/null` while launching in the background. Its return status only established a launch attempt. On the Moto, repeating it with a newly opened connection window succeeded; the first attempt's error was not retained, so its exact cause remains unknown. This candidate exposes failures and tests window expiry directly instead of attributing the original failure to an unobserved cause.

Shizuku's older guide shows a stable external `start.sh` path; its current source generates a command from the native starter's installation path. A fixed-looking command is an entrypoint choice, not a different source of Android privileges:

- https://shizuku.rikka.app/guide/setup/
- https://github.com/RikkaApps/Shizuku/blob/master/manager/src/main/java/moe/shizuku/manager/starter/Starter.kt

## Qualification

Exact candidate APK, device, signer, observed terminal statuses, native screenshots and independent inventory comparisons accompany the private delivery. No existing Profile write is authorized for this connection refinement. Download, activation/deactivation, saved rename, notification delivery and deletion are not qualified by a successful connection test. Other ROMs, Android 13 and Windows shell quoting require separate live validation for this build.

### Moto result, 20 October 2026

Final APK `8bercue-0.19.0-rc7-40.apk`, SHA-256 `847dcb6be1e39cfaba57ffb1836c0a1667f350b790fc1de4ea491d0c5c3a26bf`, was upgraded with the existing production signer on the locally USB-connected Motorola edge 70, Android API 37. The pulled installed APK matched that exact hash. This is an upgrade/launch result; fresh installation is not requalified by this test.

- Missing App-created window: exit 1 and explicit instructions to open the connection page.
- Real two-minute expiry: exit 1; the App showed expiry, disabled copying and promoted Start connection again. Backgrounding and resuming retained expiry rather than resetting authorization.
- Normal on-phone retry and Copy ADB command: the same package-resolving command established an accepted live Binder in 1.154 seconds and exited 0. The connection page closed automatically.
- Repeating the command: exit 0 with already-connected status in 0.584 seconds; no new service was needed.
- Fresh App read: Secure Element SIM2, two profiles, one enabled. Independent native/Java pre/post snapshots matched the complete card identity and all profile fields, not only displayed names or counts. No profile mutation or notification HTTP was submitted.
- Build and lint passed, 0 errors / 60 warnings. 332 compatibility/safety, 68 UI/command and 34 actual-provider-with-simulated-Binder assertions passed (434 total). All eight native payload/manifest entries were byte-identical to the pre-upgrade APK.

The phone switched to ChatGPT during the first UI expiry inspection; those interrupted snapshots and subsequent unconnected attempts were excluded from success evidence. UI navigation then used fresh unique snapshot files and a foreground-package check before input. No unrelated application was clicked. The final expiry, retry, connection, read and complete inventory comparison above belong to versionCode 40.
