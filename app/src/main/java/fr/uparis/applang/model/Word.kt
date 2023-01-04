package fr.uparis.applang.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Data base object to store Word
 * Some word may have been save with different destination language, so only URL is unique.
 */
@Entity
data class Word(val text: String, val langSrc: String, val langDest: String, @PrimaryKey val tradURL: String, var correctGuess: Int=0){
    /** Return this word as a notification sentence. */
    fun toNotificationString(): String = "Traduisez \"$text\"."
}
