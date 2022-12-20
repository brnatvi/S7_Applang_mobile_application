package fr.uparis.applang

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import fr.uparis.applang.databinding.ExercisesLayoutBinding

class ExercisesActivity: OptionsMenuActivity() {
    private lateinit var binding: ExercisesLayoutBinding
    private lateinit var menu: Toolbar
    private val model by lazy { ViewModelProvider(this)[ViewModel::class.java] }

    val TAG: String = "EXERS ===="

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // create binding
        binding = ExercisesLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // menu toolbar
        menu =  findViewById(R.id.acc_toolbar)
        setSupportActionBar(menu)
        menu.setTitle(R.string.app_name)
    }
}