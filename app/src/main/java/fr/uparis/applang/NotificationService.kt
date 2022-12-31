package fr.uparis.applang

import android.app.*
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import fr.uparis.applang.model.Language
import fr.uparis.applang.model.LanguageApplication
import fr.uparis.applang.model.Word
import fr.uparis.applang.navigation.OptionsMenuActivity
import java.util.*
import kotlin.concurrent.thread
import kotlin.math.min


class NotificationService : LifecycleService() {
    private var timer: Timer? = null
    private val dao by lazy { (application as LanguageApplication).database.langDAO() }
    private var wordsList: List<Word> = listOf<Word>()
    private val CHANNEL_ID = "entrainement traduction"
    private val notificationManager by lazy { getSystemService(NOTIFICATION_SERVICE) as NotificationManager }
    private val pendingFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        PendingIntent.FLAG_IMMUTABLE
    else
        PendingIntent.FLAG_UPDATE_CURRENT
    private val stackBuilder by lazy { TaskStackBuilder.create(this) }



    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }
    override fun onCreate() {
        Log.d("NOTIFICATIONS", "onCreate")
        super.onCreate()
        timer = Timer()
        createNotificationChannel()
        stackBuilder.addParentStack(ExercisesActivity::class.java)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Log.d("NOTIFICATIONS", "onStartCommand")
        val oma = OptionsMenuActivity();

        val sharedPref = getSharedPreferences("fr.uparis.applang", MODE_PRIVATE) // common preferences for all activities
        val wordsPerTrain = sharedPref.getInt(oma.keyQuantity, 1)// Need to be >0
        val trainPerDay = sharedPref.getInt(oma.keyFrequency , 10)// Need to be >0
        val trainingLanguage: Language = oma.getLanguageOfTheDay();


        Log.d("NOTIFICATIONS","Preferences loaded: $wordsPerTrain, $trainPerDay, $trainingLanguage")

        val words = dao.loadAllWordLangDest(trainingLanguage.id)
        words.observe(this){
            wordsList = it
            timer!!.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    wordsList = wordsList.shuffled()
                    sendNotifications(wordsList, wordsPerTrain, trainingLanguage)
                }
            }, 0L, (60000L*1440L)/trainPerDay)
        }
        return Service.START_NOT_STICKY
    }
    override fun onDestroy() {
        super.onDestroy()
        Log.d("NOTIFICATIONS", "onDestroy")
        timer!!.cancel()
    }
    /**
     * Send all notification for a training session.
     */
    private fun sendNotifications(wordsList: List<Word>, wordsPerTrain: Int, trainingLanguage: Language){
        thread {
            val len = min(wordsList.size, wordsPerTrain)
            Log.d("NOTIFICATIONS", "$len new notification will be start.")
            for (i in 0..len-1){
                Log.d("NOTIFICATIONS", "New notification: ${wordsList[i].toNotificationString()}")
                sendNotification(wordsList[i].toNotificationString(), i, trainingLanguage);
            }
        }
    }

    /**
     * Send a notification.
     * Notification id are always in [0, max notification-1] to avoid to have more than max notification at the same time.
     */
    private fun sendNotification(message: String, notId: Int, trainingLanguage: Language) {
        /* When user clic on notif, it send user to ExercisesActivity.*/
        val intent = Intent(this, ExercisesActivity::class.java)
        stackBuilder.addNextIntent(intent)

        val pendingIntent = PendingIntent.getActivity(this, 1, intent, pendingFlag)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Entrainement en $trainingLanguage")
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.small)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.large))
            .build()

        /* Envoyer la notification pour de bon. */
        notificationManager.notify(
            notId,
            notification
        )

    }

    private fun deleteNotification(id: Int) {
        notificationManager.cancel(id)
    }

    // Create the NotificationChannel, but only on API 26+ because
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "private channel",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = "private channel" }

            notificationManager.createNotificationChannel(channel)
        }
    }
}