package fr.uparis.applang.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Language::class, Word::class, Dictionary::class], version = 4)
abstract class LangDB: RoomDatabase() {
    abstract fun langDAO(): LangDAO
    companion object{
        @Volatile
        private var instance: LangDB?=null

        fun getDatabase(context: Context): LangDB {
            if (instance != null)
                return instance!!

            val db =
                Room.databaseBuilder(context.applicationContext, LangDB::class.java, "langBD")
                    .fallbackToDestructiveMigration() /* bd will be destroy if version is update */
                    .build()
            instance = db
            return instance!!
        }
    }
}