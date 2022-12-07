package fr.uparis.applang

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import fr.uparis.applang.databinding.ActivityDictBinding


class DictActivity  : OptionsMenuActivity() {
    private lateinit var bindingDict: ActivityDictBinding
    private lateinit var menu: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dict)
        bindingDict = ActivityDictBinding.inflate(layoutInflater)
        setContentView(bindingDict.root)

        // menu toolbar
        menu =  findViewById(R.id.acc_toolbar)
        setSupportActionBar(menu)
        menu.setTitle(R.string.app_name)
    }

    fun ajouterDict(view: View) {


    }

    fun chercherDict(view: View) {


    }

    fun enleverDict(view: View) {


    }
}