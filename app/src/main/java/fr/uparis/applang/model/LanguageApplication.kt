package fr.uparis.applang.model

import android.app.Application

/**
 * Main Application with a link to database.
 */
class LanguageApplication: Application() {
    val database by lazy { LangDB.getDatabase(this) }
}