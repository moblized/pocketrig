# PocketRig

Your Steam library on your Android phone. Sign in with your Steam account, pick a game you own, download it,
and play it on the phone itself. Nothing streams from a PC.

PocketRig is a personal fork of [GameNative](https://github.com/utkarshdalal/GameNative) by Utkarsh Dalal and
contributors, which does the heavy lifting: the Steam client, downloads, cloud saves, and running Windows games
through Wine/Proton, DXVK, FEX/Box64 and the Turnip GPU drivers. It is licensed under the GPL-3.0, like the
original (see `LICENSE` and `THIRD_PARTY_NOTICES`).

## Install

1. Open this repo's **Releases** page on your phone and download the newest `pocketrig-1.0.N.apk`.
2. Open it and allow installing from your browser if Android asks.
3. After that, PocketRig checks this repo's Releases when it starts and offers each new build.

Every push to `master` builds a new APK with GitHub Actions (`.github/workflows/build.yml`) and publishes it as
a release. You can also start a build by hand from the Actions tab.

## Using it

1. Open PocketRig and sign in to Steam. Scanning the QR code with the official Steam app on another device is
   the safest way; username, password and Steam Guard work too.
2. Your library loads. Tap a game, then **Install** to download it to the phone.
3. Tap **Play**. The first launch also downloads the Windows runtime, once.

Epic, GOG and Amazon libraries can be connected too.

What runs well depends on the phone's chip. Snapdragon phones get the best results because of the open-source
Turnip driver; lighter 2D and older 3D games are the safest bets. GameNative's community compatibility list is
at https://gamenative.app/compatibility.

## What's different from GameNative

Changed in November 2026:

- Name **PocketRig** and its own icon (a handheld with a download arrow), drawn by `tools/make_icons.py`.
- The launcher icon follows the app's theme: System Default follows the phone's light/dark mode, Light keeps the
  light icon, Dark and AMOLED use the dark icon. The old icon picker in Settings is gone.
- Updates come from this repo's GitHub Releases instead of GameNative's update server.
- One build (the "legacy" flavor GameNative recommends for most phones), signed with the key in `signing/`
  so each update installs over the last one.
- No analytics keys are built in.
- GameNative's paid extras (Optimize, AI help) and its account features still point at GameNative's servers.

The app keeps GameNative's Android package name (`app.gamenative`) because the downloaded Windows runtime
expects that path. That means PocketRig and the official GameNative app can't both be installed: uninstall one
before installing the other.

## Building locally

Android Studio with JDK 17, then `./gradlew :app:bundleLegacyRelease`. See the workflow file for how the
universal APK is made from the bundle.

## Credits

All the engine work is GameNative's and the projects it builds on (Pluvia, Winlator, Wine, DXVK, FEX, Box64,
Mesa Turnip and more). If PocketRig is useful to you, consider supporting GameNative: https://ko-fi.com/gamenative
