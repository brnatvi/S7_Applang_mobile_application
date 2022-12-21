package fr.uparis.applang

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import fr.uparis.applang.adapters.WordAdapter
import fr.uparis.applang.adapters.SpinnerAdapter
import fr.uparis.applang.databinding.ExercisesLayoutBinding
import fr.uparis.applang.navigation.OptionsMenuActivity


class ExercisesActivity: OptionsMenuActivity() {
    private lateinit var binding: ExercisesLayoutBinding
    private lateinit var menu: Toolbar
    private val model by lazy { ViewModelProvider(this)[ViewModel::class.java] }
    private val adapterWords by lazy { WordAdapter() }


    val TAG: String = "EXERS ===="

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCurrentActivity(this)

        // create binding
        binding = ExercisesLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // menu toolbar
        menu =  findViewById(fr.uparis.applang.R.id.acc_toolbar)
        setSupportActionBar(menu)
        menu.setTitle(R.string.app_name)

        // RecyclerView for Dictionaries
        binding.wordsRW.adapter = adapterWords
        binding.wordsRW.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        model.words.observe(this){
            adapterWords.allItems = it
            adapterWords.notifyDataSetChanged()
        }

        // SharedPreferences
        sharedPref = getSharedPreferences("fr.uparis.applang", MODE_PRIVATE)                  // common preferences for all activities
        sharedPrefEditor = sharedPref.edit()

        // init spinners
        model.languages.observe(this) {
            SpinnerAdapter(binding.lundiSP, this, it)
            SpinnerAdapter(binding.mardiSP, this, it)
            SpinnerAdapter(binding.mercrediSP, this, it)
            SpinnerAdapter(binding.jeudiSP, this, it)
            SpinnerAdapter(binding.vendrediSP, this, it)
            SpinnerAdapter(binding.samediSP, this, it)
            SpinnerAdapter(binding.dimancheSP, this, it)
        }

        // show current settings on activity's fields
        restoreFields()

    }

    // ================================= Buttons' functions =============================================
    fun nettoyerListWords(view: View) {
        model.deleteAllWords()
        adapterWords.notifyDataSetChanged()
    }

    fun enleverMot(view: View) {
        val len = adapterWords.selectedItems.size
        for (index in 0..len-1) {
            model.deleteOneWord(adapterWords.selectedItems[index].text)
            adapterWords.notifyItemRemoved(index)
        }
    }

    fun appliquerParametrage(view: View) {
        sharedPrefEditor.putInt(keyLundi,    binding.lundiSP.getSelectedItemPosition())
                        .putInt(keyMardi,    binding.mardiSP.getSelectedItemPosition())
                        .putInt(keyMercredi, binding.mercrediSP.getSelectedItemPosition())
                        .putInt(keyJeudi,    binding.jeudiSP.getSelectedItemPosition())
                        .putInt(keyVendredi, binding.vendrediSP.getSelectedItemPosition())
                        .putInt(keySamedi,   binding.samediSP.getSelectedItemPosition())
                        .putInt(keyDimanche, binding.dimancheSP.getSelectedItemPosition())
        if (!binding.frequenseET.text.isEmpty()) {
            sharedPrefEditor.putInt(keyFrequency, binding.frequenseET.text.toString().toInt())
        }
        if (!binding.nbMotsET.text.isEmpty()) {
            sharedPrefEditor.putInt(keyQuantity, binding.nbMotsET.text.toString().toInt())
        }
        sharedPrefEditor.commit()
    }

    fun reinitParametrage(view: View) {
        sharedPrefEditor.putInt(keyLundi,    0)
                        .putInt(keyMardi,    0)
                        .putInt(keyMercredi, 0)
                        .putInt(keyJeudi,    0)
                        .putInt(keyVendredi, 0)
                        .putInt(keySamedi,   0)
                        .putInt(keyDimanche, 0)
                        .putInt(keyFrequency, 1)
                        .putInt(keyQuantity,  10)
                        .commit()
        binding.lundiSP    .post( { binding.lundiSP.setSelection(0) })
        binding.mardiSP    .post( { binding.mardiSP.setSelection(0) })
        binding.mercrediSP .post( { binding.mercrediSP.setSelection(0) })
        binding.jeudiSP    .post( { binding.jeudiSP.setSelection(0) })
        binding.vendrediSP .post( { binding.vendrediSP.setSelection(0) })
        binding.samediSP   .post( { binding.samediSP.setSelection(0) })
        binding.dimancheSP .post( { binding.dimancheSP.setSelection(0) })
        binding.frequenseET.setText("")
        binding.nbMotsET   .setText("")
    }

    // ====================== Auxiliary functions ======================================================
    private fun restoreFields () {
        // restore Settings of exercises in activity's fields
        val frec  = sharedPref.getInt(keyFrequency, 1)
        val quant = sharedPref.getInt(keyQuantity, 10)
        val lun   = sharedPref.getInt(keyLundi, 0)
        val mar   = sharedPref.getInt(keyMardi, 0)
        val mer   = sharedPref.getInt(keyMercredi, 0)
        val jeu   = sharedPref.getInt(keyJeudi, 0)
        val ven   = sharedPref.getInt(keyVendredi, 0)
        val sam   = sharedPref.getInt(keySamedi, 0)
        val dim   = sharedPref.getInt(keyDimanche, 0)

        binding.lundiSP.post( { binding.lundiSP.setSelection(lun) })
        binding.mardiSP.post( { binding.mardiSP.setSelection(mar) })
        binding.mercrediSP.post( { binding.mercrediSP.setSelection(mer) })
        binding.jeudiSP.post( { binding.jeudiSP.setSelection(jeu) })
        binding.vendrediSP.post( { binding.vendrediSP.setSelection(ven) })
        binding.samediSP.post( { binding.samediSP.setSelection(sam) })
        binding.dimancheSP.post( { binding.dimancheSP.setSelection(dim) })

        binding.nbMotsET.setText(frec.toString())
        binding.frequenseET.setText(quant.toString())

    }

}