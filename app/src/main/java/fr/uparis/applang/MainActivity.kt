package fr.uparis.applang

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.example.applang.R
import com.example.applang.databinding.ActivityMainBinding
import java.net.URL
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val model by lazy { ViewModelProvider(this).get(MainViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btraduire.setOnClickListener {saveWordInDB()}
    }

    fun saveWordInDB(){
        var w: Word = Word(binding.motET.text.toString(), "fr", "en", "...");
        Log.d("DB","add word $w")
        //TODO fix java.lang.RuntimeException: Cannot create an instance of class fr.uparis.applang.MainViewModel
        //model.insertWord(w)
        //Log.d("DB","add word $w in DB")
        binding.motET.text.clear()
    }

    fun traduire(view: View){
        saveWordInDB()
    }

    fun chercher(view: View){

    }

}