package fr.uparis.applang

import androidx.room.Entity

@Entity
data class Language(val id: Int, val fullName: String){}
