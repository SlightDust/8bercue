<p align="center">
  <img src="docs/images/icon.png" width="88" alt="8bercue icon">
</p>

<h1 align="center">8bercue</h1>

<p align="center">An open-source eSIM manager for Android. Your profiles, on your terms.</p>

<p align="center">
  <a href="https://github.com/sidnvy/8bercue/releases">Download APK</a> ·
  <a href="https://github.com/sidnvy/8bercue/releases/tag/v0.18.0-beta.1">Release notes</a> ·
  <a href="https://github.com/sidnvy/8bercue/issues">Feedback</a>
</p>

Manage profiles on compatible removable eUICC cards, including 5ber, from a clean Android interface. No 5ber account required.

<p align="center">
  <img src="docs/images/profiles.png" width="260" alt="Profile list showing installed eSIMs and their status">
  &nbsp;&nbsp;
  <img src="docs/images/profile-details.png" width="260" alt="Profile details with activate, rename and delete actions">
</p>

## Features

- View profiles and card information.
- Rename, enable, disable and delete profiles.
- Add an eSIM by scanning a QR code, importing an image or entering an activation code.
- Connect through a computer or Shizuku.
- Review changes before applying them, with recovery tools for interrupted operations.

## Get started

The APK installs on **ARM64 Android 9+**. Card management also requires a compatible eUICC and a system Secure Element channel that permits access to it. Android version and ADB access alone do not guarantee card access. Available management actions depend on the connection. See [device compatibility](docs/COMPATIBILITY.md).

This branch checks the actual card connection automatically: Secure Element HAL first, then Telephony, with OMAPI as another fallback. **Compatibility check** shows the selected channel and missing conditions. No phone-brand or Android 16 admission list is used. The published Beta retains its older limits.

A computer with **ADB** can start the management connection; Shizuku is optional.

1. Install the APK from [Releases](https://github.com/sidnvy/8bercue/releases).
2. Open 8bercue and choose **Connect using computer**, or set up Shizuku in Settings.
3. Connect your card and manage your profiles.

For the computer connection, download `bootstrap_companion.py` from the same release and run:

```sh
python3 bootstrap_companion.py --serial YOUR_DEVICE_SERIAL --adb /path/to/adb
```

For an existing wireless ADB connection, add `--allow-existing-wireless`.

Currently in **Beta**. See the [release notes](https://github.com/sidnvy/8bercue/releases/tag/v0.18.0-beta.1) for setup details, migration guidance and known issues.

## Build & contribute

See [BUILDING.md](BUILDING.md) to build and sign your own APK. Bug reports and contributions are welcome through [Issues](https://github.com/sidnvy/8bercue/issues) and pull requests. Keep activation codes and personal identifiers out of public reports.

## License & contact

[AGPL-3.0-only](COPYING) · [Third-party licenses](LICENSING.md) · [Privacy](PRIVACY.txt)

Built by sid · [support@sidnvy.com](mailto:support@sidnvy.com)

8bercue is an independent project and is not affiliated with 5ber.
