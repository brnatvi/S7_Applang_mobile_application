package fr.uparis.applang.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

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