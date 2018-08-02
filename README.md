# Silent Android App Update Sample

This sample shows how to update Android app silently without user confirmation with a device owner app. 
It works on Android 6.0 and higher.

For more information see 
https://sisik.eu/blog/android/dev-admin/update-app

# Usage
1. Build app and install on device
```
adb install -r -t app.apk
```
<br>

2. Set app as device owner
```shell
 adb shell dpm set-device-owner eu.sisik.devowner/.DevAdminReceiver
```
>For Android 7.0 and higher you also need to set the `testOnly` flag in AndroidManifest.xml
<br>

3. Increment versionCode of the app and rebuild again.
>On Android 7.0 and higher you might need to **remove** the `testOnly` flag from AndroidManifest.xml again for the updates
<br>

4. Push the new APK with updated versionCode to the device
```
adb push app-update.apk /data/local/tmp
```
<br>

5. Start the device owner app (first version) and click on the button in the app. 
This should install the new version of the app from the specified location. The app should then automatically restart and show the new version.


