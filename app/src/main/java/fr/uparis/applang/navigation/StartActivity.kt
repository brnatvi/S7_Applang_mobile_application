package fr.uparis.applang.navigation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import fr.uparis.applang.*
import fr.uparis.applang.databinding.StartLayoutBinding
import fr.uparis.applang.model.Dictionary
import fr.uparis.applang.model.Language
import fr.uparis.applang.notification.NotificationService


class StartActivity : OptionsMenuActivity() {
    private lateinit var binding: StartLayoutBinding
    private lateinit var menu: Toolbar
    val TAG: String = "START == "
    protected val model by lazy { ViewModelProvider(this)[ViewModel::class.java] }

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

    private fun startNotificationService(){
        Log.d("NOTIFICATIONS","startNotificationService started")
        val intent = Intent(this, NotificationService::class.java ).apply {
            action = "sendNotifications"
        }
        applicationContext.startService(intent)
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
            sharedPrefEditor.putInt(keyDict, 0).putString(keyShare, "").commit()
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
        var languageList: MutableList<Language> = mutableListOf()
        for (line: String in languageListContent.split("\n")){
            var t = line.split(",")
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