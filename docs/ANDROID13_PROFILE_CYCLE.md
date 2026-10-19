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
