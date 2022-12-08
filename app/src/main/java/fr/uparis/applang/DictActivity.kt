package fr.uparis.applang

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import fr.uparis.applang.databinding.ActivityDictBinding


class DictActivity  : OptionsMenuActivity() {
    private lateinit var bindingDict: ActivityDictBinding
    private lateinit var menu: Toolbar

    private lateinit var sharedPref : SharedPreferences
    private lateinit var sharedPrefEditor: SharedPreferences.Editor
    private val key: String = "activity"
    private val optionDict: String = "dictActivity"

    private val model by lazy {  ViewModelProvider(this).get(DictViewModel::class.java) }
    private val adapter by lazy { DictAdapter() }

    private var name = ""
    private var url = ""
    private val dictSelected = null
    private val GOOGLE_SEARCH_PATH : String = "https://www.google.com/search?q="


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_dict)
        bindingDict = ActivityDictBinding.inflate(layoutInflater)
        setContentView(bindingDict.root)

        // menu toolbar
        menu =  findViewById(R.id.acc_toolbar)
        setSupportActionBar(menu)
        menu.setTitle(R.string.app_name)

        // shared preferences
        sharedPref = getSharedPreferences("fr.uparis.applang", MODE_PRIVATE)
        sharedPrefEditor = sharedPref.edit()
        val jj = sharedPref.getString(key, "")
        Log.d("DICT: activity1 === ", jj!!)

        loadDictPreferencies()

        when {
            intent?.action == Intent.ACTION_SEND -> {
                if ("text/plain" == intent.type) {
                    saveDictPreferencies()
                    handleReceivedText(intent)
                }
            }
            else -> {
                // Handle other intents, such as being started from the home screen
            }
        }
    }
/*
    override fun onPause() {
        sharedPrefEditor.putString(key, optionDict).commit()
        val jj = sharedPref.getString(key, "")
        Log.d("DICT: activ === ", jj!!)
        super.onPause()
    }
*/

    // ================================= Buttons' functions =============================================
    fun enleverDict(view: View) {

    }

    fun ajouterDict(view: View) {
        val name = bindingDict.nomDictET.text.toString().trim()
        val url = bindingDict.lienDictET.text.toString().trim()

        if ( (name == "") || (url == "") )
        {
            AlertDialog.Builder(this)
                .setMessage("Les champs de texte ne doivent pas être vides")
                .setPositiveButton("Ok", DialogInterface.OnClickListener {
                        dialog, id -> finish()
                }).setCancelable(false)
                .show()
            return
        }

        // TODO make the requestComposition
        val requestComp = ""
            /*url
            .replace("\$langFromLong", langFrom.fullName.unaccent().lowercase(), true)
            .replace("\$langToLong", langTo.fullName.unaccent().lowercase(), true)
            .replace("\$langFrom", langFrom.id, true)
            .replace("\$langTo", langTo.id, true)
            .replace("\$word", wordText.replace(" ", "%20"), true)
            */

        model.insertDictionnary(name, url, requestComp)

        with(bindingDict) {
            nomDictET.text.clear()
            lienDictET.text.clear()
        }
    }


    fun chercherDict(view: View) {
        sharedPrefEditor.putString(key, optionDict).commit()
        val jj = sharedPref.getString(key, "")
        Log.d("DICT: activity2 === ", jj!!)

        val namedict = bindingDict.nomDictET.text.toString().lowercase()
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(GOOGLE_SEARCH_PATH + namedict))
        startActivity(browserIntent)
    }


    // ================================== Save / load activity state ==========================================

    private fun saveDictPreferencies() {
        sharedPrefEditor.putString("name", name)
        sharedPrefEditor.putString("url", url)
        sharedPrefEditor.commit()
    }

    private fun loadDictPreferencies() {
        name = sharedPref.getString("name", "")!!
        url = sharedPref.getString("url", "")!!
    }

    // =================================== Share processing ======================================================

    // handling the received data from the "share" process
    private fun handleReceivedText(intent: Intent) {
        intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
            loadDictPreferencies()
            bindingDict.lienDictET.setText(it)
        }
    }


}