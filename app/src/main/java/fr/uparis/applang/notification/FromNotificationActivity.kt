package fr.uparis.applang.notification

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import fr.uparis.applang.ExercisesActivity

class FromNotificationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val url: String = intent.getStringExtra("URL")!!
        Log.d("NOTIFICATIONS", "Load extra.URL: $url")
        val openURL = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        //Start ExercisesActivity for when user will go back to application
        startActivity(Intent(this, ExercisesActivity::class.java))
        //Open the url
        startActivity(openURL)
        finish()
    }
}