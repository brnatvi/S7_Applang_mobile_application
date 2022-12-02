package fr.uparis.applang

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import fr.uparis.applang.model.Dictionary
import fr.uparis.applang.model.Language
import fr.uparis.applang.model.LanguageApplication
import fr.uparis.applang.model.Word
import kotlin.concurrent.thread

class MainViewModel(application: Application): AndroidViewModel(application) {
    val dao = (application as LanguageApplication).database.langDAO()

    var words: LiveData<List<Word>> = dao.loadAllWord()
    var languages: LiveData<List<Language>> = dao.loadAllLanguage()
    var dictionaries: LiveData<List<Dictionary>> = dao.loadAllDictionary()

    fun insertWord(word: Word){
        if(word==null || word.equals("")){
            return
        }
        thread {
            val returnCode = dao.insertWord(word)
            if(returnCode<0){
                Log.e("DB", "Fail to insert word $returnCode")
            }else{
                Log.i("DB", "Insert word n°$returnCode")
            }
        }
    }
    fun insertDictionary(dict: Dictionary){
        if(dict==null){
            return
        }
        thread {
            val returnCode = dao.insertDictionary(dict)
            if(returnCode<0){
                Log.e("DB", "Fail to insert dict $returnCode")
            }else{
                Log.i("DB", "Insert dict n°$returnCode")
            }
        }
    }

    fun loadAllWord(){
        thread {
            words = dao.loadAllWord()
        }
    }

    fun loadAllLanguage(){
        thread {
            languages = dao.loadAllLanguage()
        }
    }

    fun deleteAllLanguage(){
        thread {
            dao.deleteAllLang()
        }
    }

    fun loadAllDictionary(){
        thread {
            dictionaries = dao.loadAllDictionary()
        }
    }

    fun deleteAllDictionary(){
        thread {
            dao.deleteAllDist()
        }
    }

    fun insertLanguages(vararg list: Language){
        thread {
            val returnCodes = dao.insertLanguages(*list)
            for (returnCode in returnCodes){
                if(returnCode<0){
                    //Log.e("DB", "Fail to insert language $returnCode")
                }else{
                    Log.i("DB", "Insert language n°$returnCode")
                }
            }
        }
    }
}