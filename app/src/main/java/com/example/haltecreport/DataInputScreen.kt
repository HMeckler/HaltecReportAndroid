package com.example.haltecreport

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import java.util.*

class DataInputScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_input_screen)

        //Variables to hold references to all the UI elements that we will be using from the UI
        var backButton = findViewById<Button>(R.id.backButtonInput)
        var wrenchTitle = findViewById<TextView>(R.id.wrenchTitle)
        var serialOne = findViewById<EditText>(R.id.serialOne)
        var serialTwo = findViewById<EditText>(R.id.serialTwo)
        var readingsOneOne = findViewById<EditText>(R.id.readingsOneOne)
        var readingsOneTwo = findViewById<EditText>(R.id.readingsOneTwo)
        var readingsTwoOne = findViewById<EditText>(R.id.readingsTwoOne)
        var readingsTwoTwo = findViewById<EditText>(R.id.readingsTwoTwo)
        var toleranceGroupOne = findViewById<RadioGroup>(R.id.toleranceGroupOne)
        var toleranceGroupTwo = findViewById<RadioGroup>(R.id.toleranceGroupTwo)
        var repairGroupOne = findViewById<RadioGroup>(R.id.repairsGroupOne)
        var repairGroupTwo = findViewById<RadioGroup>(R.id.repairsGroupTwo)

        //Grab the wrenchType and the wrenchTitle that will have been passed from MainReportScreen. These two variables allow us to know which wrench we are entering data for
        var wrenchType = intent.extras?.get("type") as Int
        wrenchTitle.text = intent.extras?.get("title") as String

        //When the back button is pressed we will be returning back to the MainReportScreen and finishing this activity so that the information can be repopulated when we re-enter for a new wrench
        backButton.setOnClickListener {
            var intent = Intent(this, MainReportScreen::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }

        //The following two functions are for the tolerance sliders of each wrench on the page. When the radio button is changed, we will check to see if it is true or false and assign the result to a var
        //We will then set that var as the value for the InTolerance variable for the corresponding wrench in wrenchData which lives in MainReportScreen
        toleranceGroupOne.setOnCheckedChangeListener { group, checkedId ->
            var state = checkedId == R.id.yesOne
            when(wrenchType) {
                250 -> {
                    MainReportScreen.wrenchData[0].InTolerance = state
                }
                80 -> {
                    MainReportScreen.wrenchData[2].InTolerance = state
                }
                100 -> {
                    MainReportScreen.wrenchData[4].InTolerance = state
                }
                140 -> {
                    MainReportScreen.wrenchData[6].InTolerance = state
                }
            }
        }

        toleranceGroupTwo.setOnCheckedChangeListener { group, checkedId ->
            var state = checkedId == R.id.yesThree
            when(wrenchType) {
                250 -> {
                    MainReportScreen.wrenchData[1].InTolerance = state
                }
                80 -> {
                    MainReportScreen.wrenchData[3].InTolerance = state
                }
                100 -> {
                    MainReportScreen.wrenchData[5].InTolerance = state
                }
                140 -> {
                    MainReportScreen.wrenchData[7].InTolerance = state
                }
            }
        }

        //The following two functions do the exact same thing as the above two except they are for the 'Need Additional Repairs' radio buttons rather than the tolerance
        repairGroupOne.setOnCheckedChangeListener { group, checkedId ->
            var state = checkedId == R.id.yesTwo
            when(wrenchType) {
                250 -> {
                    MainReportScreen.wrenchData[0].NeedRepairs = state
                }
                80 -> {
                    MainReportScreen.wrenchData[2].NeedRepairs = state
                }
                100 -> {
                    MainReportScreen.wrenchData[4].NeedRepairs = state
                }
                140 -> {
                    MainReportScreen.wrenchData[6].NeedRepairs = state
                }
            }
        }

        repairGroupTwo.setOnCheckedChangeListener { group, checkedId ->
            var state = checkedId == R.id.yesFour
            when(wrenchType) {
                250 -> {
                    MainReportScreen.wrenchData[1].NeedRepairs = state
                }
                80 -> {
                    MainReportScreen.wrenchData[3].NeedRepairs = state
                }
                100 -> {
                    MainReportScreen.wrenchData[5].NeedRepairs = state
                }
                140 -> {
                    MainReportScreen.wrenchData[7].NeedRepairs = state
                }
            }
        }

        //The timer is needed to prevent the running of the code in the textChangedListener until the user is done editing the text
        var timer = Timer()
        val DELAY: Long = 1000

        //The following functions are all pretty much the same. They will cancel the timer that may be running, start a new timer, and then when the timer is eventually up, meaning the user is done editing the text field
        //They will grab the value from the text field and save it to the corresponding variable and position in wrenchData which again, lives in MainReportScreen's companion object
        serialOne.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                timer.cancel()
                timer = Timer()
                timer.schedule(object : TimerTask() {
                    override fun run() {
                        when(wrenchType) {
                            250 -> {
                                MainReportScreen.wrenchData[0].SerialNumber = s.toString()
                            }
                            80 -> {
                                MainReportScreen.wrenchData[2].SerialNumber = s.toString()
                            }
                            100 -> {
                                MainReportScreen.wrenchData[4].SerialNumber = s.toString()
                            }
                            140 -> {
                                MainReportScreen.wrenchData[6].SerialNumber = s.toString()
                            }
                        }
                    }
                }, DELAY)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        serialTwo.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                timer.cancel()
                timer = Timer()
                timer.schedule(object : TimerTask() {
                    override fun run() {
                        when(wrenchType) {
                            250 -> {
                                MainReportScreen.wrenchData[1].SerialNumber = s.toString()
                            }
                            80 -> {
                                MainReportScreen.wrenchData[3].SerialNumber = s.toString()
                            }
                            100 -> {
                                MainReportScreen.wrenchData[5].SerialNumber = s.toString()
                            }
                            140 -> {
                                MainReportScreen.wrenchData[7].SerialNumber = s.toString()
                            }
                        }
                    }
                }, DELAY)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        readingsOneOne.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                timer.cancel()
                timer = Timer()
                timer.schedule(object : TimerTask() {
                    override fun run() {
                        when(wrenchType) {
                            250 -> {
                                MainReportScreen.wrenchData[0].ReadingOne = s.toString()
                            }
                            80 -> {
                                MainReportScreen.wrenchData[2].ReadingOne = s.toString()
                            }
                            100 -> {
                                MainReportScreen.wrenchData[4].ReadingOne = s.toString()
                            }
                            140 -> {
                                MainReportScreen.wrenchData[6].ReadingOne = s.toString()
                            }
                        }
                    }
                }, DELAY)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        readingsOneTwo.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                timer.cancel()
                timer = Timer()
                timer.schedule(object : TimerTask() {
                    override fun run() {
                        when(wrenchType) {
                            250 -> {
                                MainReportScreen.wrenchData[0].ReadingTwo = s.toString()
                            }
                            80 -> {
                                MainReportScreen.wrenchData[2].ReadingTwo = s.toString()
                            }
                            100 -> {
                                MainReportScreen.wrenchData[4].ReadingTwo = s.toString()
                            }
                            140 -> {
                                MainReportScreen.wrenchData[6].ReadingTwo = s.toString()
                            }
                        }
                    }
                }, DELAY)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        readingsTwoOne.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                timer.cancel()
                timer = Timer()
                timer.schedule(object : TimerTask() {
                    override fun run() {
                        when(wrenchType) {
                            250 -> {
                                MainReportScreen.wrenchData[1].ReadingOne = s.toString()
                            }
                            80 -> {
                                MainReportScreen.wrenchData[3].ReadingOne = s.toString()
                            }
                            100 -> {
                                MainReportScreen.wrenchData[5].ReadingOne = s.toString()
                            }
                            140 -> {
                                MainReportScreen.wrenchData[7].ReadingOne = s.toString()
                            }
                        }
                    }
                }, DELAY)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        readingsTwoTwo.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                timer.cancel()
                timer = Timer()
                timer.schedule(object : TimerTask() {
                    override fun run() {
                        when(wrenchType) {
                            250 -> {
                                MainReportScreen.wrenchData[1].ReadingTwo = s.toString()
                            }
                            80 -> {
                                MainReportScreen.wrenchData[3].ReadingTwo = s.toString()
                            }
                            100 -> {
                                MainReportScreen.wrenchData[5].ReadingTwo = s.toString()
                            }
                            140 -> {
                                MainReportScreen.wrenchData[7].ReadingTwo = s.toString()
                            }
                        }
                    }
                }, DELAY)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }
}