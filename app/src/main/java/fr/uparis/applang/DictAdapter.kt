package fr.uparis.applang

import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import fr.uparis.applang.databinding.ItemLayoutBinding
import fr.uparis.applang.model.Dictionary


class DictAdapter() : RecyclerView.Adapter<DictAdapter.DictViewHolder>() {

    var listDictionaries : List<Dictionary> = listOf()
    val checkedItems = mutableListOf<Dictionary>()

    @RequiresApi(Build.VERSION_CODES.O)
    private val colorChecked = Color.argb(0.5f, 0.2f, 0.2f, 0.2f)
    @RequiresApi(Build.VERSION_CODES.O)
    private val colorOdd = Color.argb(20, 240, 248, 255)
    @RequiresApi(Build.VERSION_CODES.O)
    private val colorEven = Color.argb(20, 0, 255, 255)

    inner class DictViewHolder (val binding: ItemLayoutBinding) : RecyclerView.ViewHolder(binding.root){
        lateinit var dictionary: Dictionary
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DictViewHolder {
        val bindDict = ItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = DictViewHolder(bindDict)
        return holder
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: DictViewHolder, position: Int) {
        holder.dictionary = listDictionaries[position]
        if (position%2 == 0) {
            holder.itemView.setBackgroundColor(colorEven)
        } else {
            holder.itemView.setBackgroundColor(colorOdd)
        }
        holder.binding.nom.text = holder.dictionary.name
    }

    override fun getItemCount(): Int {
        return listDictionaries.size
    }

}