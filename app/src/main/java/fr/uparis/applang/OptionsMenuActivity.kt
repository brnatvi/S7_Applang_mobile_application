package fr.uparis.applang

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

open class OptionsMenuActivity : AppCompatActivity() {
    protected lateinit var sharedPref : SharedPreferences
    protected lateinit var sharedPrefEditor: SharedPreferences.Editor
    protected val GOOGLE_SEARCH_PATH : String = "https://www.google.com/search?q="

    protected val keyName: String = "nameDict"
    protected val keyWord: String = "word"
    protected val keyShare: String = "linkShare"
    protected val keySrc: String = "langSrc"
    protected val keyDest: String = "langDest"
    protected val keyActivity: String = "activity"
    protected val optionDict: String = "dictActivity"
    protected val optionTransl: String = "translActivity"

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
                cleanPreferences()
                val  intentTransl = Intent(this, TranslateActivity::class.java)
                startActivity(intentTransl)
                return true
            }
            R.id.goDictActivity -> {
                cleanPreferences()
                val  intentDict = Intent(this, DictActivity::class.java)
                startActivity(intentDict)
                return true
            }
            R.id.goExersActivity -> {
                return true
            }
            R.id.goSettingsActivity -> {
                return true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }

    // ===================== Auxiliary functions for all child Activities ===============================

    protected fun cleanPreferences() {
        sharedPrefEditor.putString(keyName, "")
                        .putString(keyShare, "")
                        .putString(keyWord, "")
                        .putInt(keySrc, 0)
                        .putInt(keyDest, 0)
                        .commit()
    }
    protected fun makeToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

}