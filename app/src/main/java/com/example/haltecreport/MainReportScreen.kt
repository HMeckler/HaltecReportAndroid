package com.example.haltecreport

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.ArrayList

class MainReportScreen : AppCompatActivity() {

    //These two variables are in the companion object so that we can save the state of whether or not the report is finished. This was introduced for a previous working of the app, but it does not affect any
    //functionality now and allows for a switch to returning to this activity after creating a report with no issues. The Array of wrench data is in the companion such that it can be accessed from the
    //DataInputScreen activity and makes for a way for us not to have to pass back a load of extras when we return to this screen
    companion object {
        //Array to hold objects of WrenchData which are stored in the order 250-1, 250-2, 80-1, 80-2, 100-1, 100-2, 140-1, 140-2
        var wrenchData: ArrayList<WrenchData> = ArrayList()
        var finished = false
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_report_screen)

        //Set the finished boolean to false when this activity is created
        finished = false
        var alert: AlertDialog? = null
        var dialogBuilder: AlertDialog.Builder? = null
        var button250 = findViewById<Button>(R.id.button250)
        var button80 = findViewById<Button>(R.id.button80)
        var button100 = findViewById<Button>(R.id.button100)
        var button140 = findViewById<Button>(R.id.button140)
        var finishButton = findViewById<Button>(R.id.finishButton)
        var backButton = findViewById<Button>(R.id.backButtonMainReport)

        //When this screen loads we will request the users permission to write to external storage. This is needed to be able to create and export PDFs
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            ActivityCompat.requestPermissions(this, permissions, 75)
        }

        //Instantiate the wrench data objects (8 In total)
        if(wrenchData.size == 0) {
            for (i in 0..7) {
                wrenchData.add(WrenchData())
            }
        }

        //When the back button is pressed on this screen we will create an alert dialog that will inform the user that if they back out, the report will not be saved, and ask them if that is what they want to do
        backButton.setOnClickListener {
            dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder!!.setMessage("The Current Report Will Not Be Saved.").setCancelable(false)
                .setPositiveButton("Yes") { dialog, id ->
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                }
                .setNegativeButton("No", { dialog, id -> })
            alert = dialogBuilder?.create()
            alert?.setTitle("Are You Sure?")
            alert?.show()
        }

        //When the finish button is pressed we will create an alert dialog to inform the user that the report will no longer be editeable after it is finished, and make them verfiy that they are sure they want to finish
        finishButton.setOnClickListener {
            dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder!!.setMessage("This report will not be editable after finishing.").setCancelable(false)
                    .setPositiveButton("Yes") { dialog, id ->

                        //If the user verifies they are ready to finish the report then we are going to grab the date and format it appropriately as well as grabbing the shared preference for location as set in settings
                        //and create an ExportToPDF object so that we can use it to create the PDF from the data we have gathered
                        val current = LocalDate.now()
                        val formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy")
                        val sharedPreferences = getSharedPreferences("Location", Context.MODE_PRIVATE)
                        val pdfCreator = ExportToPDF()

                        //Create a new report object and assign its values to the wrench data array that is holding all the data, the date we just grabbed above, and the location as set in settings
                        var report = Report()
                        report.Data = wrenchData
                        report.Date = current.format(formatter).toString()
                        report.Location = sharedPreferences.getString("Location", "N/A").toString()

                        //Grab the shared preference for the array of reports already saved to the device
                        val sharedPreferencesReports = getSharedPreferences("Reports", Context.MODE_PRIVATE)
                        var json = sharedPreferencesReports.getString("Reports", null)
                        val gson = Gson()

                        //If there are reports already saved to the device then JSON will not be null and we enter this block.
                        if(json != null) {
                            //Create a list of reports based on the JSON from shared preferences and then make that list mutable. We will insert the new report we are finishing at position 0 and then write
                            //the new list of reports back to shared preferences to update it
                            var rList: List<Report> = gson.fromJson(json, object : TypeToken<List<Report>>() {}.type)
                            rList = rList.toMutableList()
                            rList.add(0, report)
                            json = gson.toJson(rList)
                            var editor = sharedPreferencesReports.edit()
                            editor.putString("Reports", json)
                            editor.apply()
                            editor.commit()
                        }
                        // If JSON was null then there are no reports saved on the device at the moment
                        else {
                            //We will create a new list of Reports of size one and add in our current report to that list. We will then convert that array to json and save it to the shared preferences of the device
                            var rList = List<Report>(1) {report}.toMutableList()
                            json = gson.toJson(rList)
                            var editor = sharedPreferencesReports.edit()
                            editor.putString("Reports", json)
                            editor.apply()
                            editor.commit()
                        }
                        //Set finished to true as the report has been saved and finished
                        finished = true
                        //Call our createPDF method passing in the context and the report we just finished such that the PDF can be created and presented 
                        pdfCreator.createPDF(report, this)
                        finish()
                    }
                    .setNegativeButton("No", { dialog, id -> })
            alert = dialogBuilder?.create()
            alert?.setTitle("Are You Sure?")
            alert?.show()
        }

        //The following listeners will direct the user to the DataInputScreen passing in the integer of the wrench type as well as the string title based on which of the four buttons were pressed
        button250.setOnClickListener {
            var intent = Intent(this, DataInputScreen::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            intent.putExtra("title", "Adjustable 250 lbft")
            intent.putExtra("type", 250)
            startActivity(intent)
        }

        button80.setOnClickListener {
            var intent = Intent(this, DataInputScreen::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            intent.putExtra("title", "Preset 80 lbft")
            intent.putExtra("type", 80)
            startActivity(intent)
        }

        button100.setOnClickListener {
            var intent = Intent(this, DataInputScreen::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            intent.putExtra("title", "Preset 100 lbft")
            intent.putExtra("type", 100)
            startActivity(intent)
        }

        button140.setOnClickListener {
            var intent = Intent(this, DataInputScreen::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            intent.putExtra("title", "Preset 140 lbft")
            intent.putExtra("type", 140)
            startActivity(intent)
        }
    }

    //The resume method here is used so that when the user returns from viewing the PDF that was just created, we will return to the main page of the app rather than to this activity
    override fun onResume() {
        super.onResume()
        if(finished) {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
        }
        if(ExportToPDF.file != null) {
            FileOutputStream(ExportToPDF.file!!).close()
            ExportToPDF.file!!.delete()
        }
    }
}