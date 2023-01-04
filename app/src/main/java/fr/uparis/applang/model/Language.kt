package fr.uparis.applang.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Data base object to store language
 */
@Entity
data class Language(@PrimaryKey val id: String, val fullName: String){
    override fun toString(): String = fullName
}
