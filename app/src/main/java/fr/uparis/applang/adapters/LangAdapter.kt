package fr.uparis.applang.adapters

import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import fr.uparis.applang.databinding.Item1FieldLayoutBinding
import fr.uparis.applang.model.Language


class LangAdapter : RecyclerView.Adapter<LangAdapter.LangViewHolder>() {

    var listLangues : List<Language> = listOf()
    var selectedItems = mutableListOf<Language>()

    @RequiresApi(Build.VERSION_CODES.O)
    private val colorSelected = Color.argb(50, 254, 28, 85)
    @RequiresApi(Build.VERSION_CODES.O)
    private val colorOdd = Color.argb(20, 240, 248, 255)
    @RequiresApi(Build.VERSION_CODES.O)
    private val colorEven = Color.argb(20, 0, 255, 255)

    inner class LangViewHolder (val binding: Item1FieldLayoutBinding) : RecyclerView.ViewHolder(binding.root){
        lateinit var language: Language
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LangViewHolder {
        val bindLang = Item1FieldLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = LangViewHolder(bindLang)
        return holder
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: LangViewHolder, position: Int) {
        // save reference for dict in holder
        holder.language = listLangues[position]

        if (position%2 == 0) {
            holder.itemView.setBackgroundColor(colorEven)
        } else {
            holder.itemView.setBackgroundColor(colorOdd)
        }

        holder.itemView.setOnClickListener {
            if (selectedItems.contains(listLangues[position])){
                selectedItems.remove(listLangues[position])
                painting(it, position)
            }
            else {
                holder.itemView.setBackgroundColor(colorSelected)
                selectedItems.add(listLangues[position])
            }
        }
        holder.binding.nom.text = holder.language.fullName
    }

    override fun getItemCount(): Int {
        return listLangues.size
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
}