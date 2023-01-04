package fr.uparis.applang

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import fr.uparis.applang.model.Dictionary
import fr.uparis.applang.model.Language
import fr.uparis.applang.model.LanguageApplication
import fr.uparis.applang.model.Word
import kotlin.concurrent.thread

class ViewModel(application: Application): AndroidViewModel(application) {
    private val dao = (application as LanguageApplication).database.langDAO()

    var words: LiveData<List<Word>> = dao.loadAllWord()
    var languages: LiveData<List<Language>> = dao.loadAllLanguage()
    var dictionaries: LiveData<List<Dictionary>> = dao.loadAllDictionary()
    var currentTranslationUrl: String = ""
    var currentLangFrom: String = ""
    var currentLangTo: String = ""

    // ================================= Word's functions ==========================================
    fun insertWord(word: Word) : Long {
        if(word.text.isEmpty()){
            return -1
        }
        var returnCode: Long = 500
        currentTranslationUrl = "" // url have been use for save this word & we don't need anymore.
        thread {
            returnCode = dao.insertWord(word)
            if(returnCode<0){
                Log.e("DB", "Fail to insert word $returnCode")
            }else{
                Log.i("DB", "Insert word n°$returnCode")
            }
        }
        return returnCode
    }

    fun loadAllWords(){
        thread {
            words = dao.loadAllWord()
        }
    }

    fun deleteOneWord(url: String){
        thread {
            dao.deleteOneWord(url)
        }
    }

    fun deleteAllWords(){
        thread {
            dao.deleteAllWords()
        }
    }

    // ================================= Dictionary's functions ====================================
    fun insertDictionary(dict: Dictionary): Long{
        var returnCode: Long = 500
        thread {
            returnCode = dao.insertDictionary(dict)
            if(returnCode<0){
                Log.e("DB", "Fail to insert dict $returnCode")
            }else{
                Log.i("DB", "Insert dict n°$returnCode")
            }
        }
        return returnCode
    }

    fun deleteDictionary(name: String) {
        Thread {
            dao.deleteOneDict(name)
        }.start()
    }

    fun loadAllDictionary(){
        thread {
            dictionaries = dao.loadAllDictionary()
        }
    }

    fun deleteAllDictionary(){
        thread {
            dao.deleteAllDict()
        }
    }

    fun deleteOneDictionary(dict: String){
        thread {
            dao.deleteOneDict(dict)
        }
    }

    // ================================= Language's functions ======================================

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