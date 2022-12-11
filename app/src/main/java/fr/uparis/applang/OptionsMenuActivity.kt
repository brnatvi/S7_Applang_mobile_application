package fr.uparis.applang

import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity

open class OptionsMenuActivity : AppCompatActivity() {
    private lateinit var sharedPref : SharedPreferences
    private lateinit var sharedPrefEditor: SharedPreferences.Editor

    private val keyName: String = "nameDict"
    private val keyWord: String = "word"
    private val keyShare: String = "linkShare"

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
                clearReferencies()
                val  intentTransl = Intent(this, TranslateActivity::class.java)
                startActivity(intentTransl)
                return true
            }
            R.id.goDictActivity -> {
                clearReferencies()
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

    private fun clearReferencies() {
        sharedPrefEditor.putString(keyName, "").putString(keyShare, "").putString(keyWord, "").commit()
    }

}