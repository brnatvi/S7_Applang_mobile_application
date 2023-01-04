package fr.uparis.applang

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import fr.uparis.applang.adapters.DictAdapter
import fr.uparis.applang.adapters.SpinnerAdapter
import fr.uparis.applang.databinding.DictLayoutBinding
import fr.uparis.applang.model.Dictionary
import fr.uparis.applang.model.Language
import fr.uparis.applang.navigation.OptionsMenuActivity


class DictActivity  : OptionsMenuActivity() {
    private lateinit var bindingDict: DictLayoutBinding
    private lateinit var menu: Toolbar

    private val model by lazy {  ViewModelProvider(this).get(ViewModel::class.java) }
    private val adapterDict by lazy { DictAdapter() }

    private var nameDict = ""
    private var urlDict = ""
    val TAG: String = "DICT == "


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCurrentActivity(this)

        // create binding
        bindingDict = DictLayoutBinding.inflate(layoutInflater)
        setContentView(bindingDict.root)

        // menu toolbar
        menu =  findViewById(R.id.acc_toolbar)
        setSupportActionBar(menu)
        menu.setTitle(R.string.app_name)

        // RecyclerView for Dictionaries
        bindingDict.recyclerView.adapter = adapterDict
        bindingDict.recyclerView.layoutManager = LinearLayoutManager(this)

        model.dictionaries.observe(this){
            adapterDict.allItems = it
            adapterDict.notifyDataSetChanged()
        }

        // SharedPreferences
        sharedPref = getSharedPreferences("fr.uparis.applang", MODE_PRIVATE)                  // common preferences for all activities
        sharedPrefEditor = sharedPref.edit()

        // fil current settings on activity's fields
        //if configuration changes or activity goes into background
        if (savedInstanceState != null){
            val name = savedInstanceState.getString(keyDict)
            bindingDict.nomDictET.setText(name)
            val link = savedInstanceState.getString(keyShare)
            bindingDict.lienDictET.setText(link)

            val langSrc   = savedInstanceState.getInt(keySrc)
            val langDest  = savedInstanceState.getInt(keyDest)
            postValuesToSpinners (langSrc, langDest)
        }

        // SCENARIO: activity loaded by StartActivity after share -> handle url of dictionary arrived
        if (sharedPref.getString(keyShare, "").toString() != "") {
            urlDict = sharedPref.getString(keyShare, "").toString()
            handleReceivedLink(urlDict)
        }
        updateLanguagesList()
    }

    override fun onDestroy() {
        cleanPreferences()
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (!bindingDict.nomDictET.text.isEmpty()) {
            outState.putString(keyDict, bindingDict.nomDictET.toString())
        }
        if (!bindingDict.lienDictET.text.isEmpty()) {
            outState.putString(keyShare, bindingDict.lienDictET.toString())
        }
        outState.putInt   (keySrc   , bindingDict.langSrcSP .getSelectedItemPosition())
        outState.putInt   (keyDest  , bindingDict.langDestSP.getSelectedItemPosition())
    }

    // ================================= Buttons' functions =============================================

    // delete selected dictionary from DataBase
    fun enleverDict(view: View) {
        val len = adapterDict.selectedItems.size
        if (len == 0) {
            AlertDialog.Builder(this)
                .setMessage("Veuillez choisir dictionnaire.s à enlever")
                .setPositiveButton("Ok") { dialog, _ ->
                    dialog.dismiss()
                }.setCancelable(false)
                .show()
        } else {
            for (index in 0 until len) {
                model.deleteDictionary(adapterDict.selectedItems[index].name)
                adapterDict.notifyItemRemoved(index)
                adapterDict.selectedItems.clear()
            }
        }
    }

    // create add dictionary with elements from fields and add it to database
    fun ajouterDict(view: View) {
        var nameDict = bindingDict.nomDictET.text.toString().trim().replaceFirstChar(Char::titlecase)
        val url = bindingDict.lienDictET.text.toString().trim()

        if ( (nameDict == "") || (url == "") ) {
            AlertDialog.Builder(this)
                .setMessage("Merci de trouver le lien du dictionnaire et de choisir les languages")
                .setPositiveButton("Ok") { dialog, _ ->
                    dialog.dismiss()
                }.setCancelable(false)
                .show()
        }

        val word = sharedPref.getString(keyWord, "").toString()

        val splitLocation = ordinalIndexOf(url, "/", 3)+1

        val langFrom = (bindingDict.langSrcSP.selectedItem as Language)
        val langDest = (bindingDict.langDestSP.selectedItem as Language)

        val requestComposition = url.substring(splitLocation, url.length)
            .replace(word, "\$word", true)
        val urlDict = url.substring(0, splitLocation)

        if("langFrom" !in requestComposition){
            nameDict += " $langFrom"
        }
        if("langTo" !in requestComposition){
            nameDict += "->$langDest"
        }

        val ret = model.insertDictionary(Dictionary(nameDict, urlDict, requestComposition))

        if (ret < 0) makeToast(this, "Erreur d'insertion du dictionnaire '${nameDict}'")
        else makeToast(this, "Dictionnaire '${nameDict}' vient d'être ajouté")

        with(bindingDict) {
            nomDictET.text.clear()
            lienDictET.text.clear()
        }

        cleanPreferences()
    }

    fun chercherDict(view: View) {
        val phrase = bindingDict.nomDictET.text.toString().lowercase()
        if (phrase == "") {
            AlertDialog.Builder(this)
                .setMessage("Merci d'insérer le nom du dictionnaire et de choisir les languages de traduction")
                .setPositiveButton("Ok") { dialog, _ ->
                    dialog.dismiss()
                }.setCancelable(false)
                .show()
            return
        }

        nameDict = phrase.substringBefore(' ')
        val word  = phrase.substringAfter(' ').trim()
        val srLangSrc = (bindingDict.langSrcSP.selectedItem as Language).fullName
        val stLangDest = (bindingDict.langDestSP.selectedItem as Language).fullName
        val idLangSrc = bindingDict.langSrcSP.getSelectedItemPosition()
        val idLangDest = bindingDict.langDestSP.getSelectedItemPosition()

        val request = GOOGLE_SEARCH_PATH + " $nameDict translate $word  $srLangSrc $stLangDest"

        // save SharedPreferences
        sharedPrefEditor.putString(keyActivity, optionDict)
                        .putString(keyDict, nameDict)
                        .putInt(keySrc, idLangSrc)
                        .putInt(keyDest, idLangDest)
                        .putString(keyWord, word)
                        .commit()

        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(request))
        startActivity(browserIntent)
    }

    // pass to correction of selected dictionary
    fun corrigerDict(view: View){

        if (adapterDict.selectedItems.size != 1) {
            AlertDialog.Builder(this)
                .setMessage("Un seul dictionnaire peut être corrigé à la fois")
                .setPositiveButton("Ok") { dialog, _ ->
                    dialog.dismiss()
                }.setCancelable(false)
                .show()
        } else {
            val dictToEdit = adapterDict.selectedItems[0]
            adapterDict.selectedItems.clear()
            val intentEdit = Intent(this, DictEditActivity::class.java)
            val bundleEdit = Bundle()
            bundleEdit.putString(keyDict, dictToEdit.name)
            bundleEdit.putString("url", dictToEdit.url)
            bundleEdit.putString("requestComposition", dictToEdit.requestComposition)
            intentEdit.putExtras(bundleEdit)
            startActivity(intentEdit)
        }
    }

    // =================================== Share processing ======================================================

    // handling the received data from the "share" process
    private fun handleReceivedLink(shareLink: String) {
        // restore states of fields
        nameDict = sharedPref.getString(keyDict, "").toString().replaceFirstChar(Char::titlecase)
        bindingDict.nomDictET.setText(nameDict)

        bindingDict.lienDictET.setText(shareLink)

        val idLangSrc = sharedPref.getInt(keySrc, 0)
        val idLangDest = sharedPref.getInt(keyDest, 0)
        bindingDict.langSrcSP.post( { bindingDict.langSrcSP.setSelection(idLangSrc) })
        bindingDict.langDestSP.post( { bindingDict.langDestSP.setSelection(idLangDest) })

        AlertDialog.Builder(this)
            .setMessage("Assurez-vous que le contenu du lien correspond au nom du dictionnaire, à la langue source et à la langue destination choisis")
            .setPositiveButton("Ok") { dialog, _ ->
                dialog.dismiss()
            }.setCancelable(false)
            .show()
        return

//        cleanPreferences()
       }

    // ================================= DataBase's functions =============================================

    private fun updateLanguagesList(){
        model.loadAllLanguage()
        model.languages.removeObservers(this)
        model.languages.observe(this){
            SpinnerAdapter(bindingDict.langSrcSP, this, it)
            SpinnerAdapter(bindingDict.langDestSP, this, it)
        }
    }

    private fun ordinalIndexOf(str: String, subs: String?, nIn: Int): Int {
        var n = nIn
        var pos = str.indexOf(subs!!)
        while (--n > 0 && pos != -1) {
            pos = str.indexOf(subs, pos + 1)
        }
        return pos
    }

    // ====================== Auxiliary functions ======================================================

    // post values into spinners
    private fun postValuesToSpinners (src: Int, dest: Int) {
        bindingDict.langSrcSP .post( { bindingDict.langSrcSP .setSelection(src) })
        bindingDict.langDestSP.post( { bindingDict.langDestSP.setSelection(dest) })
    }

}