# Rootless Android 13 investigation

This record covers the owner-authorized, read-only investigation on 2109119BC / mona, Android 13 / API 33, V816.0.2.0.TKVCNXM. No root, ROM/security-policy changes, Shizuku, LPA identity impersonation, card writes or third-party APK installation were used.

## Other managers

| Project and inspected revision | Relevant mechanism |
| --- | --- |
| [OpenEUICC / EasyEUICC](https://gitea.angry.im/PeterCxy/OpenEUICC/src/commit/89d677e07d0ebbd2d157ede2cb31f76aec737e3a/README.md) | OpenEUICC uses privileged system installation; EasyEUICC uses OMAPI and requires the card to authorize its signing certificate. USB CCID is a separate hardware path. |
| [HyperLPA](https://github.com/FreeTeaspoon/HyperLPA/blob/afd4821e16c8ed752dfe73ad6d2f2e0480641cd4/README.md) | OMAPI/card certificate rules, trusted bridge providers or external readers. Its ordinary APK does not gain privileged telephony access just by installation. |
| [NekokoLPA2](https://github.com/iebb/NekokoLPA2) and [OTBridge](https://github.com/iebb/OTBridge/blob/90682357afe4d722d7ab130a3df45c3b2b1b50e5/README.md) | The bridge is installed with system privileges through a Magisk/KernelSU module; a normal client app does not make the bridge rootless. |
| [openeuicc-bridge](https://github.com/Laiteux/openeuicc-bridge) | Adds an ADB-facing provider around an already-compatible LPA/card combination; this does not confer new card permissions. |

The installed Qualcomm UIM LPA also declares a signature/privileged service permission. The ordinary platform EuiccService query returned no service on this ROM. Neither observation was substituted for a working card read.

## The route actually found

[AOSP Android 13 PhoneInterfaceManager](https://android.googlesource.com/platform/packages/services/Telephony/+/refs/tags/android-13.0.0_r1/src/com/android/phone/PhoneInterfaceManager.java#5204) first checks the package against the calling UID and requires appropriate telephony permissions. It then recognizes the special ISD-R AID with a case-sensitive string comparison. Equivalent lowercase hex still encodes the same bytes at the modem/card boundary.

A fixed read-only probe compared the uppercase request, which was denied, with the equivalent lowercase request, which opened ISD-R in physical slot 1 / port 0. GetEID returned success and the owned channel closed. Slot 0 returned no matching applet and was not selected by position alone.

This is a policy-check workaround relying on a framework defect, not a new Android permission, an upstream manager feature, or supported behavior promised across devices. A case-insensitive or byte-based check in another ROM can close this route.

## Candidate restrictions

The candidate applies this encoding only to the fixed ISD-R AID, while retaining com.android.shell as the real caller package. It inventories active physical slots/ports, verifies complete GetEID identity, refuses ambiguity, and uses independent readback paths.

The fallback cannot issue profile changes: previews reject the reader, write bridges reject it, and its APDU policy independently rejects every non-allowlisted command. It offers no basic-channel, arbitrary SELECT, arbitrary AID or raw APDU RPC.

The formally signed version 25 candidate was installed and connected through normal UI. It displayed three profiles; independent native/Java reads agreed on count, masked identifiers and states, with complete card identity stable before/after. No write outcome is claimed. Further write qualification requires explicit device/card/profile authorization and a separate implementation and test review.
