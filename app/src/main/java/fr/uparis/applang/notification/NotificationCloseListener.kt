package fr.uparis.applang.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import fr.uparis.applang.model.LanguageApplication
import fr.uparis.applang.navigation.OptionsMenuActivity

class NotificationCloseListener: BroadcastReceiver() {
    private val dao by lazy { LanguageApplication().database.langDAO() }
    private val oma = OptionsMenuActivity()


    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("NOTIFICATIONS","Update word")
        if(intent!=null && intent.extras != null && context!=null){
//            val url: String = intent!!.getStringExtra("URL")!!
            Log.d("NOTIFICATIONS","Update word 2")
            var intentUW = Intent(context, UpdateWordService::class.java ).apply {
                action = "updateWord"
            }
            intentUW.putExtras(intent.extras!!)
            context!!.startService(intentUW)
        }
    }

}