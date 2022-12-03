package fr.uparis.applang

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.widget.Toolbar
//import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import fr.uparis.applang.databinding.ActivityMainBinding
import fr.uparis.applang.model.Dictionary
import fr.uparis.applang.model.Language
import fr.uparis.applang.model.Word
import java.text.Normalizer


class MainActivity : OptionsMenuActivity() {
    private lateinit var binding: ActivityMainBinding
    private val model by lazy { ViewModelProvider(this)[MainViewModel::class.java] }

    private lateinit var menu: Toolbar

    private val GOOGLE_SEARCH_PATH : String = "https://www.google.com/search?q="
    private val REGEX_UNACCENT = "\\p{InCombiningDiacriticalMarks}+".toRegex()

    private var wholeURL = ""
    private var langSRC = ""
    private var langDST = ""
    private var word = ""
    private var dictURL = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // create binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // menu toolbar
        menu =  findViewById(R.id.acc_toolbar)
        setSupportActionBar(menu)
        menu.setTitle(R.string.app_name)

        // handling the received data from the "share" process
        when {
            intent?.action == Intent.ACTION_SEND -> {
                if ("text/plain" == intent.type) {
                  //  word = savedInstanceState?.getString("word")!!
                  //  langSRC = savedInstanceState?.getString("langSRC")!!
                  //  langDST = savedInstanceState?.getString("langDST")!!
                   handleSendText(intent) // Handle text being sent
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
        // https://www.wordreference.com/fren/maison
        // https://www.larousse.fr/dictionnaires/francais-anglais/maison
        // https://translate.google.fr/?sl=fr&tl=en&text=maison%0A&op=translate
        // https://dictionnaire.reverso.net/fransais-russe/maison

        updateLanguagesList()
        updateDictionaryList()
    }

    // ================================== Save activity state ==========================================
    override fun onSaveInstanceState(outState: Bundle) {
        Log.d("word onSaveInstance ===", word)
        outState.putString("word", word)
        outState.putString("langSRC", langSRC)
        outState.putString("langDST", langDST)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        word = savedInstanceState.getString("word")!!
        Log.d("word onRestore ===", word)
        langSRC = savedInstanceState.getString("langSRC")!!
        langDST = savedInstanceState.getString("langDST")!!
    }

    // =================================== Share processing ======================================================

    // handling the received data from the "share" process
    private fun handleSendText(intent: Intent) {
        intent.getStringExtra(Intent.EXTRA_TEXT)?.let {

           // val dict = Regex("^(([^:/?#]+):)?(//([^/?#]*))").find(it)!!.groupValues.get(0)

            wholeURL = Regex("^(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\\?([^#]*))?(#(.*))?").find(it)!!.groupValues.get(0)
            Log.d("wholeURL ===", wholeURL)

            word = intent?.extras?.getString("word") ?: ""
            Log.d("word after share ===", word)

            addCurrentURLAsDictionary(it)

            //TODO use somewhere useful (currently used for print only)
            updateWordsList()
        }
    }


    // =================== Menu ==================================================
  /*  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.goMainActivity -> {
                return true
            }
            R.id.addDict -> {
                val  intentDict = Intent(this, AjoutDictActivity::class.java)
                startActivity(intentDict)
                return true
            }
            R.id.exercices -> {
                return true
            }
            R.id.settings -> {
                return true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }
*/
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

        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(GOOGLE_SEARCH_PATH + phrase))
        startActivity(browserIntent)
    }

    // ================================= DataBase's functions =============================================

    private fun composeURL (): String {
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
            .replace("\$word", wordText, true)

        return url
    }
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
            .replace("\$word", wordText, true)
        val w: Word = Word(wordText, langFrom.id, langTo.id, url)
        Log.d("DB","add word $w")
        model.insertWord(w)
        binding.motET.text.clear()

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
        }
    }

    private fun updateDictionaryList(){
        model.loadAllDictionary()
        model.dictionaries.removeObservers(this)
        model.dictionaries.observe(this){
            var list = mutableListOf<Dictionary>()
            if(model.currentTranslationUrl.isNotEmpty()){
                list.add(Dictionary("Lien depuis le partage", model.currentTranslationUrl, ""))
            }
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