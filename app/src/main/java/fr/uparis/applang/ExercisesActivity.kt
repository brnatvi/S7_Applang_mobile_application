package fr.uparis.applang

import android.app.AlertDialog
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
    private val adapterWords by lazy { WordAdapter() }

    val TAG: String = "EXERS ===="

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCurrentActivity(this)

        // create binding
        binding = ExercisesLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // menu toolbar
        menu =  findViewById(R.id.acc_toolbar)
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
        sharedPref = getSharedPreferences("fr.uparis.applang", MODE_PRIVATE) // common preferences for all activities
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

        // fil current settings on activity's fields
        if (savedInstanceState != null){       //if configuration changes or activity goes into background
            val lun   = savedInstanceState.getInt(keyLundi   )
            val mar   = savedInstanceState.getInt(keyMardi   )
            val mer   = savedInstanceState.getInt(keyMercredi)
            val jeu   = savedInstanceState.getInt(keyJeudi   )
            val ven   = savedInstanceState.getInt(keyVendredi)
            val sam   = savedInstanceState.getInt(keySamedi  )
            val dim   = savedInstanceState.getInt(keyDimanche)
            postValuesToSpinners (lun, mar, mer, jeu, ven, sam, dim)

            val frec  = savedInstanceState.getInt(keyFrequency)
            if (frec == 0) { binding.frequenseET.setText("1") }
            else           { binding.frequenseET.setText(frec.toString()) }

            val quant = savedInstanceState.getInt(keyQuantity )
            if (quant == 0) { binding.nbMotsET.setText("10") }
            else            { binding.nbMotsET   .setText(quant.toString()) }
        } else {
            // initialize fields of Activity by values from Shared Preferences
            restoreFields()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(keyLundi    , binding.lundiSP    .selectedItemPosition)
        outState.putInt(keyMardi    , binding.mardiSP    .selectedItemPosition)
        outState.putInt(keyMercredi , binding.mercrediSP .selectedItemPosition)
        outState.putInt(keyJeudi    , binding.jeudiSP    .selectedItemPosition)
        outState.putInt(keyVendredi , binding.vendrediSP .selectedItemPosition)
        outState.putInt(keySamedi   , binding.samediSP   .selectedItemPosition)
        outState.putInt(keyDimanche , binding.dimancheSP .selectedItemPosition)
        if (!binding.frequenseET.text.isEmpty()) {
            outState.putInt(keyFrequency, binding.nbMotsET.text.toString().toInt())
        }
        if (!binding.nbMotsET.text.isEmpty()) {
            outState.putInt(keyQuantity, binding.frequenseET.text.toString().toInt())
        }
    }

    // ================================= Buttons' functions =============================================
    fun nettoyerListWords(view: View) {
        model.deleteAllWords()
        adapterWords.notifyDataSetChanged()
    }

    fun enleverMot(view: View) {
        val len = adapterWords.selectedItems.size

        if (len == 0) {
            AlertDialog.Builder(this)
                .setMessage("Veuillez choisir le.s mot à enlever")
                .setPositiveButton("Ok") { dialog, _ ->
                    dialog.dismiss()
                }.setCancelable(false)
                .show()
        } else {
            for (index in 0..len - 1) {
                model.deleteOneWord(adapterWords.selectedItems[index].text)
                adapterWords.notifyItemRemoved(index)
                adapterWords.selectedItems.clear()
            }
        }
    }

    fun appliquerParametrage(view: View) {
        sharedPrefEditor.putInt(keyLundi,    binding.lundiSP   .selectedItemPosition)
                        .putInt(keyMardi,    binding.mardiSP   .selectedItemPosition)
                        .putInt(keyMercredi, binding.mercrediSP.selectedItemPosition)
                        .putInt(keyJeudi,    binding.jeudiSP   .selectedItemPosition)
                        .putInt(keyVendredi, binding.vendrediSP.selectedItemPosition)
                        .putInt(keySamedi,   binding.samediSP  .selectedItemPosition)
                        .putInt(keyDimanche, binding.dimancheSP.selectedItemPosition)
        if (!binding.frequenseET.text.isEmpty()) {
            sharedPrefEditor.putInt(keyFrequency, binding.frequenseET.text.toString().toInt())
        }
        if (!binding.nbMotsET.text.isEmpty()) {
            sharedPrefEditor.putInt(keyQuantity, binding.nbMotsET.text.toString().toInt())
        }
        sharedPrefEditor.commit()
        startNotificationService()
    }

    fun reinitParametrage(view: View) {
        // save parameters
        sharedPrefEditor.putInt(keyLundi,     0)
                        .putInt(keyMardi,     0)
                        .putInt(keyMercredi,  0)
                        .putInt(keyJeudi,     0)
                        .putInt(keyVendredi,  0)
                        .putInt(keySamedi,    0)
                        .putInt(keyDimanche,  0)
                        .putInt(keyFrequency, 1)
                        .putInt(keyQuantity,  10)
                        .commit()
        // update fields
        postValuesToSpinners (0, 0, 0, 0, 0, 0, 0)
        binding.frequenseET.setText("")
        binding.nbMotsET   .setText("")
    }

    // ====================== Auxiliary functions ======================================================

    // initialize fields of Activity by values from Shared Preferences
    private fun restoreFields () {
        // restore Settings of exercises in activity's fields
        val lun   = sharedPref.getInt(keyLundi   , 0)
        val mar   = sharedPref.getInt(keyMardi   , 0)
        val mer   = sharedPref.getInt(keyMercredi, 0)
        val jeu   = sharedPref.getInt(keyJeudi   , 0)
        val ven   = sharedPref.getInt(keyVendredi, 0)
        val sam   = sharedPref.getInt(keySamedi  , 0)
        val dim   = sharedPref.getInt(keyDimanche, 0)
        postValuesToSpinners (lun, mar, mer, jeu, ven, sam, dim)

        val frec  = sharedPref.getInt(keyFrequency, 1)
        val quant = sharedPref.getInt(keyQuantity , 10)
        binding.nbMotsET   .setText(quant.toString())
        binding.frequenseET.setText(frec.toString())
    }

    // post values into spinners
    private fun postValuesToSpinners (lun: Int, mar: Int, mer: Int, jeu: Int, ven: Int, sam: Int, dim: Int) {
        binding.lundiSP    .post { binding.lundiSP.setSelection(lun) }
        binding.mardiSP    .post { binding.mardiSP.setSelection(mar) }
        binding.mercrediSP .post { binding.mercrediSP.setSelection(mer) }
        binding.jeudiSP    .post { binding.jeudiSP.setSelection(jeu) }
        binding.vendrediSP .post { binding.vendrediSP.setSelection(ven) }
        binding.samediSP   .post { binding.samediSP.setSelection(sam) }
        binding.dimancheSP .post { binding.dimancheSP.setSelection(dim) }
    }


}