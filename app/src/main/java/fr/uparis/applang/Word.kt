package fr.uparis.applang

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.net.URL

@Entity
data class Word(@PrimaryKey val text: String, val langSrc: String, val langDest: String, val tradURL: String)
