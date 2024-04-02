<br />
<p align="center">
<a><img src="image/Logo-circle.png" alt="FawnRescue" width="128" height="128" title="FawnRescue"></a>
  <h3 align="center">FawnRescue</h3>
  <p align="center">
    Frontend App<br />
    <p align="center">
  <a href="https://github.com/FawnRescue/frontend/blob/main/LICENSE"><img src="https://img.shields.io/github/license/FawnRescue/frontend" alt="License"></a>
  <a href="https://github.com/FawnRescue/frontend/network/members"><img src="https://img.shields.io/github/forks/FawnRescue/frontend?style=social" alt="GitHub forks"></a>
  <a href="https://github.com/FawnRescue/frontend/stargazers"><img src="https://img.shields.io/github/stars/FawnRescue/frontend?style=social" alt="GitHub stars"></a>
</p>
    <p>
    <a href="https://github.com/FawnRescue/frontend/issues">Report Bug</a>
    ·
    <a href="https://github.com/FawnRescue/frontend/issues">Request Feature</a>
    </p>
    <a href="https://fawnrescue.github.io/">Website</a>
  </p>
</p>

This is a Kotlin Multiplatform project targeting Android, iOS, Desktop.

* `/composeApp` is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - `commonMain` is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    `iosMain` would be the right folder for such calls.

* `/iosApp` contains iOS applications. Even if you’re sharing your UI with Compose Multiplatform, 
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.


Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…

# Setup
if there are problems with the iosX64() library import set `repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)`
- Set secrets.properties
- Set debug.keystore in ~/.android/
