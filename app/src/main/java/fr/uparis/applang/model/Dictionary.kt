package fr.uparis.applang.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Data base object to store Dictionary
 */
@Entity
data class Dictionary(@PrimaryKey val name: String, val url: String, val requestComposition: String){
    /** User friendly to string. */
    override fun toString(): String = name
    /** Not user friendly to string for log. */
    fun toStringFull(): String {
        return "$name $url $requestComposition"
    }
}
