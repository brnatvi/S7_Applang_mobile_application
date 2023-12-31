package fr.uparis.applang.navigation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.Toolbar
import fr.uparis.applang.*
import fr.uparis.applang.databinding.StartLayoutBinding
import fr.uparis.applang.model.Dictionary
import fr.uparis.applang.model.Language


/**
 * First Activity started when app is launch. It's main goal is to initialize few thing needed at app start & redirect to an activity.
 */
class StartActivity : OptionsMenuActivity() {
    private lateinit var binding: StartLayoutBinding
    private lateinit var menu: Toolbar

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
        iniAppData()

        startNotificationService()
    }

    private fun chooseActivityShare(incomingIntent: Intent) {
        incomingIntent.getStringExtra(Intent.EXTRA_TEXT)?.let {
            intent = if (sharedPref.getString(keyActivity, "") == optionDict) {
                sharedPrefEditor
                    .putString(keyActivity, optionTransl)                               // back to default shared activity.
                    .putString(keyShare, it).commit()                                   // link to dictionary
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
            sharedPrefEditor.putString(keyActivity, optionTransl).putInt(keyDict, 0).putString(keyShare, "").commit()
            Intent(this, DictActivity::class.java)
        } else {
            sharedPrefEditor.putString(keyShare, "").putString(keyWord, "").commit()
            Intent(this, TranslateActivity::class.java)
        }
        startActivity(intent)
        finish()
    }

    private fun insertAllLanguages(){
        val languageListContent: String = getString(R.string.languageList)
        val languageList: MutableList<Language> = mutableListOf()
        for (line: String in languageListContent.split("\n")){
            val t = line.split(",")
            languageList.add(Language(t[0], t[1]))
        }
        model.insertLanguages(*languageList.toTypedArray())
    }

    /** Create all languages & fiew dictionary. */
    private fun iniAppData(){
        Log.d("TEMP","Ini app data for the 1st time.")
        insertAllLanguages()
        model.insertDictionary(Dictionary("Word Reference", "https://www.wordreference.com/", "\$langFrom\$langTo/\$word"))
        model.insertDictionary(Dictionary("Larousse", "https://www.larousse.fr/dictionnaires/", "\$langFromLong-\$langToLong/\$word/"))
        model.insertDictionary(Dictionary("Google translate", "https://translate.google.fr/", "?sl=\$langFrom&tl=\$langTo&text=\$word"))
    }
}