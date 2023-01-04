package fr.uparis.applang.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * React to close notification by starting a Service that will be able to update correct guess counter for notification word.
 */
class NotificationCloseListener: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("NOTIFICATIONS","Update word")
        if(intent!=null && intent.extras != null && context!=null){
            val intentUW = Intent(context, UpdateWordService::class.java ).apply {
                action = "updateWord"
            }
            intentUW.putExtras(intent.extras!!)
            context.startService(intentUW)
        }
    }

}