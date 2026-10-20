# Android 13 disposable profile qualification

Target: local USB Xiaomi 2109119BC / mona, Android 13 / API 33, V816.0.2.0.TKVCNXM. No other phone was operated. This record does not inherit Moto authorization or acceptance.

The owner approved a disposable public Speedtest/BetterRoaming asset, including temporary activation followed by povo restoration, and deletion of the newly installed test profile only. Original povo, SMC and USIMSA metadata must remain unchanged. Povo may be inactive only during the explicitly authorized temporary switch. No commercial/private activation code, account signup, purchase or global communication change is authorized by this test.

The original three-profile baseline, full card identity and all profile fields are compared inside the Shell process. Public evidence contains only masked identifiers, counts and booleans. An expiring mode-0600 Shell-owned restriction file refuses unrelated lifecycle actions and downloads outside the chosen exact public entry. Each high-level action still needs normal application confirmation.

## Evidence before final cycle

- Version 26: inactive SMC nickname change/restore passed; original nickname was empty.
- Version 27: public Speedtest was refused by the operator at AuthenticateClient with HTTP 200 / Failed / 8.2.6 / 3.8. A different public BetterRoaming entry installed one disabled profile, masked 8944...6127; the original three were unchanged.
- Version 28: installation notification 62 completed through reviewed cleanup after the one-channel fix. The inactive disposable profile was deleted and the exact original baseline restored. This deletion occurred before switch verification, which left no disposable profile when later public entries were refused. No switch result is inferred from these operations.
- Version 29: repaired only the exact deletion notification 63 bound to that verified deletion and approved install recipient. It reached delivered; full original inventory remained unchanged and Android's povo subscription matched.

Old refused download records and immutable history are preserved. Separate reviewed-abandonment resolutions record unchanged card inventory, stopped native workers and the operator decision to use a different public test entry. They retain unknown remote-code outcome and do not authorize a retry of the refused entry.

Current code includes staged ES10b downloads, exact one-use lifecycle commands, read-only notification metadata, and sequence/operation/profile/recipient-bound install/deletion notifications. Acknowledgment loss preserves delivery_unknown; reviewed cleanup does not post HTTP again. Single-channel, card-swap, malformed-boundary and protected-asset regressions exercise the production paths.

## Sources

- [Public test-profile directory](https://euicc-manual.osmocom.org/docs/rsp/known-test-profile/). Availability claims in a directory do not override the actual SM-DP+ response.
- [BetterRoaming official installation page](https://www.betterroaming.com/esim-installation/), inspected for current public installation links.
- [GSMA SGP.22 v2.6](https://www.gsma.com/solutions-and-impact/technologies/esim/wp-content/uploads/2024/09/SGP.22-v2.6.pdf), AuthenticateClient status codes: 8.2.6 / 3.8 is a refused MatchingID.

## Final live cycle: 0.18.1-rc7 / 29

Officially signed APK SHA-256: `e83838ffc26ac1de0ea3ed7dd37f4a23590a0b17161a1681b37130e6e7d3e3b3`. The installed base APK was hashed again after the cycle and matched. Application UID 10262 and its data/history survived the same-signer upgrade. The normal computer challenge and authenticated Shell Binder bootstrap passed; no Shizuku was used.

| Normal UI action | Durable transaction | Observed result |
| --- | --- | --- |
| Install a different, previously untried current official public BetterRoaming entry | `7611ffdb-8be2-4152-b47d-21f113855b4e` | Verified; exactly one new disabled profile 8944...6127. Native exit 0, HTTP 200 / Executed-Success, native and worker stopped. Installation notification 64 delivered. |
| Activate BetterRoaming | `72a4c11c-49c5-41b8-97d3-90ff59f92b0b` | Verified; test profile enabled and povo temporarily disabled. Android active subscription matched the test profile. |
| Restore povo | `839f1068-da23-4f4b-a80d-ee2fcf47099b` | Verified; povo enabled and test profile disabled. Android active subscription matched povo. |
| Delete only the disabled disposable profile | `cc885933-51da-40c7-9658-0ddd4fd58fcb` | Verified; exact original three-profile inventory restored. Deletion notification 67 delivered, with acknowledgment before local removal. |

Independent Shell snapshots compared complete card identity, full ICCIDs, raw nicknames, provider/name fields and states at every phase. Original SMC and USIMSA remained disabled with identical metadata; povo metadata never changed. Final native libeuicc/lpac 2.3.0 and Java reads both returned three profiles and one enabled profile. Android reported the original povo subscription active. Default-data subscription remained 2 and SIM states remained LOADED,LOADED; no global communication settings were changed.

An old deletion preview expired before submission and was rejected without a new mutation journal. Normal read-only recovery and an independent unchanged-inventory check preceded a new preview and the single successful deletion attempt. No uncertain write was automatically retried.

All four final-cycle transactions, installation notification 64 and deletion notification 67 belong to APK 29. Earlier version results are history, not substitutes. The inherited notification 63 repair also completed on 29. A later public entry succeeded after that repair, but the entry itself changed: this does not establish why earlier MatchingIDs were refused.

## Checks and limits

Release build and lint passed (0 errors, 55 warnings); 241 synthetic compatibility/safety assertions passed. Official signer, release surface, metadata, six native payload hashes and ELF/ZIP 16KB alignment passed. Alignment does not constitute a separate 16KB runtime qualification.

Network registration and mobile data were not tested. Standalone disable, enable/disable notification delivery, arbitrary operator downloads, interrupted-operation recovery in all failure phases, reboot persistence and other devices/ROMs remain unqualified. The final profile inventory is restored; it is not a claim that the entire global notification queue is byte-identical. Only test-profile installation/deletion notifications with a trusted recipient were delivered. Existing unknown download history and separate reviewed resolutions remain intact.

Temporary owner-scope files, private baseline snapshots and test input/helpers are removed after final verification. Production transaction and notification history, the installed app, companion and application data are retained. No public release is made by this qualification.

## Same-APK Telephony regression: 0.18.1-rc10 / 32

On 20 October 2026 the owner authorized a current-candidate regression on the same Android 13 Xiaomi, keeping povo enabled throughout. The officially signed APK SHA-256 was `a43a57f47432e1ff98aa6520f8d07235fab35d3a5540e8408530a0bf5e2d8af4`; production source remained `c15f5499ad507d3e63505ed0afeda11f9a25b385`. No application code, new APK or phone-specific exception was added. The installed APK was pulled back and hashed before and after; both matched. UID 10262 and safety history were retained. The other Android 16 phone was not operated during this regression.

Normal UI Connect using computer and one matching ADB bootstrap passed. Automatic discovery found no accessible AIDL instance, could not reach the declared HIDL services, failed to open Telephony slot 0, and verified `telephony.1.0`. Compatibility details and an explicit UI read-only recheck passed. Native lpac and Java independently matched the complete original three-profile inventory, with povo enabled and SMC/USIMSA disabled. An initial operator readback hit the shared transaction lock while the app was checking recovery; the lock correctly refused concurrency, and the read-only check passed after the app finished.

| Normal UI write | Durable transaction | Exact independent readback |
| --- | --- | --- |
| Rename inactive SMC to SMCTEST32 | `c36161e6-ba77-437f-8587-26eedb1e2eff` | Verified; only that nickname changed. |
| Restore SMC's original blank nickname | `fbadd821-882b-4a08-8f01-2d202dd88412` | Verified; complete original inventory restored. |
| Install one current public BetterRoaming entry | `141e3cab-fa2c-4bc5-a301-9c000273dfd2` | Verified; exactly one new disabled profile `8944...6127`, original three unchanged. Native exit 0, HTTP 200 / Executed-Success, native/worker stopped. |
| Rename the new inactive test profile to TPTEST32 | `eacbcf07-d2f9-4c46-985f-ad9f8a516571` | Verified; exact nickname change, original three unchanged. |
| Restore the test profile's original blank nickname | `c3b37449-ccb9-4e00-aece-55e6c5ae4a43` | Verified; complete four-profile inventory matched the post-install baseline. |
| Delete only that inactive test profile | `a4a3e24e-2ede-43ca-ba88-096a8aa1098d` | Verified; exact original three-profile inventory restored. |

All six durable card transactions are verified. Complete card identity and full ICCID/provider/name/nickname/state comparisons passed at every phase; independent native/Java counts were three → four → three, with exactly one enabled profile throughout. The original inventory fingerprint was `369789d510d37d3b2e27e097939d17ca4bab776ff79cfeecfed11b2e0d06dcfb` before and after. Povo never switched or changed metadata. Default-data subscription remained 2 and SIM states LOADED,LOADED. No private activation code, plan purchase, root, Shizuku, ROM/SELinux change, reboot or global communication-setting change was used.

### Notification result: partial, not a closed loop

Installation notification **68** remains `posting` / **delivery_unknown**, bound to the verified download transaction above. Its worker stopped, but a valid operator acknowledgment was not durably confirmed. The existing diagnostics cannot distinguish a rejected/nonconforming HTTP reply from a network/timeout failure; the cause is unproven. The notification was not resent, marked acknowledged, removed or cleared. Successful card installation does not establish notification delivery.

Deletion notification **69** was bound to the exact test profile/deletion transaction and reached **delivered**, including acknowledgment and owned-card notification cleanup. This profile also had older delivered installation records for the same complete card/profile identity and approved recipient, which satisfied the existing deletion-recipient policy; the new unknown record was not falsely used as an acknowledgment. The four older notification records remain unchanged. Final status reports one pending notification, delivery_unknown, cleanup unavailable and worker stopped. A fresh activity recovery check re-reads the persistent status rather than treating the latest deletion's acknowledgment as an aggregate all-clear.

Thus APK 32's Telephony connection, original-profile reading, SMC rename/restore, public installation, test rename/restore and test deletion passed here. **The installation-notification closure did not pass.** Do not substitute version-29 or Android-16 notification results for this failure. A future repair requires evidence about the original receipt or a separately justified replay/recovery protocol; it must not blindly repost or rewrite the journal as success.

Temporary input devices and executable helpers were removed. The app, live authenticated Shell service, all durable operation/notification history and private Shell-owned baseline snapshots needed to review notification 68 were preserved. No test profile remains. No switching or mobile-data acceptance was attempted on APK 32. Existing build/synthetic checks belong to the unchanged APK; this follow-up changes documentation only and does not constitute a public release.
