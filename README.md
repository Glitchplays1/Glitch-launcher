# Glitch Launcher

Neon CATCH / GLITCH Android home-screen download page.

**Live site:** https://glitchplays1.github.io/Glitch-launcher/

**Repo:** https://github.com/Glitchplays1/Glitch-launcher

## What this is
A static website that matches the lock-screen and home-screen mockups.  
The **Download APK** button looks for `GlitchLauncher.apk` in this same folder.

This repo does **not** include a built APK. Add your own trusted file named `GlitchLauncher.apk` if you want the button to work.

## Change the home launcher
1. Install the APK on the phone.
2. Open **Settings** and search **Default apps**.
3. Tap **Home app** / **Launcher**.
4. Choose **Glitch Launcher**.
5. Press Home or swipe up.

To go back, set Home app to the stock launcher *before* uninstalling.

## Local preview
Open `index.html` in a browser, or serve the folder:

```bash
python3 -m http.server 8080
```
