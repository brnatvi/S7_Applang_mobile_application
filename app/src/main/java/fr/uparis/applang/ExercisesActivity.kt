package fr.uparis.applang

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import fr.uparis.applang.adapters.WordAdapter
import fr.uparis.applang.databinding.ExercisesLayoutBinding

class ExercisesActivity: OptionsMenuActivity() {
    private lateinit var binding: ExercisesLayoutBinding
    private lateinit var menu: Toolbar
    private val model by lazy { ViewModelProvider(this)[ViewModel::class.java] }
    private val adapterExers by lazy { WordAdapter() }

    val TAG: String = "EXERS ===="

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // create binding
        binding = ExercisesLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // menu toolbar
        menu =  findViewById(R.id.acc_toolbar)
        setSupportActionBar(menu)
        menu.setTitle(R.string.app_name)

        // RecyclerView for Dictionaries
        binding.recyclerViewWords.adapter = adapterExers
        binding.recyclerViewWords.layoutManager = LinearLayoutManager(this)

        model.words.observe(this){
            adapterExers.listWords = it
            adapterExers.notifyDataSetChanged()
        }
    }

    // ================================= Buttons' functions =============================================
    fun nettoyerListWords(view: View) {
        model.deleteAllWords()
        adapterExers.notifyDataSetChanged()
    }

    fun enleverMot(view: View) {
        val len = adapterExers.selectedItems.size
        for (index in 0..len-1) {
            model.deleteOneWord(adapterExers.selectedItems[index].text)
            adapterExers.notifyItemRemoved(index)
        }
    }
}