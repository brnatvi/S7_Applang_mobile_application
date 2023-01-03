package fr.uparis.applang.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class NotificationCloseListener: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("NOTIFICATIONS","Update word")
        if(intent!=null){
            val url: String = intent!!.getStringExtra("URL")!!
            Log.d("NOTIFICATIONS","Update word with $url")
//            TODO("mettre a jour le mot.")
            val action = intent!!.action
            if (action == "notification_cancelled") {
                // your code
            }
        }
    }

}