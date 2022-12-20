package fr.uparis.applang.adapters

import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import fr.uparis.applang.databinding.ItemLayoutBinding
import fr.uparis.applang.model.Dictionary


class DictAdapter : RecyclerView.Adapter<DictAdapter.DictViewHolder>() {

    var listDictionaries : List<Dictionary> = listOf()
    var selectedItems = mutableListOf<Dictionary>()

    @RequiresApi(Build.VERSION_CODES.O)
    private val colorSelected = Color.argb(50, 254, 28, 85)
    @RequiresApi(Build.VERSION_CODES.O)
    private val colorOdd = Color.argb(20, 240, 248, 255)
    @RequiresApi(Build.VERSION_CODES.O)
    private val colorEven = Color.argb(20, 0, 255, 255)


    inner class DictViewHolder (val binding: ItemLayoutBinding) : RecyclerView.ViewHolder(binding.root){
        lateinit var dictionary: Dictionary
}

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DictViewHolder {
        val bindDict = ItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = DictViewHolder(bindDict)
        return holder
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: DictViewHolder, position: Int) {
        // save reference for dict in holder
        holder.dictionary = listDictionaries[position]

        if (position%2 == 0) {
            holder.itemView.setBackgroundColor(colorEven)
        } else {
            holder.itemView.setBackgroundColor(colorOdd)
        }

        holder.itemView.setOnClickListener {
            if (selectedItems.contains(listDictionaries[position])){
                selectedItems.remove(listDictionaries[position])
                painting(it, position)
            }
            else {
                holder.itemView.setBackgroundColor(colorSelected)
                selectedItems.add(listDictionaries[position])
            }
        }
        holder.binding.nom.text = holder.dictionary.name
    }

    override fun getItemCount(): Int {
        return listDictionaries.size
    }

    // ====================== Auxiliary functions ======================================================

    @RequiresApi(Build.VERSION_CODES.O)
    private fun painting(it: View, position: Int) {
        if (position%2 == 0) {
            it.setBackgroundColor(colorEven)
        } else {
            it.setBackgroundColor(colorOdd)
        }
    }

    fun getOneDictSelected(): Dictionary? {
        if (selectedItems.size == 1) return selectedItems.get(0)
        return null
    }
}