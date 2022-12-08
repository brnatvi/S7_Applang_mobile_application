package fr.uparis.applang

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import fr.uparis.applang.model.Dictionary
import fr.uparis.applang.model.LanguageApplication

class DictViewModel(application: Application) : AndroidViewModel(application) {
    val dao = (application as LanguageApplication).database.langDAO()

    fun insertDictionnary(name: String, url: String, requestComposition: String) {
        Thread {
            val newD = Dictionary(name.trim(), url.trim(), requestComposition.trim())
            dao.insertDictionary(newD)
        }.start()
    }
}

