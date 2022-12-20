package fr.uparis.applang.adapters

import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import fr.uparis.applang.databinding.ItemLayoutBinding
import fr.uparis.applang.model.Word

class WordAdapter : RecyclerView.Adapter<WordAdapter.WordViewHolder>() {

        var listWords : List<Word> = listOf()
        var selectedItems = mutableListOf<Word>()

        @RequiresApi(Build.VERSION_CODES.O)
        private val colorSelected = Color.argb(50, 254, 28, 85)
        @RequiresApi(Build.VERSION_CODES.O)
        private val colorOdd = Color.argb(20, 240, 248, 255)
        @RequiresApi(Build.VERSION_CODES.O)
        private val colorEven = Color.argb(20, 0, 255, 255)


        inner class WordViewHolder (val binding: ItemLayoutBinding) : RecyclerView.ViewHolder(binding.root){
            lateinit var word: Word
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
            val bindWord = ItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            val holder = WordViewHolder(bindWord)
            return holder
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
            // save reference for dict in holder
            holder.word= listWords[position]

            if (position%2 == 0) {
                holder.itemView.setBackgroundColor(colorEven)
            } else {
                holder.itemView.setBackgroundColor(colorOdd)
            }

            holder.itemView.setOnClickListener {
                if (selectedItems.contains(listWords[position])){
                    selectedItems.remove(listWords[position])
                    painting(it, position)
                }
                else {
                    holder.itemView.setBackgroundColor(colorSelected)
                    selectedItems.add(listWords[position])
                }
            }
            holder.binding.nom.text = holder.word.text
        }

        override fun getItemCount(): Int {
            return listWords.size
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