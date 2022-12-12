package fr.uparis.applang

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import fr.uparis.applang.model.Dictionary
import fr.uparis.applang.model.LanguageApplication

class DictViewModel(application: Application) : AndroidViewModel(application) {
    val dao = (application as LanguageApplication).database.langDAO()

    val allDictionnaries: LiveData<List<Dictionary>> = dao.loadAllDictionary()

    fun insertDictionnary(name: String, url: String, requestComposition: String) {
        Thread {
            val newD = Dictionary(name.trim(), url.trim(), requestComposition.trim())
            Log.d("DB","try to insert "+newD)
            dao.insertDictionary(newD)
        }.start()
    }

    fun deleteDictionnary(name: String) {
        Thread {
            dao.deleteOneDict(name)
        }.start()
    }

}

