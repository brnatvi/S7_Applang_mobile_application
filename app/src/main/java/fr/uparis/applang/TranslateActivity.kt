package fr.uparis.applang

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import fr.uparis.applang.databinding.ActivityTranslateBinding
import fr.uparis.applang.model.Dictionary
import fr.uparis.applang.model.Language
import fr.uparis.applang.model.Word
import java.text.Normalizer


class TranslateActivity : OptionsMenuActivity() {
    private lateinit var binding: ActivityTranslateBinding
    private lateinit var menu: Toolbar
    private val model by lazy { ViewModelProvider(this)[TranslateViewModel::class.java] }
    private lateinit var sharedPref : SharedPreferences
    private lateinit var sharedPrefEditor: SharedPreferences.Editor
    private val key: String = "activity"
    private val optionTransl: String = "translActivity"

    private val GOOGLE_SEARCH_PATH : String = "https://www.google.com/search?q="
    private val REGEX_UNACCENT = "\\p{InCombiningDiacriticalMarks}+".toRegex()

    private var wholeURL = ""
    private var langSRC = ""
    private var langDST = ""
    private var word = ""
    private var dictURL = ""

    //private var dictList = mutableListOf<Dictionary>();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // create binding
        binding = ActivityTranslateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // menu toolbar
        menu =  findViewById(R.id.acc_toolbar)
        setSupportActionBar(menu)
        menu.setTitle(R.string.app_name)

        // shared preferences
        sharedPref = getSharedPreferences("fr.uparis.applang", MODE_PRIVATE)
        sharedPrefEditor = sharedPref.edit()
        val jj = sharedPref.getString(key, "")
        Log.d("TRANSL: activity1 === ", jj!!)

        // TODO Problem: it works not only when share, but on first launch too (word = last session word, langs = last session langs)
        loadPreferencies()

        // handling the received data from the "share" process
        when {
            intent?.action == Intent.ACTION_SEND -> {
                if ("text/plain" == intent.type) {
                    savePreferencies()
                    handleReceivedText(intent)
                }
            }
            else -> {
                // Handle other intents, such as being started from the home screen
            }
        }

        //TODO insert only if needed
        var firstStart = true
        if(firstStart){
           // model.deleteAllLanguage()
            insertAllLanguages()
           // model.deleteAllDictionary()
            model.insertDictionary(Dictionary("Word Reference", "https://www.wordreference.com/", "\$langFrom\$langTo/\$word"))
            model.insertDictionary(Dictionary("Larousse", "https://www.larousse.fr/dictionnaires/", "\$langFromLong-\$langToLong/\$word/"))
            model.insertDictionary(Dictionary("Google translate", "https://translate.google.fr/", "?sl=\$langFrom&tl=\$langTo&text=\$word"))
        }
        updateLanguagesList()
        updateDictionaryList()
    }


    override fun onPause() {
        sharedPrefEditor.putString(key, optionTransl).commit()
        val jj = sharedPref.getString(key, "")
        Log.d("TRANSL: activ === ", jj!!)
        super.onPause()
    }

    // ================================== Save / load activity state ==========================================

    private fun savePreferencies() {
        sharedPrefEditor.putString("word", word)
        sharedPrefEditor.putString("langSRC", langSRC)
        sharedPrefEditor.putString("langDST", langDST)
        sharedPrefEditor.commit()
    }

    private fun loadPreferencies() {
        word = sharedPref.getString("word", "")!!
        langSRC = sharedPref.getString("langSRC", "")!!
        langDST = sharedPref.getString("langDST", "")!!
    }

    // =================================== Share processing ======================================================

    // handling the received data from the "share" process
    private fun handleReceivedText(intent: Intent) {
        intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
           // val dict = Regex("^(([^:/?#]+):)?(//([^/?#]*))").find(it)!!.groupValues.get(0)
            wholeURL = Regex("^(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\\?([^#]*))?(#(.*))?").find(it)!!.groupValues.get(0)
            Log.d("wholeURL ===", wholeURL)

            loadPreferencies()
            Log.d("word after share ===", word)

            //tryToGuessLanguagesFromURL will be call later
            addCurrentURLAsDictionary(it)

            //TODO use somewhere useful (currently used for print only)
            updateWordsList()
        }
    }
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

    // ================================= Buttons' functions =============================================

    // transmit the word to some online dictionary
    fun traduire(view: View){
        saveWordInDB()
}

    // transmit the word to Google search motor
    fun chercher(view: View){
        val phrase = binding.motET.text.toString().lowercase()
        word = binding.motET.text.toString().lowercase().substringBefore(' ')

        Log.d("word init ===", word)
        Log.d("phrase init ===", phrase)
        langSRC = binding.langSrcSP.selectedItem.toString()

        Log.d("langSRC init ===", langSRC)
        langDST = binding.langDestSP.selectedItem.toString()
        Log.d("langDST init ===", langDST)

        sharedPrefEditor.putString(key, optionTransl).commit()
        val jj = sharedPref.getString(key, "")
        Log.d("TRANSL2: activity === ", jj!!)

        val browserInt = Intent(Intent.ACTION_VIEW, Uri.parse(GOOGLE_SEARCH_PATH + phrase))
        startActivity(browserInt)
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
        val w: Word = Word(wordText, langFrom.id, langTo.id, url)
        Log.d("DB","add word $w")
        model.insertWord(w)
        binding.motET.text.clear()
        updateDictionaryList()

        //TODO use somewhere useful (currently used for print only)
        updateWordsList()
    }

    private fun updateWordsList(){
        model.loadAllWord()
        model.words.removeObservers(this)
        model.words.observe(this){
            Log.d("DB","list: $it")
//          TODO Also update a graphic list if needed
        }
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
            binding.langDestSP.adapter = arrayAdapter
            val arrayAdapter2 = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item, it
            )
            binding.langSrcSP.adapter = arrayAdapter2
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
                binding.langSrcSP.setSelection(indexFrom)
                binding.langDestSP.setSelection(indexTo)
            }
        }
    }

    private fun updateDictionaryList(){
        model.loadAllDictionary()
        model.dictionaries.removeObservers(this)
        model.dictionaries.observe(this){
            var list = mutableListOf<Dictionary>()
            if(model.currentTranslationUrl.isNotEmpty()){
                tryToGuessLanguagesFromURL(model.currentTranslationUrl, it)
                list.add(Dictionary("Lien depuis le partage", model.currentTranslationUrl, ""))
            }
            //dictList = list
            list.addAll(it)
            Log.d("DB","list dictionaries: $list")
            val arrayAdapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item, list
            )
            binding.dictSP.adapter = arrayAdapter
        }
    }

    private fun addCurrentURLAsDictionary(url: String){
        model.currentTranslationUrl=url;
    }

    private fun insertAllLanguages(){
        val languageListContent: String = getString(R.string.languageList)
        var languageList: MutableList<Language> = mutableListOf()
        for (line: String in languageListContent.split("\n")){
            var t = line.split(",")
            languageList.add(Language(t[0], t[1]))
        }
        model.insertLanguages(*languageList.toTypedArray())
    }


    // ====================== Auxiliary functions ======================================================
    fun CharSequence.unaccent(): String {
        val temp = Normalizer.normalize(this, Normalizer.Form.NFD)
        return REGEX_UNACCENT.replace(temp, "")
    }

}