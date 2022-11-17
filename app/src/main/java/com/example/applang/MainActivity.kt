package com.example.applang

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.applang.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun traduire(view: View){

    }

    fun chercher(view: View){

    }

}