package fr.uparis.applang

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import fr.uparis.applang.databinding.ActivityDictBinding


class DictActivity  : OptionsMenuActivity() {
    private lateinit var bindingDict: ActivityDictBinding
    private lateinit var menu: Toolbar

    private lateinit var sharedPref : SharedPreferences
    private lateinit var sharedPrefEditor: SharedPreferences.Editor
    private val keyActivity: String = "activity"
    private val keyShare: String = "linkShare"
    private val keyName: String = "nameDict"
    private val optionDict: String = "dictActivity"

    private val model by lazy {  ViewModelProvider(this).get(DictViewModel::class.java) }
    private val adapter by lazy { DictAdapter() }

    private var nameDict = ""
    private var urlDict = ""

    private val dictSelected = null
    private val GOOGLE_SEARCH_PATH : String = "https://www.google.com/search?q="

    val TAG: String = "DICT == "


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")                  // DEBUG

        // create binding
        bindingDict = ActivityDictBinding.inflate(layoutInflater)
        setContentView(bindingDict.root)

        // menu toolbar
        menu =  findViewById(R.id.acc_toolbar)
        setSupportActionBar(menu)
        menu.setTitle(R.string.app_name)

        // RecyclerView
        bindingDict.recyclerView.adapter = adapter
        bindingDict.recyclerView.layoutManager = LinearLayoutManager(this)
        model.allDictionnaries.observe(this){
            adapter.listDictionaries = it
            adapter.notifyDataSetChanged()
        }

        // shared preferences
        sharedPref = getSharedPreferences("fr.uparis.applang", MODE_PRIVATE)                  // common preferences for all activities
        sharedPrefEditor = sharedPref.edit()
        Log.d(TAG + "SharedPref ", sharedPref.toString())                                       // DEBUG test if they are really commons

        // DEBUG test which activity has to be launched
        val jj = sharedPref.getString(keyActivity, "")
        Log.d(TAG + " activity1 == ", jj!!)

        // SCENARIO: activity loaded by StartActivity after share -> handle url of dictionary arrived
        if (sharedPref.getString(keyShare, "").toString() != "") {
            urlDict = sharedPref.getString(keyShare, "").toString()
            Log.d(TAG + "shared link == ", urlDict)                                             // DEBUG
            handleReceivedLink(urlDict)
        }


    }

    // ================================= Buttons' functions =============================================

    fun enleverDict(view: View) {
        for (el in adapter.checkedItems) {
            model.deleteDictionnary(el.name)
        }
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
//        sharedPrefEditor.putString(key, optionDict).commit()
//        val jj = sharedPref.getString(key, "")
//        Log.d("DICT: activity2 === ", jj!!)

        val nameDict = bindingDict.nomDictET.text.toString().lowercase()
        if (nameDict == "") {
            AlertDialog.Builder(this)
                .setMessage("Merci d'insérer le nom du dictionnaire")
                .setPositiveButton("Ok", DialogInterface.OnClickListener {
                        dialog, id -> finish()
                }).setCancelable(false)
                .show()
            return
        }
        sharedPrefEditor.putString(keyActivity, optionDict)
        sharedPrefEditor.putString(keyName, nameDict)
        sharedPrefEditor.commit()

        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(GOOGLE_SEARCH_PATH + nameDict))
        startActivity(browserIntent)
    }

    // =================================== Share processing ======================================================

    // handling the received data from the "share" process
    private fun handleReceivedLink(shareLink: String) {
        Log.d(TAG + "shared link1 = ", shareLink)                                 // DEBUG
        nameDict = sharedPref.getString(keyName, "").toString()
        bindingDict.nomDictET.setText(nameDict)
        bindingDict.lienDictET.setText(shareLink)

        // add dictionary to BD
    }

// DEBUG
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


}