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
    public val keyQuantity: String  = "Quantity"        // int stored
    public val keyFrequency: String = "Frequency"       // int stored

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
    fun getLanguageOfTheDay(): Language {
        // TODO Load current day language.
        // c'est pas très pratique d'avoir des id par jours, on pourrait avoir un tableau, ou bien d'avoir une fonction pour récupérer la langue du jour directement.
        return Language("en", "english");
    }
}