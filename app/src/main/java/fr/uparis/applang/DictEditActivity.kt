package fr.uparis.applang

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import fr.uparis.applang.adapters.DictAdapter
import fr.uparis.applang.databinding.EditLayoutBinding
import fr.uparis.applang.model.Dictionary
import fr.uparis.applang.navigation.OptionsMenuActivity

class DictEditActivity : OptionsMenuActivity()  {
    private lateinit var binding: EditLayoutBinding
    private lateinit var menu: Toolbar
    private val model by lazy { ViewModelProvider(this)[ViewModel::class.java] }
    private val adapter by lazy { DictAdapter() }

    private var initDictName = ""

    val TAG: String = "EDIT ======"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCurrentActivity(this)

        // create binding
        binding = EditLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // menu toolbar
        menu =  findViewById(R.id.acc_toolbar)
        setSupportActionBar(menu)
        menu.setTitle(R.string.app_name)

        // restore the fields' content
        initDictName = intent.getExtras()?.getString( keyDict ) ?: ""

        with(binding) {
            nameET.setText(initDictName)
            linkET.setText(intent.getExtras()?.getString( "url" ) ?: "")
            requestCompositionET.setText(intent.getExtras()?.getString("requestComposition" ) ?: "")
        }
    }

    // ================================= Buttons' functions =============================================
    fun sauvegarder(view: View) {
        model.deleteDictionary(initDictName)
        adapter.notifyDataSetChanged()

        val nameDict = binding.nameET.text.toString()
        val urlDict = binding.linkET.text.toString()
        val requestComposition = binding.requestCompositionET.text.toString()

        val ret = model.insertDictionary(Dictionary(nameDict, urlDict, requestComposition))
        adapter.notifyDataSetChanged()

        if (ret < 0) {
            makeToast(this, "Erreur d'insertion du dictionnaire '${nameDict}'")
        } else {
            makeToast(this, "Dictionnaire '${nameDict}' vient d'être ajouté")
            with(binding) {
                nameET.text.clear()
                linkET.text.clear()
                requestCompositionET.text.clear()
            }
            finish()
        }
    }
}