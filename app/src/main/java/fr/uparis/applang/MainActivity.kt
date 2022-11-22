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
import com.example.applang.R
import com.example.applang.databinding.ActivityMainBinding
import fr.uparis.applang.model.Language
import fr.uparis.applang.model.Word


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val model by lazy { ViewModelProvider(this)[MainViewModel::class.java] }

    private val GOOGLE_SEARCH_PATH : String = "https://www.google.com/search?q="

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //TODO insert only if needed
        insertAllLanguages()
        updateLanguagesList()
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
        var w: Word = Word(binding.motET.text.toString().lowercase(), "fr", "en", "...")
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
            var list = mutableListOf<String>()
            for (lang in it){
                list.add(lang.fullName)
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