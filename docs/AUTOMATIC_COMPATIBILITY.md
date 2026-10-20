# Automatic card compatibility checks

The unpublished 0.18.1-rc10 / 32 candidate selects a connection from actual read-only observations. The release has no phone-brand, model or Android 16 admission list. Installation still requires Android 9 / API 28 and ARM64 because of the packaged Java/native implementation. That minimum does not promise a usable card channel.

## Selection and evidence

1. Enumerate registered Secure Element **AIDL HAL** instances. Load interface classes only from the trusted system SecureElement APK.
2. Check **HIDL HAL** instances declared in the platform-merged VINTF manifests. This does not depend on optional Java HIDL service-manager classes or guessed SIM1/SIM2 names. A declaration is not evidence that Shell can reach a service.
3. If neither HAL backend produced one verified card, check **Telephony**: require the implemented physical-slot/port Binder methods, enumerate present active slots/ports, and use the real Shell identity. The experimental fixed-AID case workaround remains necessary on the tested Xiaomi ROM.
4. If still unconnected, check **OMAPI**, retaining its normal card/app access rules.

For each reader, check service lookup, card presence, opening the fixed ISD-R management applet, complete GetEID response with bounded continuation, and closing only the owned channel. No profile mutation, download, notification HTTP, HAL initialization/reset or global communication-setting change is part of discovery. Interface methods including channel closure are checked before a HAL channel is opened.

Two verified readers in one backend block automatic selection. Invalid, duplicate or over-bound reader inventories also stop fallback; they are not mislabeled as permission denial. An uncertain/failed channel close stops all fallback; an active production transaction coordinator is quarantined. Discovery never selects a reader just because it is first, removable, called SIM1, or on a known phone. Every later operation independently rechecks full card identity and inventory.

HIDL transport in this build implements read-only GetEID/GetProfilesInfo/GetEuiccInfo2 commands. HIDL management writes are not implemented and are refused by operation admission. This is a transport implementation boundary, not a device-name gate. The AIDL and Telephony management protocols retain their existing scoped-operation restrictions.

## User-visible results

Home and Settings show the actual selected card channel, separately from the connection used to start the Shell service. **Compatibility check** displays each attempted backend/reader and its observed stage. A user can explicitly rerun the read-only check. Reconnection/lost Binder clears the old report.

Results distinguish missing accessible services, missing interface classes/methods, denied Shell access, no card, unavailable management applet/channel, invalid complete identity response, unreachable service, ambiguous cards and unconfirmed cleanup. A failed lookup cannot always distinguish a missing service from SELinux denial; the UI explicitly retains that uncertainty rather than inventing a cause.

The authenticated inventory response includes a schema-1 `compatibility` object: selected backend/reader, fixed-vocabulary attempts (`backend`, `reader`, `stage`, `condition`), details and a separate profile-read verification flag. It includes no card identity/hash, profile names/identifiers, activation data, APDUs or vendor exception text. Safe diagnostic export includes the same channel observations. Success verifies discovery and profile reading only, not all management operations or network service.

## Verification boundaries

Synthetic regressions cover backend order and early success, missing classes and denied access, preserving multiple failure reasons, no-card versus invalid response, ambiguity, no fallback after cleanup failure, coordinator quarantine, VINTF declaration formats versus compatibility matrices, XML entity rejection, fixed read-only HIDL commands and failed/lost callback handling. They also exercise admission across API 28–37 with no brand predicate. Physical-device results must be bound to the exact installed APK; the successful version-29 management cycle does not qualify version 31 writes.

## Interface references

- [AOSP Secure Element HIDL contract](https://android.googlesource.com/platform/hardware/interfaces/+/refs/heads/main/secure_element/1.0/ISecureElement.hal). The candidate never calls init/reset or replaces the system callback; unavailable/uninitialized services remain unavailable.
- [AOSP VINTF](https://source.android.com/docs/core/architecture/vintf): declarations describe interfaces, not authorization or working card routing.

## Physical candidate result

0.18.1-rc9 / 31, APK SHA-256 `e07490486bb24a7cf298ecb3941f843e67535b00438b604f1d3cb11b44ef5abc`, was upgraded with the existing official signer on local USB Xiaomi 2109119BC / mona, Android 13 / API 33, V816.0.2.0.TKVCNXM. The normal computer challenge and authenticated Binder bootstrap passed without Shizuku. Home/Settings displayed the selected Telephony channel, HAL failures and slot-specific results; Run read-only check was exercised.

AIDL enumeration found no accessible instances. VINTF declared HIDL SIM1/eSE1, but their service lookup failed; the API return does not by itself distinguish absence of a running service from blocked lookup. Telephony slot 0 / port 0 could not open ISD-R; slot 1 / port 0 verified the complete card and closed its channel. Native lpac and Java inventory both returned the same three profiles, with povo enabled and SMC/USIMSA disabled.

The complete inventory fingerprint matched the pre-upgrade baseline; operation and notification files were byte-identical. Default data remained subscription 2, SIM states LOADED,LOADED, UID 10262 and app data/history were retained. Temporary test input/helpers were removed. This candidate submitted no profile mutation, download or notification delivery. Prior version-29 write-cycle results remain separate.

296 synthetic compatibility/safety assertions passed; Release build and lint passed (0 errors, 59 warnings). APK metadata, official signature, native hashes and ELF/ZIP alignment passed. No generic Android-version, other-ROM, successful HIDL-hardware or new write qualification follows from these checks.

## Fresh-install candidate result (20 October 2026)

0.18.1-rc10 / 32, APK SHA-256 `a43a57f47432e1ff98aa6520f8d07235fab35d3a5540e8408530a0bf5e2d8af4`, was freshly installed on Redmi 26021RN18C / mist, Android 16 / API 36, HyperOS OS3.0.305.0.WPUCNXM, ARM64, 4 KB pages. The target initially had no application package. HyperOS rejected streamed ADB installation with INSTALL_FAILED_USER_RESTRICTED; opening the identical APK through the normal system package installer succeeded without changing USB-install settings. App UID is 10324. The installed APK was pulled back and its complete hash matched the delivered artifact.

The release no longer contains the Shizuku SDK/provider or a Shizuku UI/fallback. The normal Connect using computer challenge, matching bootstrap script and authenticated Shell UID2000 service passed. The sheet closed automatically. Actual automatic discovery selected AIDL HAL SIM1, verified a complete eUICC identity and closed the owned channel; SIM2 reported no card, while eSE1 could not be reached. HIDL, Telephony and OMAPI were not attempted after this verified HAL selection. No ROM/model-specific exception was needed.

Native lpac and Java independently read the same empty profile inventory (0 profiles, 0 enabled); complete card identity was stable before/after. An explicit UI read-only recheck and background/foreground return passed without a second bootstrap. Card information reported approximately 491 KB available. Home, Settings, compatibility details and setup help showed the actual route and the sole computer connection flow. Default-data subscription 1 and SIM states LOADED,ABSENT were unchanged. Temporary UI input and readback helpers were removed; the app and its live authenticated service were retained.

This qualifies installation, launch, connection, card recognition and reading an empty profile list on that exact APK/device. It does not qualify displaying populated profiles on this target, downloads, writes, switching, deletion, notification delivery, network registration, mobile data or reboot persistence. No activation credentials, card mutation or notification HTTP was submitted. Older-device write authorization was not transferred. The Xiaomi Android 13 / version-31 and version-29 results above remain separate.

The version-32 build passed 296 compatibility/safety assertions, Release compilation, lint (0 errors, 58 warnings), approved signing identity, native payload hash/ELF and APK ZIP alignment checks. A negative check rejected the previous SDK-bearing APK. The physical runtime here uses 4 KB pages; 16 KB alignment checks are not 16 KB-device runtime qualification.

### Subsequent authorized write cycle on the same APK

The owner then authorized a public disposable-profile cycle on this phone. Without changing APK 32 or its application source, the existing AIDL SIM1 route completed BetterRoaming installation, nickname change to AIDLTEST32, restoration of its original blank nickname, and deletion of only that disabled test profile. Each phase passed independent complete-identity/full-inventory readback. All four durable operations verified; installation notification 0 and deletion notification 1 were delivered. The original empty inventory was restored. The earlier read-only-only limits above describe that initial phase; activation, switching and network-service acceptance still remain untested. See [full write qualification](ANDROID16_PROFILE_CYCLE.md).

### Current APK on the Android 13 Telephony route

APK 32 also passed the selected Android 13 Xiaomi's normal ADB connection, automatic Telephony fallback, explicit read-only recheck and original three-profile read. SMC nickname change/restore and disposable public BetterRoaming install/nickname change/restore/delete all passed exact independent postflight; povo remained enabled. No application code was changed. Installation notification 68 remains delivery_unknown while deletion notification 69 delivered, so notification closure remains partial. See [the APK-bound Telephony record](ANDROID13_PROFILE_CYCLE.md#same-apk-telephony-regression-0181-rc10--32); older successful notifications do not qualify this attempt.
