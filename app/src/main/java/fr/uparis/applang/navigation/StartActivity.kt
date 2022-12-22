package fr.uparis.applang.navigation

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import fr.uparis.applang.DictActivity
import fr.uparis.applang.R
import fr.uparis.applang.TranslateActivity
import fr.uparis.applang.databinding.StartLayoutBinding


class StartActivity : OptionsMenuActivity() {
    private lateinit var binding: StartLayoutBinding
    private lateinit var menu: Toolbar
    val TAG: String = "START == "

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // create binding
        binding = StartLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // menu toolbar
        menu = findViewById(R.id.acc_toolbar)
        setSupportActionBar(menu)
        menu.setTitle(R.string.app_name)

        // shared preferences
        sharedPref = getSharedPreferences("fr.uparis.applang", MODE_PRIVATE)        // common preferences for all activities
        sharedPrefEditor = sharedPref.edit()

        // first launch - TranslateActivity to launch
        if (sharedPref.getString(keyActivity, "") == "") {
            sharedPrefEditor.putString(keyActivity, optionTransl).commit()
        }

        // choose activity in case of 1) return from share and 2) launch simple
        if (intent?.action == Intent.ACTION_SEND) {
            if ("text/plain" == intent.type) {
                chooseActivityShare(intent)
            }
        } else {
            chooseActivity()
        }
    }

    private fun chooseActivityShare(incomingIntent: Intent) {
        incomingIntent.getStringExtra(Intent.EXTRA_TEXT)?.let {
            intent = if (sharedPref.getString(keyActivity, "") == optionDict) {
                sharedPrefEditor.putString(keyShare, it).commit()                                   // link to dictionary
                Intent(this, DictActivity::class.java)
            } else {
                sharedPrefEditor.putString(keyShare, it).commit()                                   // link to translation of word
                Intent(this, TranslateActivity::class.java)
            }
            startActivity(intent)
            finish()
        }
    }

    private fun chooseActivity() {
        intent = if (sharedPref.getString(keyActivity, "") == optionDict) {
            sharedPrefEditor.putString(keyDict, "").putString(keyShare, "").commit()
            Intent(this, DictActivity::class.java)
        } else {
            sharedPrefEditor.putString(keyShare, "").putString(keyWord, "").commit()
            Intent(this, TranslateActivity::class.java)
        }
        startActivity(intent)
        finish()
    }
}