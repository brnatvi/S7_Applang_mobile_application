package fr.uparis.applang.model

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface LangDAO {
    @Insert(entity = Language::class, onConflict = OnConflictStrategy.IGNORE)
    fun insertLanguage(language: Language):Long
    @Insert(entity = Word::class, onConflict = OnConflictStrategy.IGNORE)
    fun insertWord(word: Word):Long
    @Insert(entity = Dictionary::class, onConflict = OnConflictStrategy.IGNORE)
    fun insertDictionary(dictionary: Dictionary):Long

    @Query("SELECT * FROM word")
    fun loadAllWord(): LiveData<List<Word>>
}