package com.example.haltecreport

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Variables to hold references to the buttons on the main home screen that will allow the user to navigate the app
        var newReportButton = findViewById<Button>(R.id.newReportButton)
        var previousReportsButton = findViewById<Button>(R.id.previousReportsButton)
        var settingsButton = findViewById<Button>(R.id.settingsButton)

        //These three listeners merely take the user to the designated activity
        newReportButton.setOnClickListener {
            var intent = Intent(this, MainReportScreen::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
        }
        previousReportsButton.setOnClickListener {
            var intent = Intent(this, PreviousReports::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
        }
        settingsButton.setOnClickListener {
            var intent = Intent(this, Settings::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
        }
    }
}