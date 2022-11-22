package fr.uparis.applang

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import fr.uparis.applang.model.LanguageApplication
import fr.uparis.applang.model.Word
import kotlin.concurrent.thread

class MainViewModel(application: Application): AndroidViewModel(application) {
    val dao = (application as LanguageApplication).database.langDAO()

    var words: LiveData<List<Word>> = dao.loadAllWord()

    fun insertWord(word: Word){
        thread {
            val returnCode = dao.insertWord(word)
            if(returnCode<0){
                Log.e("DB", "Fail to insert word $returnCode")
            }else{
                Log.i("DB", "Insert word n°$returnCode")
            }
        }
    }

    fun loadAllWord(){
        thread {
            words = dao.loadAllWord()
        }
    }
}

//class MainViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
//    @Suppress("unchecked_cast")
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
//            return MainViewModel(application) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}