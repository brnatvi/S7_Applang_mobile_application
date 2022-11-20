package fr.uparis.applang.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Word(@PrimaryKey val text: String, val langSrc: String, val langDest: String, val tradURL: String)
