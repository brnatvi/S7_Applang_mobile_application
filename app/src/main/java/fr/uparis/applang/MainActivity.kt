package fr.uparis.applang

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.example.applang.R
import com.example.applang.databinding.ActivityMainBinding
import fr.uparis.applang.model.LanguageApplication
import fr.uparis.applang.model.Word

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
//    private val model by lazy { ViewModelProvider(this,MainViewModelFactory(LanguageApplication())).get(MainViewModel::class.java) }
    private val model by lazy { ViewModelProvider(this).get(MainViewModel::class.java) }

    private val GOOGLE_SEARCH_PATH : String = "https://www.google.com/search?q="

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updateList()
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
        startActivity(browserIntent);
    }

    fun saveWordInDB(){
        var w: Word = Word(binding.motET.text.toString(), "fr", "en", "...");
        Log.d("DB","add word $w")
        model.insertWord(w)
        binding.motET.text.clear()

        //TODO use somewhere usefull
        updateList()
    }

    fun updateList(){
        model.loadAllWord()
        model.words.removeObservers(this)
        model.words.observe(this){
            Log.d("DB","list: $it")
        }
    }

}