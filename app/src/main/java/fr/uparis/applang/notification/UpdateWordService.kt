package fr.uparis.applang.notification

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.LiveData
import fr.uparis.applang.model.LanguageApplication
import fr.uparis.applang.model.Word
import kotlin.concurrent.thread

class UpdateWordService : LifecycleService() {
    private val dao by lazy { (application as LanguageApplication).database.langDAO() }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null && intent.action == "updateWord") {
            Log.d("NOTIFICATIONS", "onStartCommand updateWord")
            val url = intent.getStringExtra("URL")!!
            Log.d("NOTIFICATIONS", "Update word with $url")
            val notifWord: LiveData<List<Word>> = dao.loadWordFromUrl(url)
            Log.d("NOTIFICATIONS", "Before observe")
            notifWord.removeObservers(this)
            notifWord.observe(this) {
                if (it.isNotEmpty()) {
                    val word: Word = it[0]
                    word.correctGuess += 1
                    thread {
                        dao.deleteOneWordFromUrl(url)
                        dao.insertWord(word)
                    }
                    Log.d("NOTIFICATIONS", "Update word to $word")
                } else {
                    Log.d("NOTIFICATIONS", "Empty answer")
                }
                notifWord.removeObservers(this)
            }
        }
        super.onStartCommand(intent, flags, startId)
        return Service.START_NOT_STICKY
    }
}