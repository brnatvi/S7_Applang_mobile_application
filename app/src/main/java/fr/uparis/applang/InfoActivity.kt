package fr.uparis.applang

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import fr.uparis.applang.databinding.InfoLayoutBinding

class InfoActivity : OptionsMenuActivity() {
    private lateinit var binding: InfoLayoutBinding
    private lateinit var menu: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // create binding
        binding = InfoLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // menu toolbar
        menu =  findViewById(R.id.acc_toolbar)
        setSupportActionBar(menu)
        menu.setTitle(R.string.app_name)
    }
}