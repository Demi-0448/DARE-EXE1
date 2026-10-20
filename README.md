# DARE.EXE v1.0

An offline-first Android dare/quest game with cyberpunk RPG styling.

## Features in this starter build
- Seven ranks: NPC, Beginner, Intermediate, Pro, Hacker, Developer, Legend.
- Rank unlock progression based on XP.
- Random curated quests and rerolls.
- XP rewards, completion count, and local persistence.
- No login, server, or internet permission required for core gameplay.
- GitHub Actions workflow builds a debug APK in the cloud.

## Build the APK in GitHub Actions
1. Upload/push the contents of this project into the root of your `DARE-EXE` repository on the `main` branch.
2. Open the repository's **Actions** tab.
3. Select **Build DARE.EXE APK** and tap **Run workflow** (or let the workflow run after a push).
4. When it succeeds, open the run and download the `DARE-EXE-v1.0-debug-apk` artifact.
5. Extract the downloaded ZIP to get `app-debug.apk`, then install it on your Android phone.

## Notes
- This is an unsigned debug APK intended for personal testing, not Play Store release.
- XP and completion history are stored locally on the device. Uninstalling the app may erase that data.
- Proof-upload and Instagram screenshot verification are not implemented in this first starter build; no media is uploaded anywhere.
- AI integration is intentionally not included yet; the quest list is local and works offline.
