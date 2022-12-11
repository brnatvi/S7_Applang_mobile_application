package fr.uparis.applang.model

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface LangDAO {
    @Insert(entity = Language::class, onConflict = OnConflictStrategy.IGNORE)
    fun insertLanguage(language: Language):Long
    @Insert(entity = Language::class, onConflict = OnConflictStrategy.IGNORE)
    fun insertLanguages(vararg language: Language):List<Long>
    @Insert(entity = Word::class, onConflict = OnConflictStrategy.IGNORE)
    fun insertWord(word: Word):Long
    @Insert(entity = Dictionary::class, onConflict = OnConflictStrategy.IGNORE)
    fun insertDictionary(dictionary: Dictionary):Long

    @Query("SELECT * FROM word")
    fun loadAllWord(): LiveData<List<Word>>
    @Query("SELECT * FROM language")
    fun loadAllLanguage(): LiveData<List<Language>>
    @Query("SELECT * FROM dictionary")
    fun loadAllDictionary(): LiveData<List<Dictionary>>

    @Query("DELETE FROM dictionary")
    fun deleteAllDict()
    @Query("DELETE FROM dictionary WHERE name=:dict")
    fun deleteOneDict(dict: String)
    @Query("DELETE FROM language")
    fun deleteAllLang()
    @Query("DELETE FROM word WHERE tradURL=:url")
    fun deleteOneWord(url: String)
}