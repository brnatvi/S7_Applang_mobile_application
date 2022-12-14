package fr.uparis.applang

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import fr.uparis.applang.databinding.ActivityDictBinding
import fr.uparis.applang.model.Dictionary
import fr.uparis.applang.model.Language


class DictActivity  : OptionsMenuActivity() {
    private lateinit var bindingDict: ActivityDictBinding
    private lateinit var menu: Toolbar

    private lateinit var sharedPref : SharedPreferences
    private lateinit var sharedPrefEditor: SharedPreferences.Editor
    private val keyActivity: String = "activity"
    private val keyShare: String = "linkShare"
    private val keyName: String = "nameDict"
    private val optionDict: String = "dictActivity"

    private val model by lazy {  ViewModelProvider(this).get(ViewModel::class.java) }
    private val adapterDict by lazy { DictAdapter() }

    private var nameDict = ""
    private var urlDict = ""

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

        // RecyclerView for Dictionaries
        bindingDict.recyclerView.adapter = adapterDict
        bindingDict.recyclerView.layoutManager = LinearLayoutManager(this)
        model.dictionaries.observe(this){
            adapterDict.listDictionaries = it
            adapterDict.notifyDataSetChanged()
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

        updateLanguagesList()
    }

    private fun insertAllLanguages(){
        val languageListContent: String = R.string.languageList.toString()
        var languageList: MutableList<Language> = mutableListOf()
        for (line: String in languageListContent.split("\n")){
            var t = line.split(",")
            languageList.add(Language(t[0], t[1]))
        }
        model.insertLanguages(*languageList.toTypedArray())
    }

    private fun updateLanguagesList(){
        model.loadAllLanguage()
        model.languages.removeObservers(this)
        model.languages.observe(this){
            Log.d("DB","list language: $it")
            val arrayAdapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item, it
            )
            bindingDict.langSrcSP.adapter = arrayAdapter
            val arrayAdapter2 = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item, it
            )
            bindingDict.langDestSP.adapter = arrayAdapter2
            if(model.currentTranslationUrl.isNotEmpty()){
                var indexFrom: Int = 0
                var indexTo: Int = 0
                var k = 0
                for (lang in it){
                    if(lang.id == model.currentLangFrom){
                        indexFrom = k
                    }
                    if(lang.id == model.currentLangTo){
                        indexTo = k
                    }
                    k++;
                }
                bindingDict.langSrcSP.setSelection(indexFrom)
                bindingDict.langDestSP.setSelection(indexTo)
            }
        }
    }

    // ================================= Buttons' functions =============================================

    fun enleverDict(view: View) {
        for (el in adapterDict.checkedItems) {
            model.deleteDictionary(el.name)
        }
    }

    fun ajouterDict(view: View) {
        val name = bindingDict.nomDictET.text.toString().trim()
        val url = bindingDict.lienDictET.text.toString().trim()
       // val requestComp = bindingDict.requestCompDictET.text.toString().trim()

        if ( (name == "") || (url == "") ) {
            AlertDialog.Builder(this)
                .setMessage("Merci d'insérer le nom du dictionnaire, ainsi que trouver sont lien internet et indiquer les languages")
                .setPositiveButton("Ok", DialogInterface.OnClickListener {
                        dialog, id -> dialog.dismiss()
                }).setCancelable(false)
                .show()
            return
        }

        model.insertDictionary(Dictionary(name, url, ""))

        showToast("Dictionnaire " + "$name" + " vient d'être ajouté", 1)

        with(bindingDict) {
            nomDictET.text.clear()
            lienDictET.text.clear()
           // requestCompDictET.text.clear()
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
                        dialog, id -> dialog.dismiss()
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

        showToast("Veuiller composer les languages sourse et destination pour le dictionnaire " + "$nameDict", 3)
    }

    private fun showToast(message: String, koeff: Int) {
        Toast.makeText(this, message, (Toast.LENGTH_SHORT * koeff)).show()
    }
}