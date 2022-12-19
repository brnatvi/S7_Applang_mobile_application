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
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.codec.binary.StringUtils
import fr.uparis.applang.adapters.DictAdapter
import fr.uparis.applang.databinding.DictLayoutBinding
import fr.uparis.applang.model.Dictionary
import fr.uparis.applang.model.Language
import java.text.Normalizer


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
            adapterDict.listDictionaries = it
            adapterDict.notifyDataSetChanged()
        }

        // SharedPreferences
        sharedPref = getSharedPreferences("fr.uparis.applang", MODE_PRIVATE)                  // common preferences for all activities
        sharedPrefEditor = sharedPref.edit()

        val jj = sharedPref.getString(keyActivity, "")
        Log.d(TAG + 1, jj!!)

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

    // ================================= Buttons' functions =============================================

    // delete selected dictionary from DataBase
    fun enleverDict(view: View) {
        val len = adapterDict.selectedItems.size
         for (index in 0..len-1) {
             model.deleteDictionary(adapterDict.selectedItems[index].name)
             adapterDict.notifyItemRemoved(index)
        }
    }

    // create add dictionary with elements from fields and add it to database
    fun ajouterDict(view: View) {
        var nameDict = bindingDict.nomDictET.text.toString().trim().capitalize()
        val url = bindingDict.lienDictET.text.toString().trim()

        if ( (nameDict == "") || (url == "") ) {
            AlertDialog.Builder(this)
                .setMessage("Merci de trouver le lien du dictionnaire et de choisir les languages")
                .setPositiveButton("Ok", DialogInterface.OnClickListener {
                        dialog, id -> dialog.dismiss()
                }).setCancelable(false)
                .show()
            return
        }

        val word = sharedPref.getString(keyWord, "").toString()
       // val word = "word"

        val splitLocation = ordinalIndexOf(url, "/", 3)+1

        var langFrom = (bindingDict.langSrcSP.selectedItem as Language)
        var langDest = (bindingDict.langDestSP.selectedItem as Language)

        val requestComposition = url.substring(splitLocation, url.length)
            .replace(word, "\$word", true)
// Next replace have issues with weard translate url as language code like 'fra' for french or even 'us' for russian.
//            .replace(langFrom.fullName, "\$langFromLong", true)
//            .replace(langDest.fullName, "\$langToLong", true)
//            .replace(langFrom.fullName.unaccent(), "\$langFromLong", true)
//            .replace(langDest.fullName.unaccent(), "\$langToLong", true)
//            .replace(langFrom.id, "\$langFrom", false)
//            .replace(langDest.id, "\$langTo", false)
        val urlDict = url.substring(0, splitLocation)

        if("langFrom" !in requestComposition){
            nameDict += " $langFrom";
        }
        if("langTo" !in requestComposition){
            nameDict += "->$langDest";
        }

        val ret = model.insertDictionary(Dictionary(nameDict, urlDict, requestComposition))

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

    // pass to correction of selected dictionary
    fun corrigerDict(view: View){
        val dictToEdit = adapterDict.getSelected()
        if (dictToEdit == null) {
            AlertDialog.Builder(this)
                .setMessage("Un seul dictionnaire peut être corrigé à la fois")
                .setPositiveButton("Ok", DialogInterface.OnClickListener {
                        dialog, id -> dialog.dismiss()
                }).setCancelable(false)
                .show()
            return
        }
        val  intentEdit = Intent(this, DictEditActivity::class.java)
        val bundleEdit = Bundle()
        bundleEdit.putString(keyName, dictToEdit.name)
        bundleEdit.putString("url", dictToEdit.url)
        bundleEdit.putString("requestComposition", dictToEdit.requestComposition)
        intentEdit.putExtras(bundleEdit)
        startActivity(intentEdit)
    }

    // =================================== Share processing ======================================================

    // handling the received data from the "share" process
    private fun handleReceivedLink(shareLink: String) {

        val jj = sharedPref.getString(keyActivity, "")
        Log.d(TAG + 2, jj!!)

        // restore states of fields
        nameDict = sharedPref.getString(keyName, "").toString().capitalize()
        bindingDict.nomDictET.setText(nameDict)

        bindingDict.lienDictET.setText(shareLink)

        val idLangSrc = sharedPref.getInt(keySrc, 0)
        val idLangDest = sharedPref.getInt(keyDest, 0)
        bindingDict.langSrcSP.post( { bindingDict.langSrcSP.setSelection(idLangSrc) })
        bindingDict.langDestSP.post( { bindingDict.langDestSP.setSelection(idLangDest) })

        AlertDialog.Builder(this)
            .setMessage("Assurez-vous que le contenu du lien correspond au nom du dictionnaire, à la langue source et à la langue destination choisis")
            .setPositiveButton("Ok", DialogInterface.OnClickListener {
                    dialog, id -> dialog.dismiss()
            }).setCancelable(false)
            .show()
        return

        cleanPreferences()
      //  makeToast(this,"Veuiller verifier les languages sourse et destination pour le dictionnaire $nameDict")
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

    private fun ordinalIndexOf(str: String, subs: String?, n: Int): Int {
        var n = n
        var pos = str.indexOf(subs!!)
        while (--n > 0 && pos != -1) {
            pos = str.indexOf(subs, pos + 1)
        }
        return pos
    }

    //TODO avoid duplicate if possible
    private val REGEX_UNACCENT = "\\p{InCombiningDiacriticalMarks}+".toRegex()
    fun CharSequence.unaccent(): String {
        val temp = Normalizer.normalize(this, Normalizer.Form.NFD)
        return REGEX_UNACCENT.replace(temp, "")
    }

}