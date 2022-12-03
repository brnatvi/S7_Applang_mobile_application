package fr.uparis.applang


import android.content.Intent
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity

open class OptionsMenuActivity : AppCompatActivity() {

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.goMainActivity -> {
                Log.d("From menu == ", "go MainActivity")
                val  intentMain = Intent(this, MainActivity::class.java)
                startActivity(intentMain)
                return true
            }
            R.id.goDictActivity -> {
                Log.d("From menu == ", "go DictActivity")
                val  intentDict = Intent(this, AjoutDictActivity::class.java)
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

}