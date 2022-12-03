package fr.uparis.applang.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Word(val text: String, val langSrc: String, val langDest: String, @PrimaryKey val tradURL: String)
