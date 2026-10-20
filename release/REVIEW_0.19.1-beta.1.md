# Release 46 source review

Reviewed localization, the integration of origin/main 694a121, caller identity,
read-only recovery, lock preservation, bootstrap windows and release disclosure.

Resolved before publication:
- Preserve authenticated app identity for nested journal checks while HAL calls
  execute as Shell; keep unauthenticated direct service calls rejected.
- Explicit recovery can replace a verified stale helper, but ordinary connected
  reruns remain idempotent. Recovery does not clear original quarantine/journal.
- Parse process names containing spaces; validate all peer identities and children
  before signals, reject active native workers, and retain the original kernel lock.
- Merge feedback failure stages without losing localized UI or recovery-window state.
- Correct accidental localization of protocol operation IDs and canonical status
  keys during merge; added regression assertions preventing recurrence.
- Update Chinese privacy notice, SDK license packaging, and unavailable-reporting
  behavior for this build without a DSN.

Residual limitations: existing unknown operation remains unknown and writes stay
blocked. Android process inspection/signal is not an atomic pidfd operation;
identity is rechecked and card access additionally requires the original exclusive
lock. Device qualification for APK46 is explicitly pending until after publication.
No profile mutations or network diagnostic submissions are part of acceptance.
