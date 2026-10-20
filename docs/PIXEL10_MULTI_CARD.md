# Pixel 10 Pro XL: explicit card choice and removable-eUICC cycle

## Exact candidate and target

On 20 October 2026, the owner authorized tests on a locally attached Pixel 10 Pro XL / mustang with a different removable eUICC (manufacturer not confirmed). The phone reports Android 17 / API 37, build CP2A.260805.005, ARM64 and 4096-byte pages. This is not an Android 13 qualification result.

The installed candidate is **0.18.1-rc11 / versionCode 33**, APK SHA-256 `8039d70ed62af419a916066a568bc1915e8df31ef13d207a3ad630c87d824149`. Production source is `03d1e6f8d0964a190786bfed95fbcb7f7cc9a0bf`. It uses the existing official signer, SHA-256 `24aa7984b0d0977ff1e87b57beca91b2e36a05c04a5443f1a47aa23d90a6ef1c`. Earlier APKs' physical results are separate.

## Installation and connection

The existing 0.17.1 / 20 APK used an unrelated debug certificate and was registered in both the owner and Private space. Before removal, the old APK, owner app-private data and Shell safety history were backed up and structurally/hash checked. The private-space registration reported never launched and no allocated app data directories; the space was not unlocked or started. The owner separately approved removing that app registration as well as the owner installation. Only this old package was removed. The official candidate was installed for owner user 0; no other app or private-space data was removed. App preferences reset; no unsupported private-data restore or downgrade rollback was promised.

The installed APK was pulled back and matched the candidate hash. Shell operation/notification records were byte-identical immediately after migration. The normal **Connect using computer** challenge and one ADB bootstrap attached the authenticated Shell UID2000 service. No root command, ROM change, Shizuku, reboot, flight-mode change or global communication-setting change was used.

## Actual card routing

The normal installed service reached two cards through **AIDL HAL**. Automatic selection stopped with an explicit multiple-eUICC error; it did not guess a slot. **Choose card** showed reader and masked EID suffix:

| Reader | Complete-identity mapping verified independently | Protected original profiles |
|---|---|---|
| AIDL SIM1 / EID 8908…7857 | Removable card; Telephony physical slot 1 / port 0 maps to the same full identity | Tigo 8950…5293, disabled, blank nickname |
| AIDL SIM2 / EID …9132 | Built-in card; Telephony physical slot 0 / port 1 maps to the same full identity | Skylo, giffgaff, YES, povo, Club; all disabled |

The mappings above come from full-identity comparisons and observed framework slot metadata; the product does not infer them from SIM1/SIM2 names. The removable card was explicitly selected in the normal UI. An expired 90-second choice was first rejected without a write or alternative selection; a fresh choice succeeded. The UI then displayed AIDL / SIM1 and the fresh Tigo inventory. After the write cycle, an explicit read-only choice of SIM2 also displayed the built-in five-profile inventory through the same live Shell service. No profile activation belongs to this card-choice test. Read-only recovery verification and card information passed; the card reported approximately 226 KB free before installation.

A preliminary temporary Java probe had reported AIDL failures and found both cards through Telephony. That preliminary context did not establish an OS permission limit. The installed service and later Looper-initialized readbacks actually used AIDL. Initial native readback also encountered a legacy driver still staged on the phone; it was not accepted as current-APK native evidence. The application/current readback deployed and verified all six native file hashes against APK 33 before the qualified baseline and writes below.

## Authorized write cycle

All actions went through normal UI preview and one-use confirmation. Operator helpers only inspected UI or independently read cards; they submitted no card mutation or HTTP request. The public reusable BetterRoaming test entry was used once. No private activation code was used. The new profile stayed disabled throughout; no existing profile was enabled, renamed or deleted.

| Action | Durable transaction | Result |
|---|---|---|
| Install public BetterRoaming 8944…5589 | `1b67c1ff-2d88-4014-af04-0c827c344b1f` | Verified; exactly one new disabled profile, Tigo unchanged |
| Rename new profile to PIXELTEST33 | `88bafc97-91a1-45ea-be01-3f8f7604b561` | Verified; only the exact new nickname changed |
| Restore original blank nickname | `3f17d680-4308-4155-8142-34c6206fe034` | Verified; complete installed inventory restored |
| Delete only the disabled new profile | `a06be0de-c6f2-4c13-a6ea-bb951a18295c` | Verified; original Tigo inventory restored |

Each phase independently compared full card identity and every profile field/state, with native/Java inventory agreement. The built-in five-profile inventory was independently checked after every phase as well. Its fingerprint remained `8236f0e1e5df43ac809d9bfd2d4b74e4a7d9ad404c3aba12073690c0d9691507`; final removable-card fingerprint returned to `c3f6bf68fb4e6489be03a894407ce040a0d9769176a1f7ee9a0683dce16d8808`. No test profile remains. Default-data subscription stayed 19, SIM states stayed LOADED,NOT_READY and the current user stayed 0.

## New notification closure

Installation receipt **2** and deletion receipt **3** reached `delivered` in durable, full-card/transaction-bound records. Independent read-only card metadata showed both sequences absent afterwards. The normal UI reported operator acknowledgement. Card receipts 0 and 1, observed after installation, remained present and were not submitted for delivery or removal. The old local delivered record 32 was preserved separately; it does not qualify either new receipt.

This verifies the successful acknowledgement/cleanup path for these two new receipts. It does not inject an HTTP acknowledgement loss, app/service crash, power loss or interrupted cleanup on physical hardware. Synthetic regressions cover those safety decisions. The Android 13 Xiaomi's unknown installation receipt 68 remains a separate unresolved event; this Pixel result neither replays it nor establishes its outcome.

## Boundaries and checks

Candidate 33 passed 332 synthetic compatibility/safety assertions, Release build and lint (0 errors, 59 warnings), approved signature/metadata and release-surface checks, all six native payload hashes/ELF alignment, and 16 KB APK ZIP alignment. Alignment checks do not qualify a 16 KB-page device. No public release, tag change or upload was performed.

Installation, launch, authenticated connection, explicit card recognition, populated profile reading, the four authorized writes and two new successful receipt loops passed on this exact Pixel/card/APK. Profile activation/switching, network registration/mobile data, reboot persistence, physical card-swap handling, successful HIDL hardware access and other ROMs/APKs remain untested here. Multiple-card and changed-identity refusal are also covered by synthetic tests; there is no brand/version admission list. APK 33 has not yet been physically regressed on the Android 13 Xiaomi.
