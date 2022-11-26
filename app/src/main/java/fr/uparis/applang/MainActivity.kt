package fr.uparis.applang

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.room.PrimaryKey
import fr.uparis.applang.R
import fr.uparis.applang.databinding.ActivityMainBinding
import fr.uparis.applang.model.Language
import fr.uparis.applang.model.Word


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val model by lazy { ViewModelProvider(this)[MainViewModel::class.java] }

    private val GOOGLE_SEARCH_PATH : String = "https://www.google.com/search?q="
    private var dictURL = ""
    private var langSRC = ""
    private var langDST = ""
    private var mot = ""
    private var tradURL = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        when {
            intent?.action == Intent.ACTION_SEND -> {
                if ("text/plain" == intent.type) {
                    handleSendText(intent) // Handle text being sent
                }
            }
            else -> {
                // Handle other intents, such as being started from the home screen
            }
        }

        //TODO insert only if needed
        insertAllLanguages()
        updateLanguagesList()
    }

    // "text/plain" prishlet ssilku  https://dictionnaire.reverso.net/fransais-russe/maison
    private fun handleSendText(intent: Intent) {
        Log.d("DB", "in handleSendText()")
        intent.getStringExtra(Intent.EXTRA_TEXT)?.let {

            dictURL = Regex("^(([^:/?#]+):)?(//([^/?#]*))").find(it)!!.groupValues.get(0)

            tradURL = Regex("^(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\\?([^#]*))?(#(.*))?").find(it)!!.groupValues.get(0)

            Log.d("tradURL ===", tradURL)

            val matchResult = Regex("(\\w+)\\-(\\w+)").find(it)!!.groupValues.drop(1)
            langSRC = matchResult!!.get(0)
            Log.d("langSRC ===", langSRC)

            langDST = matchResult!!.get(1)
            Log.d("langDST ===", langDST)

            mot = Regex("\\w+\$").find(it)!!.groupValues.get(0)
            Log.d("mot ===", mot)

            // add new word to BD
            val w = Word(mot, langSRC.substring(0.. 1), langDST.substring(0..1), tradURL)
            model.insertWord(w)

            //TODO use somewhere useful (currently used for print only)
            updateWordsList()
        }
    }


    // =================== Menu ==================================================
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
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

    // ================================= Buttons' functions =============================================
    fun traduire(view: View){
        saveWordInDB()
    }

    fun chercher(view: View){
        val mot = binding.motET.text.toString()
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(GOOGLE_SEARCH_PATH + mot))
        startActivity(browserIntent)
    }

    // ================================= DataBase's functions =============================================
    private fun saveWordInDB(){
        var w: Word = Word(binding.motET.text.toString().lowercase(), (binding.langSrcSP.selectedItem as Language).id, (binding.langDestSP.selectedItem as Language).id, "...")
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
            var list = mutableListOf<Language>()
            for (lang in it){
                list.add(lang)
            }
            val arrayAdapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item, list
            )
            binding.langDestSP.adapter = arrayAdapter
            val arrayAdapter2 = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item, list
            )
            binding.langSrcSP.adapter = arrayAdapter2
        }

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

}