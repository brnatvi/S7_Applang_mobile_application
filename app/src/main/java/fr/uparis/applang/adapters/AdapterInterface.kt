package fr.uparis.applang.adapters

import android.graphics.Color
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi

interface AdapterInterface<T>{
    var allItems : List<T>
    var selectedItems : MutableList<T>

    // ====================== Auxiliary functions ======================================================

    fun getColorSelected (): Int { return Color.argb(50, 254, 28, 85) }
    fun getColorOdd (): Int { return Color.argb(20, 240, 248, 255) }
    fun getColorEven (): Int { return Color.argb(20, 0, 255, 255) }

    @RequiresApi(Build.VERSION_CODES.O)
    fun painting(it: View, position: Int) {
        if (position%2 == 0) {
            it.setBackgroundColor(getColorEven())
        } else {
            it.setBackgroundColor(getColorOdd())
        }
    }
}