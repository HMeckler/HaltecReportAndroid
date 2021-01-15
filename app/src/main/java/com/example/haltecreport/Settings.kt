package com.example.haltecreport

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class Settings : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        //Shared Preferences saved on the user device to remember the location that has been input
        val sharedPreferences = getSharedPreferences("Location", MODE_PRIVATE)
        var backButton = findViewById<Button>(R.id.backButtonSettings)
        var locationInput = findViewById<EditText>(R.id.locationInput)

        //When the activity loads, if there is a saved value for the location then we will populate it in the text field
        locationInput.setText(sharedPreferences.getString("Location", ""))

        //When the back button is pressed we will grab whatever value is in the location text field and save it to the device shared preferences for use in creating reports later
        backButton.setOnClickListener {
            val editor = sharedPreferences.edit()
            editor.putString("Location", locationInput.text.toString())
            editor.apply()
            editor.commit()
            var intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
        }
    }
}