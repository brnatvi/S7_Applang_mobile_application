package fr.uparis.applang.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
/*
data class Dictionary(@PrimaryKey val name: String, val url: String, val langSrc: String, val langDest: String){
    override fun toString(): String = name
}*/

data class Dictionary(@PrimaryKey val name: String, val url: String, val requestComposition: String){
    override fun toString(): String = name
    fun toStringFull(): String {
        return "$name $url $requestComposition";
    }
}
