# Device compatibility

Installing the APK and accessing an eUICC are separate requirements. The APK's minimum SDK is 28 (Android 9), with ARM64 native components built for API 28. The compile/target SDK remains 36. This does not guarantee that a phone exposes a permitted card channel.

The published `v0.18.0-beta.1` APK is version `0.18.0-rc1` / 22. Its management code rejects API levels below 36. This branch's unpublished `0.18.1-rc2` / 24 candidate removes that version gate and checks actual services and access instead. It is not a declaration of general Android 13 support.

## Transports

- Prefer the installed system Secure Element AIDL service when its instances are present. Interface classes come only from the trusted system SecureElement APK.
- Otherwise use the framework OMAPI service, including on systems where that service talks to HIDL. System applet access rules still apply. The app does not initialize/reset the hardware HAL or replace the system service's callback.
- OMAPI owns the physical channel; the existing APDU policies use virtual channel 1. Only the owned channel and session are closed. Global reader sessions are never closed.
- Discovery, profile inventory, lifecycle, download and notification paths share this transport interface. Each caller retains its existing identity checks and APDU allowlist. Reader names include the transport namespace; switching transports does not silently reuse an existing selection.

ADB starts the Shell companion after **Connect using computer** is selected. Shizuku is optional. The connection lasts while that process remains alive; start it again after reboot, process termination or an app upgrade. Neither method grants permissions denied to Shell by the ROM.

## Android 13 qualification: Xiaomi 2109119BC (mona)

Observed ROM: `V816.0.2.0.TKVCNXM`, Android 13 / API 33, ARM64. These results apply to this device/ROM, not every Android 13 phone.

| Channel | Read-only observation |
| --- | --- |
| Hardware AIDL | Not registered; its interface classes are absent from the installed SecureElement APK. |
| Hardware HIDL 1.0/1.1/1.2 | SIM1 and eSE1 are registered, and system interface classes exist. SELinux denies Shell `find` on `hal_secure_element_hwservice`. Direct HIDL is not used by this candidate. |
| Framework OMAPI | Reader enumeration/presence works. Opening ISD-R is denied by the system access-control enforcer: `No ARF exists`. |
| Telephony | Shell has `MODIFY_PHONE_STATE`; the ROM still rejects the correct ISD-R AID with `The calling package is not allowed to access ISD-R.` This was a diagnostic probe, not a runtime fallback. |

The formally signed candidate APK SHA-256 is `cc513ef7da0a6c5107a876f25c302cb692b1f618a6486acbc2a5f79a58d6bb7e`. It was installed as a same-signer update and launched on this device. The normal **Connect using computer** flow completed the independent Binder handoff without Shizuku; the waiting sheet closed and the UI displayed the system-access denial. The candidate's bundled native `lpac version` command returned `2.3.0` on the phone with stdio drivers and no card broker or HTTP request. This only verifies native loading, not profile I/O.

No profile inventory can currently be established through these authorized Shell channels on this ROM. A reader name or a present card is not verified eUICC identification. No profile mutation, activation-code consumption, notification removal, radio change or reboot is authorized by these diagnostic results.

The public host regression suite covers adapter behavior, virtual channel isolation, denied/null opens and cleanup, full GetEID assembly and bounded continuation, ambiguous discovery, non-Shell rejection, read-only APDU policy, active-profile deletion rejection and protected-profile handling. It uses synthetic data and cannot qualify hardware. Run it with:

```sh
bash android-app/tools/run_compatibility_tests.sh
```

The delivery acceptance report binds build, installation, UI and access results to the candidate APK SHA-256 and the actual device. Successful writes, downloads, notifications and deletion remain unverified until separately authorized and observed. Previous Moto results do not qualify this candidate.
