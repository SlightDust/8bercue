# 8bercue 0.19.1 Beta 3

The integrated Beta with Simplified Chinese, capability-selected card access,
read-only recovery, and **configured GlitchTip reporting**. APK versionCode **48**.

- Configure the owner's existing 8bercue GlitchTip project. The client DSN is
  embedded in the APK; no account token or signing credential is included.
- Connection/read-failure reports can be previewed and explicitly submitted.
  Optional minimized crash reports remain **off by default** and require consent.
  Local diagnostic ZIP and support email remain available.
- Retain Chinese/English language selection, the single ADB connection command,
  automatic HAL/Telephony capability checks, and Beta2's recovery-button fix.
- Preserve caller authentication, full card binding, single-use write confirmation,
  transaction history and unknown-outcome write blocks. Enabling reporting does
  not bypass safety checks or permit an unresolved transaction to be retried.
- Add `verify_artifact.py --require-reporting` to reject publication artifacts
  with missing/invalid HTTPS DSN. The check rejects the previous empty-DSN APK.

## Validation and limits

Release build/lint passed (0 errors / 55 warnings), as did localization (477
bilingual strings), feedback privacy and reconciliation composition regressions.
The production signer is unchanged. Independent source-archive rebuild and signed
payload checks accompany the release. Device/reporting results are recorded after
publication against this exact downloaded APK, not inherited from earlier builds.

No Profile writes or notification HTTP actions are authorized in this round.
Download, activation/deactivation, rename and delete remain experimental and are
not newly qualified on APK48. The existing Moto unknown transaction stays blocked
for changes even when its card can be read. Other-ROM, Android 13, fresh-install,
camera and accessibility qualification are not newly claimed. This is a Beta,
not a stable/production certification.

## Upgrade

```sh
adb -s YOUR_SERIAL install -r 8bercue-0.19.1-beta.3-48.apk
```

Keep app data and Shell safety records. Open **Connect using computer** and run
the displayed stable ADB command. No Python companion, Shizuku or root is needed.
Use a device serial when several phones are connected. Do not uninstall or clear
records to bypass a signing/recovery problem.

Certificate SHA-256:
`24aa7984b0d0977ff1e87b57beca91b2e36a05c04a5443f1a47aa23d90a6ef1c`.

GitHub Actions is deferred: this release uses the existing local build and protected
signing workflow. Previous release tags and binary/source attachments are retained.
