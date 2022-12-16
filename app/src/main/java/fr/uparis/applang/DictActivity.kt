package fr.uparis.applang

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import fr.uparis.applang.databinding.ActivityDictBinding
import fr.uparis.applang.model.Dictionary
import fr.uparis.applang.model.Language


class DictActivity  : OptionsMenuActivity() {
    private lateinit var bindingDict: ActivityDictBinding
    private lateinit var menu: Toolbar

    private val model by lazy {  ViewModelProvider(this).get(ViewModel::class.java) }
    private val adapterDict by lazy { DictAdapter() }

    private var nameDict = ""
    private var urlDict = ""
    val TAG: String = "DICT == "


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        // SharedPreferences
        sharedPref = getSharedPreferences("fr.uparis.applang", MODE_PRIVATE)                  // common preferences for all activities
        sharedPrefEditor = sharedPref.edit()

        // SCENARIO: activity loaded by StartActivity after share -> handle url of dictionary arrived
        if (sharedPref.getString(keyShare, "").toString() != "") {
            urlDict = sharedPref.getString(keyShare, "").toString()
            handleReceivedLink(urlDict)
        }

        updateLanguagesList()
    }

    // ================================= Buttons' functions =============================================

    fun enleverDict(view: View) {
        for (el in adapterDict.selectedItems) {
            model.deleteDictionary(el.name)
        }
    }

    fun ajouterDict(view: View) {
        nameDict = bindingDict.nomDictET.text.toString().trim()
        urlDict = bindingDict.lienDictET.text.toString().trim()

        // TODO make requestComposition
        val word = sharedPref.getString(keyWord, "").toString()


        if ( (nameDict == "") || (urlDict == "") ) {
            AlertDialog.Builder(this)
                .setMessage("Merci de trouver le lien de dictionnaire, ainsi choisir les languages")
                .setPositiveButton("Ok", DialogInterface.OnClickListener {
                        dialog, id -> dialog.dismiss()
                }).setCancelable(false)
                .show()
            return
        }

        // TODO make requestComposition
        val ret = model.insertDictionary(Dictionary(nameDict, urlDict, ""))

        if (ret < 0) makeToast(this, "Erreur d'insertion du dictionnaire '${nameDict}'")
        else makeToast(this, "Dictionnaire '${nameDict}' vient d'être ajouté")

        with(bindingDict) {
            nomDictET.text.clear()
            lienDictET.text.clear()
           // requestCompDictET.text.clear()
        }

        cleanPreferences()
    }

    fun chercherDict(view: View) {
        val phrase = bindingDict.nomDictET.text.toString().lowercase()
        if (phrase == "") {
            AlertDialog.Builder(this)
                .setMessage("Merci d'insérer le nom du dictionnaire et de choisir les languages de traduction")
                .setPositiveButton("Ok", DialogInterface.OnClickListener {
                        dialog, id -> dialog.dismiss()
                }).setCancelable(false)
                .show()
            return
        }

        nameDict = phrase.substringBefore(' ')
        val word  = phrase.substringAfter(' ').trim()
        val srLangSrc = (bindingDict.langSrcSP.selectedItem as Language).fullName
        val stLangDest = (bindingDict.langDestSP.selectedItem as Language).fullName
        val idLangSrc = bindingDict.langSrcSP.getSelectedItemPosition()
        val idLangDest = bindingDict.langDestSP.getSelectedItemPosition()

        val request = GOOGLE_SEARCH_PATH + " ${nameDict} translate ${word}  ${srLangSrc} ${stLangDest}"

        // save SharedPreferences
        sharedPrefEditor.putString(keyActivity, optionDict)
                        .putString(keyName, nameDict)
                        .putInt(keySrc, idLangSrc)
                        .putInt(keyDest, idLangDest)
                        .putString(keyWord, word)
                        .commit()

        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(request))
        startActivity(browserIntent)
    }

    // =================================== Share processing ======================================================

    // handling the received data from the "share" process
    private fun handleReceivedLink(shareLink: String) {
        // restore states of fields
        nameDict = sharedPref.getString(keyName, "").toString().capitalize()
        bindingDict.nomDictET.setText(nameDict)

        bindingDict.lienDictET.setText(shareLink)

        val idLangSrc = sharedPref.getInt(keySrc, 0)
        val idLangDest = sharedPref.getInt(keyDest, 0)
        bindingDict.langSrcSP.post( { bindingDict.langSrcSP.setSelection(idLangSrc) })
        bindingDict.langDestSP.post( { bindingDict.langDestSP.setSelection(idLangDest) })

        makeToast(this,"Veuiller verifier les languages sourse et destination pour le dictionnaire $nameDict")
       }


    // ================================= DataBase's functions =============================================

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

}