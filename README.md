# Riru - IFWEnhance

A module of [Riru](https://github.com/RikkaApps/Riru)/[Zygisk](https://github.com/topjohnwu/zygisk-module-sample). Enhance Intent Firewall.

## Requirements

* [Riru](https://github.com/RikkaApps/Riru) >= 26.0 installed.
* Android 8.0+

## Feature

Enhance Intent Firewall

* Apply Intent Firewall for **Implicit intents** (`PackageManager.queryIntentActivities`)



## Build

1. Install JDK ,Android SDK ,Android NDK

2. Configure local.properties 

   ```properties
   sdk.dir=/path/to/android/sdk
   ```

3. Run command 

    ``` bash 
    ./gradlew module:assembleRelease
    ```
    
4. Pick `riru-ifw-enhance-<version>.zip` from `module/build/outputs/<variant>`

