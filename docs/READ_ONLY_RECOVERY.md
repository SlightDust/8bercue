# Read-only recovery

An uncertain operation must not disable observation indefinitely. Version 46 separates exclusive read-only card access from mutation authorization. It preserves the persistent quarantine marker and original transaction bytes; it never retries a write or treats a readable card as proof that an earlier operation succeeded.

## Recovery path

1. Open **Connect using computer** and run the same displayed command during the App-created window. An existing connected session is unchanged unless the app explicitly opens a recovery window. The Shell-to-App challenge, version/APK pin and authenticated typed Binder remain mandatory.
2. When a persistent quarantine exists, the Shell client validates the marker and journal, serializes recovery attempts and inventories the phone's processes. It refuses recovery while any Shell native `lpac` worker or child of an old backend remains. It verifies each old backend's UID 2000, exact name, executable, package APK classpath and process start time, rechecks identity before requesting termination, and verifies exit. It never signals a native worker, another app or an unverified process. If process inspection is denied or inconsistent, access stays paused.
3. Recovery must obtain the original kernel file lock. Neither the lock inode nor quarantine marker is unlinked. New immutable recovery receipts bind the marker, original transaction and installed APK digests; no card/Profile identifier or credential is stored in clear text.
4. The authenticated backend reacquires that exclusive lock, verifies the complete eUICC identity against the unresolved transaction, reads the complete Profile inventory twice and requires identical fingerprints and stable reader/card identity. Failed cleanup, changed cards and unstable results remain blocked.
5. Successful observation is displayed as **Read-only access restored**. The earlier outcome remains **unknown**, mutation controls stay unavailable, and all write entry points still require both an unblocked lock and reconciled history. No automatic enable, rollback, nickname, download, notification submission or deletion occurs.

The current recovery scope restores observation only. It does not resolve the earlier transaction's intent or provide authorization to resume writes. If a backend cannot be proved stopped, the kernel lock is busy, recovery evidence is invalid, or the same card is unavailable, retain records and investigate; do not clear app data or delete safety files.

## Qualification

`bash android-app/tools/run_recovery_tests.sh` exercises worker identity, PID reuse, children/native workers, observed termination, process-table names containing spaces, malformed/symlink evidence, cross-process exclusion and preserved unknown journals. Provider tests cover explicit recovery windows, live Binder replacement rejection and single-use authenticated replacement.

The owner requested publication before device verification. Version-46 device results are recorded against the downloaded Release APK after publication. Earlier candidates and other devices are not acceptance evidence for that APK.
