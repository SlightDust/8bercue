# Selected-card UI and ADB onboarding candidate

Unpublished 0.19.0-rc5, versionCode 38. No release upload is authorized.

The homepage manages one explicitly selected eUICC. Settings is in the top-right app bar; no bottom navigation is added. Profiles have a dedicated detail page with a native Use this eSIM switch and direct Rename/Delete actions. Switching and nickname Save use one explicit user gesture followed by the existing authenticated prepare/execute pair. Installation and deletion retain confirmation; enabled profiles cannot be deleted. Journals, unknown-result recovery, identity checks and one-use tokens remain in the service.

Connection uses one APK-bound ADB shell command copied from the installed application. The starter already bundles challenge, caller UID, protocol/version, app lifetime and artifact checks. Python is not an end-user prerequisite. Connection alone does not prove card access; multi-card discovery requires explicit selection and capability/permission failures stay visible.

A session-scoped UI selection digest includes the complete verified card identity and full ICCID. It is not an authorization token. Masked ICCIDs are only presentation values and cannot be used to follow a profile after inventory replacement. Backgrounding/navigation invalidates pending UI selection and prepare callbacks.

## Validation scope

Release build and lint passed (0 errors, 60 warnings), along with 332 compatibility/safety and 40 UI/command/identity assertions. The production-signed APK was installed as a data-preserving upgrade on the locally attached Pixel 10 Pro XL, API 37. Its single-command connection, AIDL HAL SIM1 card choice, fresh read and native UI navigation were verified. Independent native/Java readback compared all full fields on both cards: one removable-card profile and five built-in profiles remained unchanged.

No live write was authorized for this candidate: no activation, deactivation, saved rename, download, notification send/cleanup or deletion was executed. Prior APK write cycles do not qualify this build. Fresh install, Android-13 live UI, other ROMs, Windows terminal quoting, camera/image import, large-font and tablet cases remain untested in this run. The direct command was tested from the local Linux host. Existing native payload assets are unchanged. Detailed exact-APK evidence, signer and checksums accompany the private candidate delivery.
