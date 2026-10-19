# Rootless Android 13 investigation

This record begins with the owner-authorized, read-only investigation on 2109119BC / mona, Android 13 / API 33, V816.0.2.0.TKVCNXM. No root, ROM/security-policy changes, Shizuku, LPA identity impersonation, card writes or third-party APK installation were used.

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

Version 25 is strictly read-only. Version 26 adds a separately scoped, exact nickname-only transport for disabled profiles; all other mutations remain blocked. Neither version offers basic-channel, arbitrary SELECT, arbitrary AID or raw APDU RPC.

The formally signed version 25 candidate was installed and connected through normal UI. It displayed three profiles; independent native/Java reads agreed on count, masked identifiers and states, with complete card identity stable before/after. No write outcome is claimed. Further write qualification requires explicit device/card/profile authorization and a separate implementation and test review.

## Explicitly authorized nickname qualification: version 26

The owner authorized only the disabled SMC profile (masked ICCID 8985...5339), with enabled povo and every other profile protected. The original nickname was empty; SMC was the provider label. Before writing, a read-only helper saved the full card identity and every profile field in a mode-0700 Shell-private directory with a mode-0600 snapshot. No raw identifiers were included in the public evidence.

The formally signed 0.18.1-rc4 / 26 APK was installed with data and UID preserved. The normal UI prepared and confirmed SMC A13 test once, then independently verified it. A second normal UI preview explicitly removed that nickname, returning it to empty. Separate read-only checks compared the complete card identity, full identifiers, names, providers, nicknames and states: only the intended nickname changed in the intermediate snapshot, and the final snapshot matched the original exactly. Both fsynced transaction records are verified. Native libeuicc/lpac and independent Java inventory reads also passed on version 26.

No activation code, enable/disable/delete command, download, notification cleanup, reboot or communication-setting change was submitted. ADB started the authenticated Shell companion; Shizuku was not used. Temporary UHID input drove the ordinary UI using existing Shell permissions and was closed after the test. This input method is separate from the Telephony applet access workaround.

## Scoped writes and the single-channel boundary: versions 27–29

The owner later approved disposable public-profile installation/deletion and temporary switching away from povo followed by restoration. A Shell-private restriction file binds the complete card, full original-profile metadata and one independently verified newly installed profile. Original-profile rename/deletion and unrelated writes are refused.

Version 27 installed a disabled BetterRoaming profile with exact independent readback. Its notification reached operator acknowledgment, but an attempted second ISD-R channel blocked pre-removal card verification. A read-only diagnostic and a dry run denied at the Binder boundary isolated that failure without removing a notification. Version 28 closes the owned read channel before full identity discovery and reopens an exact acknowledged-removal channel; the actual notification completed.

Version 28 deleted only that inactive disposable profile and restored the exact original inventory. A fixed read-only metadata query then found its deletion notification still pending. Version 29 supports the separately signed deletion envelope, binds new notification metadata to a verified deletion and its approved installation receipt, posts once and removes only after acknowledgment. Operator-assisted recovery of that exact pre-existing deletion notification completed on version 29, without changing any profile.

Public directory and current official activation entries can be refused at AuthenticateClient even after a previous successful installation. The client must preserve remote uncertainty and cannot infer reusable-code availability from a public listing. Each actual result belongs to its exact APK and target device; see COMPATIBILITY.md and ANDROID13_PROFILE_CYCLE.md.
