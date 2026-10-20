# Android 16 AIDL profile qualification

Target: Redmi 26021RN18C / mist, Android 16 / API 36, HyperOS OS3.0.305.0.WPUCNXM, ARM64, 4 KB pages. Tests ran only on the explicitly selected local USB target. The owner authorized installing a public disposable eSIM, renaming it and deleting that newly installed profile. The card's fresh independent baseline was empty. No previous phone's profile authorization was reused.

All operations used the existing officially signed 0.18.1-rc10 / versionCode 32 APK, SHA-256 `a43a57f47432e1ff98aa6520f8d07235fab35d3a5540e8408530a0bf5e2d8af4`, application UID 10324. Its production source is commit `c15f5499ad507d3e63505ed0afeda11f9a25b385`. No application code, APK or ROM-specific exception was added for this phone or write cycle. The installed APK was pulled back and hashed before and after; both matched. The prior single ADB bootstrap remained sufficient: its authenticated Shell service was still alive. No new bootstrap was needed for these writes.

Automatic discovery selected **Secure Element AIDL HAL / SIM1**. Complete GetEID identity checks, exact inventory binding, one-use confirmations, scoped commands and durable operation/notification journals remained active. HIDL, Telephony and OMAPI were not selected; the Android 13 Telephony case-comparison workaround was not used. HAL presence by itself is not the qualification: actual card commands and independent postflight reads passed here.

## Normal UI cycle (20 October 2026)

The test asset came from the current public [BetterRoaming official installation page](https://www.betterroaming.com/esim-installation/). The owner allowed Speedtest or a comparable public test eSIM. Speedtest was not retried following historical server refusals on another device. No private/commercial activation credential, purchase, account signup or data-plan activation was involved. The profile stayed disabled throughout.

| Confirmed UI action | Durable transaction | Independently observed result |
| --- | --- | --- |
| Install public BetterRoaming profile once | `8c382957-b08a-49a6-9caf-47395d4fd9ec` | Verified; exactly one new disabled profile `8944...6144`, initial nickname empty. Native exit 0, HTTP 200 / Executed-Success; native and worker stopped. Installation notification sequence 0 delivered. |
| Rename to `AIDLTEST32` | `06331602-bda7-4f2e-9335-55d991cf1e25` | Verified; complete inventory differed only in the exact nickname; same card/profile, still disabled. |
| Restore original empty nickname | `bff9265b-30db-4168-a581-e6bf14f6284a` | Verified; full inventory fingerprint exactly matched the installed baseline. |
| Delete only that disabled disposable profile | `e4b3c5e5-c45c-474f-a460-eab68eff5cbb` | Verified; original empty inventory restored. Deletion notification sequence 1 was bound to this transaction/profile/recipient and delivered. |

At each completed phase, a separate Shell process read native lpac and Java inventories and checked complete card identity before/after, full ICCIDs, nickname, provider/name and enabled states. Full identifiers and snapshots remained private on the phone. Exported evidence uses only masked identifiers, counts and inventory hashes. Final native/Java counts were both 0, with the same empty-inventory fingerprint as before installation. All four durable transactions are verified; both notification records are delivered. No unknown operation was retried or cleared.

The temporary evidence helper initially called an unavailable Java Files.readString method. It was corrected to Files.readAllBytes; no APK change or repeated download was involved. UI automation also needed a normal text accessibility action to avoid the phone's Chinese input-method composition. Text entry affected the visible nickname field only; preview and confirmation still ran through the normal app UI. Standard temporary HID input operated buttons. A standard wake-only HID key and normal dismissal of the password-free keyguard resumed the phone after sleep. These are operator test tools, not runtime dependencies or changes to card access. Temporary helpers, input devices and private baseline files were removed after verified deletion. Application data, live companion, operation history and notification records were retained.

## Boundaries

Default-data subscription remained 1 and SIM states remained LOADED,ABSENT. There was no root, Shizuku, ROM/SELinux change, reboot, flight-mode change or global communication-setting change. There was no enable/disable, switching, network registration or mobile-data test. Those remain unqualified on this exact device/APK. Arbitrary operator downloads, interrupted-operation recovery, reboot persistence, 16 KB-device runtime and other ROMs are also not established by this cycle. Older Android 13/Moto/Pixel results remain separate.

The candidate's previous 296 synthetic assertions, Release build and lint (0 errors, 58 warnings), signature and native/ZIP checks were not rerun for this documentation-only qualification: application code did not change. This cycle adds physical evidence, not a new APK or public release.
