package fr.uparis.applang.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import fr.uparis.applang.databinding.Item1FieldLayoutBinding
import fr.uparis.applang.model.Dictionary

class DictAdapter: RecyclerView.Adapter<DictAdapter.DictViewHolder>(), AdapterInterface<Dictionary>  {

    override var allItems: List<Dictionary> = listOf()
    override var selectedItems: MutableList<Dictionary> = mutableListOf()

    inner class DictViewHolder (val binding: Item1FieldLayoutBinding) : RecyclerView.ViewHolder(binding.root){
        lateinit var dictionary: Dictionary
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DictViewHolder {
        val bindDict = Item1FieldLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = DictViewHolder(bindDict)
        return holder
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: DictViewHolder, position: Int) {
        // save reference for dict in holder
        holder.dictionary = allItems[position]

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
        holder.binding.nom.text = holder.dictionary.name
    }

    override fun getItemCount(): Int {
        return allItems.size
    }

    // ====================== Auxiliary functions ======================================================

    fun getOneDictSelected(): Dictionary? {
        if (selectedItems.size == 1) return selectedItems.get(0)
        return null
    }
}