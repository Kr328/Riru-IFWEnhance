# Riru - InternalBrowserRedirect

A module of [Riru](https://github.com/RikkaApps/Riru). Enhance Intent Firewall.

## Requirements

* [Riru](https://github.com/RikkaApps/Riru) > 19 installed.
* Android 8.0-9.0 (below not tests)



## Feature

Enhance Intent Firewall

* Apply Intent Firewall to `PackageManager.queryIntentActivities`



## Build

1. Install JDK ,Gradle ,Android SDK ,Android NDK

2. Configure local.properties 

   ```properties
   ndk.dir=/path/to/android/ndk
   sdk.dir=/path/to/android/sdk
   cmake.dir=/path/to/android/cmake/*version*
   ```

3. Run command 

``` Gradle 
./gradlew build
```
4. Pick riru-internal-browser-redirect.zip from module/build/outputs



## Feedback

Telegram Group [Kr328 Riru Modules](https://t.me/kr328_riru_modules)