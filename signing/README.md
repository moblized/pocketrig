# Signing key

`pocketrig.keystore` signs every PocketRig build (debug and release), so each new APK installs over the
last one and keeps your games and settings. Password `android`, alias `pocketrig`.

It's committed to this public repo on purpose, so GitHub can build without secrets. That makes it a
sideloading key only: anyone could sign an APK with it, so only install PocketRig APKs from this repo's
Releases page.
