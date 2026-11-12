# Vendroid

> [!NOTE]
> This fork adds **third-party plugin support**: install Vencord plugins that aren't part of Vencord, without building Vencord yourself.
>
> 1. Open Settings → **Third-Party Plugins**.
> 2. Install a built plugin `.js` file from your phone's storage or from an `https://` link.
> 3. Tap **Restart to apply**, then enable the plugin in the Plugins tab.
>
> Only install plugins you trust: they get full access to your Discord account.
>
> The loader script, `app/src/main/res/raw/third_party_plugins.js`, is built from the [Vesktop fork](https://github.com/0-IceMan3D-0/Vesktop/tree/third-party-plugins/src/renderer/thirdPartyPlugins) (`pnpm build`, then copy `dist/vendroid/third_party_plugins.js`). See [its plugin docs](https://github.com/0-IceMan3D-0/Vesktop/blob/third-party-plugins/scripts/plugin-sdk/README.md) for how to build a plugin.
>
> To build the APK: `./gradlew assembleDebug`, output in `app/build/outputs/apk/debug/`.

Vendroid is not actively being improved. However, it still functions just fine and doesn't need any updates (it is not abandoned / discontinued).

For a more feature complete app, check out VendroidEnhanced instead!

[![Static Badge](https://img.shields.io/badge/Get%20VendroidEnhanced-black?style=for-the-badge&logo=android&link=https%3A%2F%2Fvendroid.nin0.dev)](https://vendroid.nin0.dev)


---

Vendroid is a custom Discord client for Android. The way it works is that it loads the discord.com website and injects Vencord.

| | |
|:--:|:--:|
|![image](https://github.com/Vencord/Vendroid/assets/45497981/e6464167-78b1-4f38-8e96-bb355ea5bbc3)|![image](https://github.com/Vencord/Vendroid/assets/45497981/3f6b278e-f18d-4cae-964f-f357f06ca2bd)|


Discord's mobile site is not very usable since it has many issues. As such, this is mostly just a proof of concept and is not actively worked on.

You are likely better off using other mods that specifically target the Android app, like [Aliucord](https://github.com/Aliucord/Aliucord).

## Download

Visit the [latest release](https://github.com/Vencord/Vendroid/releases/latest), grab the apk and install it
