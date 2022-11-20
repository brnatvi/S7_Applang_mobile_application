package fr.uparis.applang.model

import android.app.Application
import fr.uparis.applang.model.LangDB

class LanguageApplication: Application() {
    val database by lazy { LangDB.getDatabase(this) }
}