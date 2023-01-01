package fr.uparis.applang

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class FromNotificationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val url: String = intent.getStringExtra("URL")!!
        val openURL = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(openURL)
    }
}