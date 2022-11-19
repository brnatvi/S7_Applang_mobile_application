package fr.uparis.applang

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Dictionary(@PrimaryKey val name: String, val url: String, val requestComposition: String)
