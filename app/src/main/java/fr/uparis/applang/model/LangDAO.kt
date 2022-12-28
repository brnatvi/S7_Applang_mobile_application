package fr.uparis.applang.model

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface LangDAO {

    // ======================= INSERT ===================================
    @Insert(entity = Language::class, onConflict = OnConflictStrategy.IGNORE)
    fun insertLanguage(language: Language):Long

    @Insert(entity = Language::class, onConflict = OnConflictStrategy.IGNORE)
    fun insertLanguages(vararg language: Language):List<Long>

    @Insert(entity = Word::class, onConflict = OnConflictStrategy.IGNORE)
    fun insertWord(word: Word):Long

    @Insert(entity = Dictionary::class, onConflict = OnConflictStrategy.IGNORE)
    fun insertDictionary(dictionary: Dictionary):Long

    // ======================= SELECT ===================================

    @Query("SELECT * FROM word")
    fun loadAllWord(): LiveData<List<Word>>

    @Query("SELECT * FROM word WHERE langDest=:langDest")
    fun loadAllWordLangDest(langDest: String): LiveData<List<Word>>

    @Query("SELECT * FROM language")
    fun loadAllLanguage(): LiveData<List<Language>>

    @Query("SELECT * FROM dictionary")
    fun loadAllDictionary(): LiveData<List<Dictionary>>

    // ======================= DELETE ===================================

    @Query("DELETE FROM dictionary")
    fun deleteAllDict()
    @Query("DELETE FROM dictionary WHERE name=:name")
    fun deleteOneDict(name: String)

    @Query("DELETE FROM language")
    fun deleteAllLang()

    @Query("DELETE FROM word WHERE text=:textName")
    fun deleteOneWord(textName: String)
    @Query("DELETE FROM word")
    fun deleteAllWords()
}