package fr.uparis.applang.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import fr.uparis.applang.databinding.Item3FieldsLayoutBinding
import fr.uparis.applang.model.Word

/**
 * RecyclerView Adapter for word
 */
class WordAdapter: RecyclerView.Adapter<WordAdapter.WordViewHolder>(), AdapterInterface<Word>  {

    override var allItems: List<Word> = listOf()
    override var selectedItems: MutableList<Word> = mutableListOf()

    inner class WordViewHolder (val binding: Item3FieldsLayoutBinding) : RecyclerView.ViewHolder(binding.root){
        lateinit var word: Word
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val bindWord = Item3FieldsLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WordViewHolder(bindWord)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        // save reference for word in holder
        holder.word= allItems[position]

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

        holder.binding.text.text = holder.word.text
        holder.binding.langSRC.text = holder.word.langSrc
        holder.binding.langDest.text = holder.word.langDest
    }

    override fun getItemCount(): Int {
        return allItems.size
    }
}