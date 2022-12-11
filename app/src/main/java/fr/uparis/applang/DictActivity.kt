package fr.uparis.applang

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import fr.uparis.applang.databinding.ActivityDictBinding
import fr.uparis.applang.model.Dictionary


class DictActivity  : OptionsMenuActivity() {
    private lateinit var bindingDict: ActivityDictBinding
    private lateinit var menu: Toolbar

    private lateinit var sharedPref : SharedPreferences
    private lateinit var sharedPrefEditor: SharedPreferences.Editor
    private val key: String = "activity"
    private val optionDict: String = "dictActivity"

    private val model by lazy {  ViewModelProvider(this).get(DictViewModel::class.java) }
    private val adapter by lazy { DictAdapter() }

    private var name = ""
    private var url = ""
    private val dictSelected = null
    private val GOOGLE_SEARCH_PATH : String = "https://www.google.com/search?q="


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_dict)
        bindingDict = ActivityDictBinding.inflate(layoutInflater)
        setContentView(bindingDict.root)

        bindingDict.recyclerView.adapter = adapter
        bindingDict.recyclerView.layoutManager = LinearLayoutManager(this)

        model.allDictionnaries.observe(this){
            adapter.listDictionaries = it
            adapter.notifyDataSetChanged()
        }
/*
        model.dictionaries.observe(this){
            adapter.dictionaries = it
            adapter.notifyDataSetChanged()
        }

        bindingDict.pays.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int)
            {            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int)
            {            }

            override fun afterTextChanged(s: Editable?) {
                model.loadPartialName(s.toString())
            }
        })
*/




        // menu toolbar
        menu =  findViewById(R.id.acc_toolbar)
        setSupportActionBar(menu)
        menu.setTitle(R.string.app_name)

        // shared preferences
        sharedPref = getSharedPreferences("fr.uparis.applang", MODE_PRIVATE)
        Log.d("DICT: ShPr === ", sharedPref.toString())
        sharedPrefEditor = sharedPref.edit()
        val jj = sharedPref.getString(key, "")
        Log.d("DICT: activity1 === ", jj!!)

        loadDictPreferencies()

        when {
            intent?.action == Intent.ACTION_SEND -> {
                if ("text/plain" == intent.type) {
                    saveDictPreferencies()
                    handleReceivedText(intent)
                }
            }
            else -> {
                // Handle other intents, such as being started from the home screen
            }
        }

    }
/*
    override fun onPause() {
        sharedPrefEditor.putString(key, optionDict).commit()
        val jj = sharedPref.getString(key, "")
        Log.d("DICT: activ === ", jj!!)
        super.onPause()
    }
    */
    val TAG: String = "TEST DICT ======"

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
    }

    override fun onStop(){
        super.onStop();
        Log.d(TAG, "onStop")
        }

    override fun onStart(){
        super.onStart();
        Log.d(TAG, "onStart")
        }

    override fun onPause(){
        super.onPause();
        Log.d(TAG, "onPause")
        }

    override fun onResume() {
        super.onResume();
        Log.d(TAG, "onResume")
    }

    override fun onRestart(){
        super.onRestart();
        Log.d(TAG, "onRestart")
    }

    // ================================= Buttons' functions =============================================
    fun enleverDict(view: View) {

    }

    fun ajouterDict(view: View) {
        val name = bindingDict.nomDictET.text.toString().trim()
        val url = bindingDict.lienDictET.text.toString().trim()
        val requestComp = bindingDict.requestCompDictET.text.toString().trim()

        if ( (name == "") || (url == "") ) {
            AlertDialog.Builder(this)
                .setMessage("Merci d'insérer le nom du dictionnaire ET son lien internet")
                .setPositiveButton("Ok", DialogInterface.OnClickListener {
                        dialog, id -> finish()
                }).setCancelable(false)
                .show()
            return
        }

        model.insertDictionnary(name, url, requestComp)

        with(bindingDict) {
            nomDictET.text.clear()
            lienDictET.text.clear()
            requestCompDictET.text.clear()
        }
    }


    fun chercherDict(view: View) {
        sharedPrefEditor.putString(key, optionDict).commit()
        val jj = sharedPref.getString(key, "")
        Log.d("DICT: activity2 === ", jj!!)

        val namedict = bindingDict.nomDictET.text.toString().lowercase()
        if (namedict == "") {
            AlertDialog.Builder(this)
                .setMessage("Merci d'insérer le nom du dictionnaire")
                .setPositiveButton("Ok", DialogInterface.OnClickListener {
                        dialog, id -> finish()
                }).setCancelable(false)
                .show()
            return
        }

        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(GOOGLE_SEARCH_PATH + namedict))
        startActivity(browserIntent)
    }


    // ================================== Save / load activity state ==========================================

    private fun saveDictPreferencies() {
        sharedPrefEditor.putString("name", name)
        sharedPrefEditor.putString("url", url)
        sharedPrefEditor.commit()
    }

    private fun loadDictPreferencies() {
        name = sharedPref.getString("name", "")!!
        url = sharedPref.getString("url", "")!!
    }

    // =================================== Share processing ======================================================

    // handling the received data from the "share" process
    private fun handleReceivedText(intent: Intent) {
        intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
            loadDictPreferencies()
            bindingDict.lienDictET.setText(it)
        }
    }


}