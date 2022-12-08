package fr.uparis.applang

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import fr.uparis.applang.databinding.ItemLayoutBinding
import fr.uparis.applang.model.Dictionary


class DictAdapter() : RecyclerView.Adapter<DictAdapter.DictViewHolder>() {

    private var listDictionaries : List<Dictionary> = listOf()

    inner class DictViewHolder (val binding: ItemLayoutBinding) : RecyclerView.ViewHolder(binding.root){
        lateinit var dictionary: Dictionary
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DictViewHolder {
        val bind = ItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = DictViewHolder(bind)
        return holder
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: DictViewHolder, position: Int) {
        holder.dictionary = listDictionaries[position]
        holder.binding.nom.text = holder.dictionary.name
       /* with(holder.binding) {
            nom.text = holder.dictionary.name
           // holder.dictionary.url
           // holder.dictionary.requestComposition
        }*/
    }

    override fun getItemCount(): Int {
        return listDictionaries.size
    }

    fun setListeDictionnaries(listDict: List<Dictionary>){
        listDictionaries = listDict
    }
}