package fr.uparis.applang

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlin.concurrent.thread

class MainViewModel(application: Application): AndroidViewModel(application) {
    val dao = (application as LanguageApplication).database.langDAO()
    var success:Long =0
    fun insertWord(word: Word):Long{
        thread {
            success= dao.insertWord(word)
        }
        return success
    }
}