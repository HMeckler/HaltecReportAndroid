package com.example.haltecreport

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.Image
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.haltecreport.PreviousReports.Companion.adapter
import com.example.haltecreport.PreviousReports.Companion.dialogBuilder
import com.example.haltecreport.PreviousReports.Companion.rList
import com.example.haltecreport.PreviousReports.Companion.sharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PreviousReports : AppCompatActivity() {
    //These variables must remain in the companion object so that they can be accessed in the companion classes below that handle the creation and managing of the recyclerview
    companion object {
        var dialogBuilder: AlertDialog.Builder? = null
        var rList: MutableList<Report>? = null
        var sharedPreferences: SharedPreferences? = null
        lateinit var recyclerView: RecyclerView
        lateinit var adapter: MyAdapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_previous_reports)

        //References to the delete all and back buttons
        var deleteAllButton = findViewById<ImageView>(R.id.deleteAllButton)
        var backButton = findViewById<Button>(R.id.backButtonReports)

        //Grab the array of saved reports from shared preferences on the device. It is brought up as a JSON and then converted to a mutable list of reports for us to use
        sharedPreferences = getSharedPreferences("Reports", Context.MODE_PRIVATE)
        val json = sharedPreferences!!.getString("Reports", null)
        val gson = Gson()
        if(json != null) {
            rList = gson.fromJson(json, object : TypeToken<List<Report>>() {}.type)
            rList = rList!!.toMutableList()
        }


        //Grab and assign the recyclerview, and so long as there are reports to display, assign the custom adapter, gathering the elements with the getMyList() function
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        if(rList != null) {
            adapter = MyAdapter(this, getMyList(rList!!))
            recyclerView.adapter = adapter
        }

        //If the delete all button is pressed we are going to create an alert dialog to make the user confirm their action. If they say yes then we are going to save a null list to the shared preferences,
        //clear the models list that is used to locally populate the adapter, and then notify the adapter that the data has changed so that it is cleared. A toast is also sent to notify the user visually that
        //their deleting of all reports was successful
        deleteAllButton.setOnClickListener {
            if(rList != null) {
                dialogBuilder = AlertDialog.Builder(this)
                dialogBuilder!!.setMessage("Delete ALL reports?").setCancelable(false)
                    .setPositiveButton("Yes") { dialog, id ->
                        var editor = sharedPreferences?.edit()
                        editor?.putString("Reports", null)
                        editor?.apply()
                        editor?.commit()
                        var temp = rList!!.size
                        rList = null
                        adapter.models.clear()

                        adapter.notifyDataSetChanged()
                        adapter.notifyItemRangeRemoved(0, temp)
                        recyclerView.adapter = adapter

                        Toast.makeText(this, "All Reports Deleted", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("No", { dialog, id -> })
                val alert = dialogBuilder?.create()
                alert?.setTitle("Confirm Deletion")
                alert?.show()
            }
        }

        //The back button will merely take the user back to the main home screen of the app
        backButton.setOnClickListener {
            var intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
        }
    }

    //Function called in OnCreate to fill recyclerview adapter. This returns a list of models which are defined below in this class. Each of the models is created and assigned its information that it
    //needs to hold and then added to the ArrayList that will be returned at the end of this method
    private fun getMyList(rList: MutableList<Report>): ArrayList<Model> {
        var models: ArrayList<Model> = ArrayList()
        rList.forEach { report ->
            var model = Model()
            model.date = report.Date
            model.deleteButton = R.drawable.delete
            models.add(model)
        }
        return models
    }
}

//Custom model that holds the date the report was created as well as the reference to the delete button icon that will allow users to delete individual reports
class Model {
    var date: String? = null
    var deleteButton: Int = R.id.deleteButton
}

//This is the custom holder for the recyclerview that will hold the information of a single card in the recyclerview
class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    lateinit var date: TextView
    lateinit var deleteButton: ImageView

    init {
        //Initialize all of the elements of the holder by referencing the UI elements in each card of the recyclerview
        this.deleteButton = itemView.findViewById(R.id.deleteButton)
        this.date = itemView.findViewById(R.id.titleTV)
    }
}

//Custom adapter class for the recyclerview to handle the custom values and layout that we have set up
class MyAdapter(c: Context, models: ArrayList<Model>) : RecyclerView.Adapter<MyHolder>() {
    lateinit var c: Context //We need to hold the context so that we can launch other activities from it below
    lateinit var models: ArrayList<Model> //Keeping a reference to the list of models that is passed in

    init {
        this.c = c
        this.models = models
    }

    //When the holder is created, create a view using the layoutinflator based on the card view designed in R.layout.row, then pass that view to the MyHolder() init
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.row, null)
        return MyHolder(view)
    }

    override fun getItemCount(): Int {
        return models.size
    }

    //When the holder is bound set the holder values to match the position in the models array
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.date.text = models[position].date
        holder.deleteButton.setImageResource(models[position].deleteButton!!)

        //When a card is pressed, we will reference its position in our rList of reports and then pass that in to our ExportToPDF object and create a pdf for the user
        holder.itemView.setOnClickListener {
            for (i in 0 until models.size) {
                if (holder.date.text == models[i].date) {
                    val pdfCreator = ExportToPDF()
                    rList?.elementAt(holder.adapterPosition)?.let { it1 -> pdfCreator.createPDF(it1, c) }
                }
            }
        }
        //When the delete button is pressed on a card we will provide the user with an alert dialog to make them confirm their decision. If they do confirm it, we will remove the report they selected from the rList,
        //save the new list back to shared preferences, and then remove it from the local models list and inform the adapter that an item was removed so that it updates the recycler view
        holder.deleteButton.setOnClickListener {
            dialogBuilder = AlertDialog.Builder(c)
            dialogBuilder!!.setMessage("Delete this report?").setCancelable(false).setPositiveButton("Yes") { dialog, id ->
                rList?.removeAt(holder.adapterPosition)
                models.removeAt(holder.adapterPosition)
                var gson = Gson()
                var json: String = gson.toJson(rList)
                var editor = sharedPreferences?.edit()
                editor?.putString("Reports", json)
                editor?.apply()
                editor?.commit()

                adapter.notifyItemRemoved(holder.adapterPosition)
                Toast.makeText(c, "Report Deleted", Toast.LENGTH_SHORT).show()
            }
                .setNegativeButton("No", {dialog, id -> })
            val alert = dialogBuilder?.create()
            alert?.setTitle("Confirm Deletion")
            alert?.show()
        }
    }
}
