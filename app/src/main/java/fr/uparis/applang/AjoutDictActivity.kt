package fr.uparis.applang

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import fr.uparis.applang.R
import fr.uparis.applang.databinding.ActivityMainBinding

class AjoutDictActivity  : AppCompatActivity() {
    private lateinit var bindingDict: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_dict)
        bindingDict = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindingDict.root)
    }

    fun ajouterDict(view: View) {


    }
}