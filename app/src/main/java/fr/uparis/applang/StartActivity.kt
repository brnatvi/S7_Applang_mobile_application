package fr.uparis.applang

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.Toolbar
import fr.uparis.applang.databinding.ActivityDictBinding

class StartActivity : OptionsMenuActivity(){
    private lateinit var binding: ActivityDictBinding
    private lateinit var menu: Toolbar
    private lateinit var sharedPref : SharedPreferences
    private lateinit var sharedPrefEditor: SharedPreferences.Editor
    private val key: String = "activity"
    private val optionTransl: String = "translActivity"
    private val optionDict: String = "dictActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu_layout)
        binding = ActivityDictBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // menu toolbar
        menu = findViewById(R.id.acc_toolbar)
        setSupportActionBar(menu)
        menu.setTitle(R.string.app_name)

        // shared preferences
        sharedPref = getSharedPreferences("fr.uparis.applang", MODE_PRIVATE)
        sharedPrefEditor = sharedPref.edit()

        if (sharedPref.getString(key, "") == "")
        {
            sharedPrefEditor.putString(key, optionTransl).commit()
            Log.d("START: ", "first launch")
        }

        val jj = sharedPref.getString(key, "")
        Log.d("START: activity === ", jj!!)

        intent = if (sharedPref.getString(key, "") == optionDict) {
            Log.d("START ==", "launch DictActivity" )
            Intent(this, DictActivity::class.java)

        } else {
            Log.d("START ==", "launch TranslateActivity" )
            Intent(this, TranslateActivity::class.java)

        }

        startActivity(intent)
        finish()
    }



}