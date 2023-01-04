package fr.uparis.applang

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.widget.Toolbar
import fr.uparis.applang.databinding.TranslateLayoutBinding
import fr.uparis.applang.model.Dictionary
import fr.uparis.applang.model.Language
import fr.uparis.applang.model.Word
import fr.uparis.applang.navigation.OptionsMenuActivity

/**
 * Main Activity were user can add new translation.
 */
class TranslateActivity : OptionsMenuActivity() {
    private lateinit var binding: TranslateLayoutBinding
    private lateinit var menu: Toolbar

    private var wholeURL = ""
    private var langSRC = ""
    private var langDST = ""
    private var word = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCurrentActivity(this)

        // create binding
        binding = TranslateLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // menu toolbar
        menu =  findViewById(R.id.acc_toolbar)
        setSupportActionBar(menu)
        menu.setTitle(R.string.app_name)

        // shared preferences
        sharedPref = getSharedPreferences("fr.uparis.applang", MODE_PRIVATE)                  // common preferences for all activities
        sharedPrefEditor = sharedPref.edit()

        // fil current settings on activity's fields
        //if configuration changes or activity goes into background
        if (savedInstanceState != null){
            val mot = savedInstanceState.getString(keyWord)
            binding.motET.setText(mot)

            val langSrc   = savedInstanceState.getInt(keySrc)
            val langDest  = savedInstanceState.getInt(keyDest)
            val dict      = savedInstanceState.getInt(keyDict)
            postValuesToSpinners (langSrc, langDest, dict)
        }

        // SCENARIO: activity loaded after share -> handle url of translation arrived
        if (sharedPref.getString(keyShare, "").toString() != "") {
            wholeURL = sharedPref.getString(keyShare, "").toString()
            handleReceivedLink(wholeURL)
        }

        updateLanguagesList()
        updateDictionaryList()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (binding.motET.text.isNotEmpty()) {
            outState.putString(keyWord, binding.motET.toString())
        }
        outState.putInt   (keySrc , binding.langSrcSP .selectedItemPosition)
        outState.putInt   (keyDest, binding.langDestSP.selectedItemPosition)
        outState.putInt   (keyDict, binding.dictSP    .selectedItemPosition)
    }

    // ================================= Buttons' functions =============================================

    // save Word and URL in BD
    fun traduire(view: View){
        saveWordInDB()
        cleanPreferences()
        postValuesToSpinners (0, 0, 0)
}

    // transmit the word to Google search motor
    fun chercher(view: View){
        word = binding.motET.text.toString().lowercase()
        if (word == "") {
            AlertDialog.Builder(this)
                .setMessage("Merci d'insérer un mot ou une phase pour recherche.\nPar exemple 'cherher la femme' ")
                .setPositiveButton("Ok") { dialog, _ ->
                    dialog.dismiss()
                }.setCancelable(false)
                .show()
            return
        }
       // word = phrase.substringBefore(' ')
        langSRC = binding.langSrcSP.selectedItem.toString()
        langDST = binding.langDestSP.selectedItem.toString()
        val idLangSrc = binding.langSrcSP.selectedItemPosition
        val idLangDest = binding.langDestSP.selectedItemPosition

        // save SharedPreferences
        sharedPrefEditor.putString(keyActivity, optionTransl)
                        .putString(keyWord, word)
                        .putInt(keySrc, idLangSrc)
                        .putInt(keyDest, idLangDest)
                        .commit()

        // launch Google search
        val browserInt = Intent(Intent.ACTION_VIEW, Uri.parse("$GOOGLE_SEARCH_PATH$word traduction de $langSRC vers $langDST"))
        startActivity(browserInt)
    }

    override fun onDestroy() {
        cleanPreferences()
        super.onDestroy()
    }

    // =================================== Share processing ======================================================

    // handling the received data from the "share" process
    private fun handleReceivedLink(shareLink: String) {
        wholeURL = shareLink

        // restore states of fields
        word = sharedPref.getString(keyWord, "").toString()
        binding.motET.setText(word)
        val idLangSrc  = sharedPref.getInt(keySrc, 0)
        val idLangDest = sharedPref.getInt(keyDest, 0)
        val idDict     = sharedPref.getInt(keyDict, 0)
        postValuesToSpinners (idLangSrc, idLangDest, idDict)

        //tryToGuessLanguagesFromURL will be call later
        addCurrentURLAsDictionary(wholeURL)

        cleanPreferences()
    }

    //Not used in last app version because there is to much weard web site url
    private fun tryToGuessLanguagesFromURL(wholeURL: String, dictList: List<Dictionary>){
        Log.d("GuessFromURL", "try to guess for $wholeURL in $dictList")
        for (dict in dictList){
            if(wholeURL.startsWith(dict.url)){
                Log.d("GuessFromURL", "found matching dictionary: $dict for $wholeURL")
                var rc: String = dict.requestComposition.lowercase()
                var endURL = wholeURL.replace(dict.url, "")//.lowercase()
                model.currentLangFrom = ""
                model.currentLangTo = ""
                var word = ""
                while (rc.isNotEmpty() && endURL.isNotEmpty()){
                    if(rc.startsWith("\$langfrom")){
                        model.currentLangFrom = endURL.substring(0, 2)
                        rc = rc.substring(9)
                        endURL = endURL.substring(2)
                    }else if(rc.startsWith("\$langto")) {
                        model.currentLangTo = endURL.substring(0, 2)
                        rc = rc.substring(7)
                        endURL = endURL.substring(2)
                    }else if(rc.startsWith("\$word")){
                        word = endURL.replace("/", "")
                        break
                    }else if(rc[0]==endURL[0]){
                        rc = rc.substring(1)
                        endURL = endURL.substring(1)
                    }else{
                        Log.e("GuessFromURL", "Unable to match url composition for $rc & $endURL")
                        break
                    }
                }
                Log.d("GuessFromURL","Guess : langFrom=${model.currentLangFrom}, langTo=${model.currentLangTo}, word=$word")
                binding.motET.setText(word)
//                "\$langFrom\$langTo/\$word" : "enfr/hello"
//                "?sl=\$langFrom&tl=\$langTo&text=\$word"
            }
        }
    }

    // ================================= DataBase's functions =============================================

    private fun saveWordInDB(){
        val wordText = binding.motET.text.toString().lowercase()
        val dict = binding.dictSP.selectedItem as Dictionary
        val langFrom = (binding.langSrcSP.selectedItem as Language)
        val langTo = (binding.langDestSP.selectedItem as Language)

        // compose URL
        val url = dict.url + dict.requestComposition
            .replace("\$langFromLong", langFrom.fullName.unaccent().lowercase(), true)
            .replace("\$langToLong", langTo.fullName.unaccent().lowercase(), true)
            .replace("\$langFrom", langFrom.id, true)
            .replace("\$langTo", langTo.id, true)
            .replace("\$word", wordText.replace(" ", "%20"), true)
        val w = Word(wordText, langFrom.id, langTo.id, url)
        Log.d("DB","add word $w")
        val ret = model.insertWord(w)

        if (ret < 0) makeToast(this, "Erreur d'insertion du mot '${wordText}'")
        else makeToast(this, "Le mot/la phrase '${wordText}' vient d'être ajouté")

        binding.motET.text.clear()
        updateDictionaryList()
        updateWordsList()
    }


    private fun updateWordsList(){
        model.loadAllWords()
        model.words.removeObservers(this)
        model.words.observe(this){
            Log.d("DB","list: $it")
        }
    }

    private fun updateLanguagesList(){
        model.loadAllLanguage()
        model.languages.removeObservers(this)
        model.languages.observe(this){
            if(it.isEmpty()){ //if there is any language, we need to initialise all app data.
//                iniAppData();
                updateLanguagesList()
            }else {
                val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, it)
                binding.langDestSP.adapter = arrayAdapter

                val arrayAdapter2 = ArrayAdapter(this, android.R.layout.simple_spinner_item, it)
                binding.langSrcSP.adapter = arrayAdapter2

                if (model.currentTranslationUrl.isNotEmpty()) {
                    var indexFrom = 0
                    var indexTo = 0
                    for ((k, lang) in it.withIndex()) {
                        if (lang.id == model.currentLangFrom) {
                            indexFrom = k
                        }
                        if (lang.id == model.currentLangTo) {
                            indexTo = k
                        }
                    }
                    binding.langSrcSP.setSelection(indexFrom)
                    binding.langDestSP.setSelection(indexTo)
                }
            }
        }
    }

    private fun updateDictionaryList(){
        model.loadAllDictionary()
        model.dictionaries.removeObservers(this)
        model.dictionaries.observe(this){
            val list = mutableListOf<Dictionary>()
            if(model.currentTranslationUrl.isNotEmpty()){
//                tryToGuessLanguagesFromURL(model.currentTranslationUrl, it)
                list.add(Dictionary("Lien depuis le partage", model.currentTranslationUrl, ""))
            }
            list.addAll(it)
            Log.d("DB","list dictionaries:")
            for(dict: Dictionary in list ){
                Log.d("DB",dict.toStringFull())
        }
            val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, list)
            binding.dictSP.adapter = arrayAdapter
        }
    }

    private fun addCurrentURLAsDictionary(url: String){
        model.currentTranslationUrl=url
    }

    // ====================== Auxiliary functions ======================================================
    // post values into spinners
    private fun postValuesToSpinners (src: Int, dest: Int, dict: Int) {
        binding.langSrcSP .post { binding.langSrcSP.setSelection(src) }
        binding.langDestSP.post { binding.langDestSP.setSelection(dest) }
        binding.dictSP    .post { binding.dictSP.setSelection(dict) }
    }
}