# Display languages

The unpublished 0.19.1-rc1 candidate (versionCode 42) adds English and Simplified Chinese. The published 0.19.0-beta.1 APK is unchanged.

The default follows the phone's language. Settings → Language offers Follow system, 简体中文 and English. Android 13+ uses the platform's per-app locale setting; Android 9–12 uses an app-scoped preference and configuration context. This never changes the system locale. Both languages are bundled for offline use; language splitting is disabled for future bundles.

428 paired Android string resources cover navigation, Profile states/actions, connection and countdown, installation/deletion confirmation, unknown-result/recovery guidance, notification outcomes, compatibility errors, camera/QR UI, validation and accessibility descriptions. A translated local privacy page accompanies the original policy; third-party legal notices retain their original text.

User/operator names, activation/confirmation codes, card identifiers, ADB commands, protocol/status keys and exported diagnostic vocabulary are not translated. Fixed backend messages are mapped only at explicit presentation boundaries. No generic translation of a TextView's data is performed. Existing task-context comparisons remain consistent in each locale; the idle-status policy still consumes canonical keys. Destructive confirmation styling explicitly recognizes the localized permanent-delete action.

Only UI/presentation, resources, locale selection and version metadata change. The authenticated Binder service, authorization, operation tokens, lifecycle/download engines, safety journals, transaction exclusion and native payload remain unchanged. No Profile write is authorized by a language change.

## Validation boundaries

Build/lint, bilingual resource and placeholder parity, complete owned-error-message coverage and the existing safety/compatibility/UI regressions are required. Resource assertions are offline checks, not real-device qualification.

The selected local Moto already had a persistent `card-transaction.lock.quarantine` marker before the candidate was installed. The old Beta also displayed unverified inventory and blocked operations. That marker must not be removed or bypassed for UI testing. Current-build card reading and profile-detail/install/delete write flows cannot be qualified while it remains. No earlier APK read/write result is reused as this candidate's result. Exact candidate signing, installation, language/connection UI observations and the unchanged marker are recorded with the private delivery.

### Candidate 42 result

The production-signed APK SHA-256 is `b9ab8256b744608cc970684ae85f72dcfd0bf5ef31822571dcec9ee68cb266cd`. A same-signer, data-preserving upgrade and 23 normal UI checks passed on the local Motorola edge 70, Android API 37. Simplified Chinese, English and Follow system were selected through the App's normal picker. Platform per-app locales changed between `zh-Hans`, `en` and empty while the system locale remained unchanged. Settings, home, connection/countdown/copy, authenticated handoff, blocked recovery/compatibility, records and translated privacy display were observed. The final App language is Simplified Chinese.

Build and lint passed (0 errors / 52 warnings). 1,854 bilingual resource/format/error-coverage assertions and the existing 434 offline safety/compatibility/UI assertions passed. The installed APK was pulled and matched the delivered hash; all eight native payload/manifest entries matched the previous Beta. No card/operation authorization, journal, engine or ADB command source changed.

The pre-existing quarantine marker's byte hash was preserved. Card recognition/fresh Profile read remain blocked, so Profile detail, rename/delete/install confirmations, camera UI, Android 9–12 fallback, system-Chinese default behavior and TalkBack/large-font cases are not live-qualified by these checks. No Profile write or safety-record removal was submitted. The published Beta tag and assets were not modified; this is a private candidate for review.

## Version 46 publication

The language work is included in 0.19.1-beta.1 (46), alongside the separate read-only recovery fix described in READ_ONLY_RECOVERY.md. Earlier private candidate-42 UI observations above are historical and do not qualify APK46. Per the owner's requested sequence, APK46 device qualification occurs after publication using the downloaded Release asset.
