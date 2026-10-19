# Device compatibility

Installing the APK and accessing an eUICC are separate requirements. Minimum SDK is 28 (Android 9), with ARM64 native components built for API 28. Compile/target SDK remains 36.

The published v0.18.0-beta.1 APK (0.18.0-rc1 / 22) rejects management below API 36. The unpublished 0.18.1-rc3 / 25 candidate checks actual capabilities instead and includes an **experimental, read-only Telephony fallback**. This is not general Android 13 qualification.

## Transports

1. Prefer registered hardware AIDL instances using interface classes from the trusted system SecureElement APK.
2. Otherwise try framework OMAPI, retaining its applet access rules.
3. If no card was verified through that preferred backend, try the experimental Shell Telephony backend. Never hide an ambiguous selection or failed channel cleanup by falling back.

Every backend retains caller authentication, complete EID identity binding and bounded APDU policies. Reader names include the transport namespace. A backend change invalidates an existing management selection. No path initializes/resets the HAL or closes another client's channels.

The fallback depends on an Android framework **security-check defect**: a case-sensitive comparison of the ISD-R hex string, despite case-insensitive interpretation of the encoded bytes. It uses the real Shell UID and package, with the existing MODIFY_PHONE_STATE permission. It does not impersonate the system LPA. A patched ROM may reject this path. See [rootless investigation](ROOTLESS_ANDROID13.md).

This backend is intentionally read-only. Write previews and mutation transports reject its reader namespace, and a final transport policy permits only GetEID, GetProfilesInfo, optional GetEuiccInfo2 and bounded conditional GET RESPONSE. Lifecycle changes, downloads and notification removal are blocked.

## Actual Android 13 result

Device: Xiaomi 2109119BC / mona, ROM V816.0.2.0.TKVCNXM, Android 13 / API 33, ARM64. Final tested APK: 0.18.1-rc3 / 25, SHA-256 97c465ab293eef85f5256d8c074b697f977ce71f8bd00a2d9e5148c97a267798.

| Gate | Actual result |
| --- | --- |
| Same-signer installation and normal launch | Passed; app data and UID preserved. |
| Normal computer connection | Passed, with one ADB bootstrap and no Shizuku. |
| Complete card identity | GetEID verified before/after independent inventory. |
| Profile list | 3 profiles, 1 enabled and 2 disabled, visible in normal UI. Native lpac/libeuicc and Java reads agreed on count, masked identifiers and states. |
| Writes | None attempted; blocked in this experimental backend. |
| Download / notification / deletion closure | Unverified. |
| Other Android 13 phones or patched ROMs | Unverified. |

The earlier version 24 candidate could not read the card: HIDL lookup was denied by SELinux, OMAPI rejected ISD-R with No ARF exists, and the uppercase Telephony request was denied as non-LPA. Those observations remain valid; version 25 adds the separately verified encoding workaround.

ADB starts the authenticated Shell companion after **Connect using computer** is selected. Shizuku is optional and was not installed or used in this test. Reconnect after reboot, app upgrade or companion termination. The phone's existing conservative recovery flag was preserved; successful reading is not permission to clear an unknown operation or enable writes.

Run the public synthetic regressions:

    bash android-app/tools/run_compatibility_tests.sh

They do not replace actual-device qualification. Old Moto or Beta results do not qualify this APK.
