package fr.uparis.applang.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import fr.uparis.applang.databinding.Item1FieldLayoutBinding
import fr.uparis.applang.model.Language

class LangAdapter: RecyclerView.Adapter<LangAdapter.LangViewHolder>(), AdapterInterface<Language> {

    override var allItems: List<Language> = listOf()
    override var selectedItems: MutableList<Language> = mutableListOf()

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
        // save reference for langue in holder
        holder.language = allItems[position]

        if (position%2 == 0) {
            holder.itemView.setBackgroundColor(getColorEven())
        } else {
            holder.itemView.setBackgroundColor(getColorOdd())
        }

        holder.itemView.setOnClickListener {
            if (selectedItems.contains(allItems[position])){
                selectedItems.remove(allItems[position])
                painting(it, position)
            }
            else {
                holder.itemView.setBackgroundColor(getColorSelected())
                selectedItems.add(allItems[position])
            }
        }
        holder.binding.nom.text = holder.language.fullName
    }

    override fun getItemCount(): Int {
        return allItems.size
    }
}