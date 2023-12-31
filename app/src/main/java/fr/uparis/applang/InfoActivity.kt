package fr.uparis.applang

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import fr.uparis.applang.databinding.InfoLayoutBinding
import fr.uparis.applang.navigation.OptionsMenuActivity

/**
 * Text activity that describe the app for new users.
 */
class InfoActivity : OptionsMenuActivity() {
    private lateinit var binding: InfoLayoutBinding
    private lateinit var menu: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCurrentActivity(this)

        // create binding
        binding = InfoLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // menu toolbar
        menu =  findViewById(R.id.acc_toolbar)
        setSupportActionBar(menu)
        menu.setTitle(R.string.app_name)

        binding.translInfoIW.setOnClickListener {
            AlertDialog.Builder(this)
                .setMessage(R.string.message1)
                .setPositiveButton("Ok") { dialog, _ ->
                    dialog.dismiss()
                }
                .setNeutralButton("Y aller") { _, _ ->
                    startActivity(Intent(this, TranslateActivity::class.java))
                }.setCancelable(false)
                .show()
        }

        binding.dictInfoIW.setOnClickListener {
            AlertDialog.Builder(this)
                .setMessage(R.string.message2)
                .setPositiveButton("Ok") { dialog, _ ->
                    dialog.dismiss()
                }
                .setNeutralButton("Y aller") { _, _ ->
                    startActivity(Intent(this, DictActivity::class.java))
                }.setCancelable(false)
                .show()
        }
        binding.exersInfoIW.setOnClickListener {
            AlertDialog.Builder(this)
                .setMessage(R.string.message3)
                .setPositiveButton("Ok") { dialog, _ ->
                    dialog.dismiss()
                }
                .setNeutralButton("Y aller") { _, _ ->
                    startActivity(Intent(this, ExercisesActivity::class.java))
                }.setCancelable(false)
                .show()
        }

    }
}