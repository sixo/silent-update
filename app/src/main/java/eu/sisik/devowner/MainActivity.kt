package eu.sisik.devowner

import android.Manifest
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.UserManager
import android.provider.Settings
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            // Clicking on install button will cause the app-update.apk to
            // be installed from the given location.
            // You need to ensure that the location where you put the APK file
            // with updated version is readable from this app.
            try {
                install(this@MainActivity, packageName, "/data/local/tmp/app-update.apk")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val dpm = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        if (dpm.isDeviceOwnerApp(packageName))
            Log.d(TAG, "is device owner")
        else
            Log.d(TAG, "not device owner")

        tv_text.text = "version code=" + BuildConfig.VERSION_CODE
    }

    /**
     * Below are some methods that demonstrate what privileged tasks can be
     * performed from a Device Owner app
     */

    fun startLockTaskMode() {

        // Get DevicePolicyManager instance
        val dpm = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager

        // Get an identifier for the component that is DeviceAdminReceiver
        // This will be used in various DevicePolicyManager methods
        val cn = ComponentName(this, DevAdminReceiver::class.java)

        // If we are a device owner, we are allowed to
        // whitelist a package for using LockTask mode
        if (dpm.isDeviceOwnerApp(packageName) && !dpm.isLockTaskPermitted(packageName)) {
            val packages = arrayOf(packageName)
            dpm.setLockTaskPackages(cn, packages)
        }

        // If we are whitelisted for LockTask mode,
        // start the mode
        if (dpm.isLockTaskPermitted(packageName)) {
            startLockTask()
        }
    }

    fun hideDialer() {
        val dpm = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val cn = ComponentName(this, DevAdminReceiver::class.java)

        // Hide dialer here, or set last parameter to false to make it visible again
        dpm.setApplicationHidden(cn, "com.android.dialer", true)
    }

    fun grantStoragePermission() {
        val dpm = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val cn = ComponentName(this, DevAdminReceiver::class.java)

        dpm.setPermissionGrantState(cn, packageName,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                DevicePolicyManager.PERMISSION_GRANT_STATE_GRANTED)
    }

    fun disallowApsControl() {

        val dpm = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val cn = ComponentName(this, DevAdminReceiver::class.java)

        // Restrictions can be cleared again with clearUserRestriction()
        dpm.addUserRestriction(cn, UserManager.DISALLOW_APPS_CONTROL)
        //        dpm.clearUserRestriction(cn, UserManager.DISALLOW_APPS_CONTROL)
    }

    fun changeSecureSettings() {

        val dpm = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val cn = ComponentName(this, DevAdminReceiver::class.java)

        dpm.setSecureSetting(cn, Settings.Secure.LOCATION_MODE, "1")
        dpm.setSecureSetting(cn, Settings.Secure.SKIP_FIRST_USE_HINTS, "1")
    }

    fun rebootMyDevice() {
        val dpm = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val cn = ComponentName(this, DevAdminReceiver::class.java!!)

        // Reboot requires Nougat and higher
        dpm.reboot(cn)
    }

    // This removes the device owner without the need to factory reset the device
    fun clearDeviceOwner() {
        try {
            val cn = ComponentName(packageName, packageName + ".AdminReceiver")
            val dpm = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
            dpm.removeActiveAdmin(cn)
            dpm.clearDeviceOwnerApp(packageName)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    companion object {
        val TAG = "MinDeviceOwner"
    }
}
