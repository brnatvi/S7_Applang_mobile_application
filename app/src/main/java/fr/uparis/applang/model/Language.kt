package fr.uparis.applang.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Language(@PrimaryKey val id: String, val fullName: String){}
