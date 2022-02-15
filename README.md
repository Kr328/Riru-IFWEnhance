# Riru - IFWEnhance

A module of [Riru](https://github.com/RikkaApps/Riru)/[Zygisk](https://github.com/topjohnwu/zygisk-module-sample). Allows Intent Firewall to filter results of queryIntent(Activities/Services) APIs.

## Requirements

* Zygote inject framework installed
  - Riru >= 26.0
  - Zygisk >= 24000
* Android 8.0+

## Feature

Allows Intent Firewall to filter results of follow APIs.

- `IPackageManager.queryIntentActivities`
- `IPackageManager.queryIntentActivityOptions`
- `IPackageManager.queryIntentServices`


## Build

1. Install JDK, Android SDK

2. Configure sdk path in local.properties 

   ```properties
   sdk.dir=/path/to/android/sdk
   ```

3. Build

    ``` bash 
    ./gradlew module:assembleRelease
    ```
    
4. Pick `<loader>-ifw-enhance-<version>.zip` from `module/build/outputs/<loader>/release`

