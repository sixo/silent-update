package eu.sisik.devowner

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.util.Log

/**
 * Copyright (c) 2018 by Roman Sisik. All rights reserved.
 */
class UpdateReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        Log.d("devowner", "UpdateReceiver:" + intent?.dataString)

        // First check that your package got updated and not some
        // other package
        if (intent?.dataString == "package:" + context.packageName) {

            // Restart your app here
            val i = Intent(context, MainActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(i)
        }
    }
}
