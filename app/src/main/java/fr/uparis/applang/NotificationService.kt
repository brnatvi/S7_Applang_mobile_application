package fr.uparis.applang

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.LifecycleService
import fr.uparis.applang.model.LanguageApplication
import fr.uparis.applang.model.Word
import java.util.*
import kotlin.concurrent.thread
import kotlin.math.min


class NotificationService : LifecycleService() {
    private var timer: Timer? = null
    private val dao by lazy { (application as LanguageApplication).database.langDAO() }
    private var wordsList: List<Word> = listOf<Word>()

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }
    override fun onCreate() {
        Log.d("NOTIFICATIONS", "onCreate")
        super.onCreate()
        timer = Timer()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Log.d("NOTIFICATIONS", "onStartCommand")
        val words = dao.loadAllWord()
        words.observe(this){
            wordsList = it
            timer!!.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    wordsList = wordsList.shuffled()
                    sendNotifications(2, wordsList)
                }
            }, 0, 60000) //Actuelement c'est toutes les minutes
        }
        return Service.START_NOT_STICKY
    }
    override fun onDestroy() {
        super.onDestroy()
        Log.d("NOTIFICATIONS", "onDestroy")
        timer!!.cancel()
    }

    private fun sendNotifications(number: Int, wordsList: List<Word>){
        thread {
            val len = min(wordsList.size, number)
            Log.d("NOTIFICATIONS", "$len new notification will be start.")
            for (i in 0..len-1){
                Log.d("NOTIFICATIONS", "New notification: ${wordsList[i].toNotificationString()}")
            }
        }
    }
}