package fr.uparis.applang

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.Toolbar
import fr.uparis.applang.databinding.ActivityStartBinding


class StartActivity : OptionsMenuActivity(){
    private lateinit var binding: ActivityStartBinding
    private lateinit var menu: Toolbar
    val TAG: String = "START == "

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // create binding
        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d(TAG, "onCreate")

        // menu toolbar
        menu = findViewById(R.id.acc_toolbar)
        setSupportActionBar(menu)
        menu.setTitle(R.string.app_name)

        // shared preferences
        sharedPref = getSharedPreferences("fr.uparis.applang", MODE_PRIVATE)        // common preferences for all activities
        sharedPrefEditor = sharedPref.edit()
        Log.d(TAG + "SharedPref ", sharedPref.toString())                            // DEBUG test if they are really commons

        // first launch - TranslateActivity to launch
        if (sharedPref.getString(keyActivity, "") == "") {
            sharedPrefEditor.putString(keyActivity, optionTransl).commit()
            Log.d(TAG, "first launch ")                                              // DEBUG
        }

        // DEBUG test which activity to launch is current
        val jj = sharedPref.getString(keyActivity, "")
        Log.d(TAG + "activity ", jj!!)

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
                Log.d(TAG, "launch DictActivity" )                                             // DEBUG
                sharedPrefEditor.putString(keyShare, it).commit()                                   // link to dictionary
                Log.d(TAG + "link ==", it)                                                      // DEBUG
                Intent(this, DictActivity::class.java)
            } else {
                Log.d(TAG, "launch TranslateActivity" )                                        // DEBUG
                sharedPrefEditor.putString(keyShare, it).commit()                                   // link to translation of word
                Log.d(TAG + "link ==", it)                                                      // DEBUG
                Intent(this, TranslateActivity::class.java)
            }
            startActivity(intent)
        }
    }

    private fun chooseActivity() {
        intent = if (sharedPref.getString(keyActivity, "") == optionDict) {
            Log.d("START ==", "launch DictActivity" )                                      // DEBUG
            sharedPrefEditor.putString(keyName, "")
                            .putString(keyShare, "")
                            .commit()
            Intent(this, DictActivity::class.java)

        } else {
            Log.d("START ==", "launch TranslateActivity" )                                 // DEBUG
            sharedPrefEditor.putString(keyShare, "")
                            .putString(keyWord, "")
                            .commit()
            Intent(this, TranslateActivity::class.java)
        }
        startActivity(intent)
    }

}