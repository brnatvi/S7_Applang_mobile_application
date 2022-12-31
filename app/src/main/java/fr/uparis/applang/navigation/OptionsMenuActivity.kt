package fr.uparis.applang.navigation

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import fr.uparis.applang.*
import fr.uparis.applang.model.Language
import fr.uparis.applang.model.LanguageApplication
import java.util.*

open class OptionsMenuActivity : AppCompatActivity() {
    protected lateinit var sharedPref : SharedPreferences
    protected lateinit var sharedPrefEditor: SharedPreferences.Editor
    protected val GOOGLE_SEARCH_PATH : String = "https://www.google.com/search?q="

    protected val keyDict: String      = "nameDict"          // int stored
    protected val keyWord: String      = "word"              // string stored
    protected val keyShare: String     = "linkShare"         // string stored
    protected val keySrc: String       = "langSrc"           // int stored
    protected val keyDest: String      = "langDest"          // int stored
    protected val keyActivity: String  = "activity"          // string stored
    protected val optionDict: String   = "dictActivity"      // string stored
    protected val optionTransl: String = "translActivity"    // string stored

    protected val keyLundi: String     = "Lundi"           // int stored
    protected val keyMardi: String     = "Mardi"           // int stored
    protected val keyMercredi: String  = "Mercredi"        // int stored
    protected val keyJeudi: String     = "Jeudi"           // int stored
    protected val keyVendredi: String  = "Vendredi"        // int stored
    protected val keySamedi: String    = "Samedi"          // int stored
    protected val keyDimanche: String  = "Dimanche"        // int stored
    val keyQuantity: String  = "Quantity"        // int stored
    val keyFrequency: String = "Frequency"       // int stored

    protected lateinit var currentActivity: AppCompatActivity



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        sharedPref = getSharedPreferences("fr.uparis.applang", MODE_PRIVATE)
        sharedPrefEditor = sharedPref.edit()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.goTranslateActivity -> {
                if (currentActivity is TranslateActivity) {
                    currentActivity.finish()
                }
                cleanPreferences()
                val  intentTransl = Intent(this, TranslateActivity::class.java)
                startActivity(intentTransl)
                return true
            }
            R.id.goDictActivity -> {
                if (currentActivity is DictActivity) {
                    currentActivity.finish()
                }
                cleanPreferences()
                val  intentDict = Intent(this, DictActivity::class.java)
                startActivity(intentDict)
                return true
            }
            R.id.goExersActivity -> {
                if (currentActivity is ExercisesActivity) {
                    currentActivity.finish()
                }
                cleanPreferences()
                val  intentExers = Intent(this, ExercisesActivity::class.java)
                startActivity(intentExers)
                return true
            }
            R.id.goInfoActivity -> {
                if (currentActivity is InfoActivity) {
                    currentActivity.finish()
                }
                cleanPreferences()
                val  intentInfo = Intent(this, InfoActivity::class.java)
                startActivity(intentInfo)
                return true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }

    // ===================== Auxiliary functions for all child Activities ===============================

    protected fun cleanPreferences() {
        sharedPrefEditor.putInt(keyDict, 0)
                        .putString(keyShare, "")
                        .putString(keyWord, "")
                        .putInt(keySrc, 0)
                        .putInt(keyDest, 0)
                        .putString(optionDict, "")
                        .putString(optionTransl, "")
                        .commit()
    }
    protected fun makeToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }


    @JvmName("getCurrentActivity1")
    protected fun getCurrentActivity() : AppCompatActivity {
        return currentActivity
    }
    @JvmName("setCurrentActivity1")
    protected fun setCurrentActivity(activity: AppCompatActivity ) {
        currentActivity = activity
    }

    // Used by NotificationService
    /**
     * Return the current language to train
     * At this time this.sharedPref might be null. So we used a not null given in args.
     */
    fun getLanguageOfTheDay(sharedPref: SharedPreferences): Language {
        val calendar: Calendar = Calendar.getInstance()
        val day: Int = calendar.get(Calendar.DAY_OF_WEEK)

        // TODO c'est pas une bonne idée de sauvegarder un int, parce que la base de donnée peut renvoyé les langages dans un ordre différent.
        val languageNumber: Int
        when (day) {
            Calendar.MONDAY -> {
                languageNumber = sharedPref.getInt(keyLundi, 0);
            }
            Calendar.TUESDAY -> {
                languageNumber = sharedPref.getInt(keyMardi, 0);
            }
            Calendar.WEDNESDAY -> {
                languageNumber = sharedPref.getInt(keyMercredi, 0);
            }
            Calendar.THURSDAY -> {
                languageNumber = sharedPref.getInt(keyJeudi, 0);
            }
            Calendar.FRIDAY -> {
                languageNumber = sharedPref.getInt(keyVendredi, 0);
            }
            Calendar.SATURDAY -> {
                languageNumber = sharedPref.getInt(keySamedi, 0);
            }
            Calendar.SUNDAY -> {
                languageNumber = sharedPref.getInt(keyDimanche, 0);
            }
        }
        // TODO Load current day language from int value.
//        val dao = (application as LanguageApplication).database.langDAO()
//        val languages = dao.loadAllLanguage()
//        languages.observe(this){
//
//        }
        return Language("en", "english");
    }
}