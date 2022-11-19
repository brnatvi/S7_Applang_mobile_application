package fr.uparis.applang

import android.app.Application

class LanguageApplication: Application() {
    val database by lazy { LangDB.getDatabase(this) }
}