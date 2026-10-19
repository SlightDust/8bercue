# Device compatibility

Installing the APK and accessing an eUICC are separate requirements. Minimum SDK is 28 (Android 9), with ARM64 native components built for API 28. Compile/target SDK remains 36.

The published v0.18.0-beta.1 APK (0.18.0-rc1 / 22) rejects management below API 36. The unpublished 0.18.1-rc4 / 26 candidate checks actual capabilities instead and includes an **experimental Telephony fallback with disabled-profile nickname changes**. This is not general Android 13 qualification.

## Transports

1. Prefer registered hardware AIDL instances using interface classes from the trusted system SecureElement APK.
2. Otherwise try framework OMAPI, retaining its applet access rules.
3. If no card was verified through that preferred backend, try the experimental Shell Telephony backend. Never hide an ambiguous selection or failed channel cleanup by falling back.

Every backend retains caller authentication, complete EID identity binding and bounded APDU policies. Reader names include the transport namespace. A backend change invalidates an existing management selection. No path initializes/resets the HAL or closes another client's channels.

The fallback depends on an Android framework **security-check defect**: a case-sensitive comparison of the ISD-R hex string, despite case-insensitive interpretation of the encoded bytes. It uses the real Shell UID and package, with the existing MODIFY_PHONE_STATE permission. It does not impersonate the system LPA. A patched ROM may reject this path. See [rootless investigation](ROOTLESS_ANDROID13.md).

Default Telephony channels remain read-only: GetEID, GetProfilesInfo, optional GetEuiccInfo2 and bounded conditional GET RESPONSE. A separate lifecycle connection accepts exactly one confirmed nickname command for one fully identified, disabled profile. The nickname command is consumed before the Binder call; an uncertain response cannot trigger a retry. Confirmation, full inventory binding, durable operation records and independent postflight checks remain in place. Enable, disable, delete, download and notification cleanup remain blocked, including at the final transport boundary. Failed mutation-channel cleanup quarantines further transactions.

## Actual Android 13 result

Device: Xiaomi 2109119BC / mona, ROM V816.0.2.0.TKVCNXM, Android 13 / API 33, ARM64. Final tested APK: 0.18.1-rc4 / 26, SHA-256 c5e6fe33e4d696466b11a33f5c359799f5fda8997b6b3b388d4c17aa0c377b38.

| Gate | Actual result |
| --- | --- |
| Same-signer installation and normal launch | Passed; app data and UID preserved. |
| Normal computer connection | Passed, with one ADB bootstrap and no Shizuku. |
| Complete card identity | GetEID verified before/after independent inventory. |
| Profile list | 3 profiles, 1 enabled and 2 disabled, visible in normal UI. Native lpac/libeuicc and Java reads agreed on count, masked identifiers and states. |
| Writes | Normal UI renamed disabled SMC to SMC A13 test, then restored its original empty nickname. Both durable transactions are verified. Independent complete inventory comparison confirmed all original fields/states restored, povo enabled throughout and every other profile unchanged. |
| Download / notification / deletion closure | Unverified. |
| Other Android 13 phones or patched ROMs | Unverified. |

The earlier version 24 candidate could not read the card: HIDL lookup was denied by SELinux, OMAPI rejected ISD-R with No ARF exists, and the uppercase Telephony request was denied as non-LPA. Those observations remain valid; version 25 adds the separately verified encoding workaround.

ADB starts the authenticated Shell companion after **Connect using computer** is selected. Shizuku is optional and was not installed or used in this test. Reconnect after reboot, app upgrade or companion termination. The existing recovery flag was cleared only through the normal read-only Verify card and recovery flow after its durable journal reported idle; no unknown transaction was discarded. MIUI denied SDK input injection, so temporary standard UHID touch/keyboard devices drove the normal UI without changing settings. They were removed after testing.

Run the public synthetic regressions:

    bash android-app/tools/run_compatibility_tests.sh

They do not replace actual-device qualification. Old Moto or Beta results do not qualify this APK.

## Version admission versus actual access

- APK and native payload minimum: Android 9 / API 28, ARM64. Target/compile SDK stays 36.
- Public [OMAPI SEService](https://developer.android.com/reference/android/se/omapi/SEService) starts at API 28.
- Public [Telephony logical channels](https://developer.android.com/reference/android/telephony/TelephonyManager#iccOpenLogicalChannel(java.lang.String)) start at API 21; the P2 overload starts at API 26. These older subscription APIs are not the physical-slot/port backend implemented here and do not grant applet access by themselves.
- The current fallback probes the Android 13 [IccLogicalChannelRequest and physical-port Binder API](https://android.googlesource.com/platform/frameworks/base/+/refs/tags/android-13.0.0_r1/telephony/java/com/android/internal/telephony/ITelephony.aidl). It fails closed if those exact classes/methods or permissions are absent. Android 9–12 Telephony variants have not been implemented or tested.
- The original hardware [Secure Element AIDL is already present in AOSP Android 14](https://android.googlesource.com/platform/hardware/interfaces/+/refs/tags/android-14.0.0_r1/secure_element/aidl/android/hardware/secure_element/ISecureElement.aidl). It is a vendor HAL contract, not a public SDK guarantee or an Android 16 requirement. Android 13 on this phone exposes HIDL instead; its Shell lookup is denied.

One adapter can serve multiple ROMs exposing the same interface and access behavior; this does not require a ROM-name branch for every device. Each claimed device/ROM still needs actual qualification. ROMs that patch the ISD-R comparison or deny Shell access may have no usable rootless route under these constraints. No further lowering below API 28 is claimed without rebuilding native components, auditing APIs and testing older devices.
