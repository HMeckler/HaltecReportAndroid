package com.example.haltecreport

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import kotlin.math.roundToInt

@RequiresApi(Build.VERSION_CODES.KITKAT)
class ExportToPDF {

    companion object {
        var file: File? = null
    }

    var report: PdfDocument = PdfDocument()
    var paint: Paint = Paint()
    var pageInfo = PdfDocument.PageInfo.Builder(612,792,1).create() //This page size was arbitrary and just seemed to look pretty good. No further thought went into it

    //Function called when an inspection report is saved, it passes in the InspectionData object that contains all the information we need to create and save a report on the inspection
    fun createPDF(torqueReport: Report, c: Context) {
        var page = report.startPage(pageInfo)
        var canvas = page.canvas

        //Title of the page that is centered and 40 from the top
        paint.textSize = 24F
        paint.isUnderlineText = false
        paint.isFakeBoldText = true
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("Torque Report", (pageInfo.pageWidth/2).toFloat(), 40F, paint)

        //Headers for the Date and Store Location that will be in the upper right and upper left of the page, respectively
        paint.textSize = 18F
        paint.isFakeBoldText = true
        paint.isUnderlineText = true
        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText("Report Date", pageInfo.pageWidth.toFloat() - 25F, 25F, paint)
        paint.textAlign = Paint.Align.LEFT
        canvas.drawText("Store Location", 25F, 25F, paint)

        //Actual values for date and store location that will be stored beneath their respective headers that are printed in the block above this one
        paint.isFakeBoldText = false
        paint.isUnderlineText = false
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText(torqueReport.Date, pageInfo.pageWidth.toFloat() - 25F - (paint.measureText("Report Date")/2), 50F, paint)
        canvas.drawText(torqueReport.Location, 25F + (paint.measureText("Store Location")/2), 50F, paint)

        //Draw a black line seperating the title and date from the rest of the report. Solely for visual appeal and organization
        paint.color = Color.BLACK
        canvas.drawLine(25F, 80F, pageInfo.pageWidth.toFloat() - 25F, 80F, paint)

        paint.color = Color.BLACK

        //The number of rows and columns for this application are constant, however it made things easier to leave them as variables here to reduce the amount of code I needed to change
        var numRows = 9
        var numCols = 6

        paint.textAlign = Paint.Align.CENTER
        paint.style = Paint.Style.STROKE //Sets the drawing style to outline rather than fill

        //Draw a rectangle that is centered on the page and grants 50 height for each row we need
        canvas.drawRect(66F, 120F, pageInfo.pageWidth - 66F, 120 + numRows * 50F, paint)

        //For each row we have, draw a horizontal line across the table
        for(i in 0 until numRows) {
            canvas.drawLine(66F, 120 + (i*50F), pageInfo.pageWidth - 66F, 120 + (i*50F), paint)
        }

        //For each column in the table we will be creating a vertical line in the table that are spaced evenly based on the number of columns that we have
        for(i in 0 until numCols) {
            canvas.drawLine(66F + (i*(480/numCols)), 120F, 66F + (i*(480/numCols)), 120F + numRows * 50, paint)
        }

        //Draw in the titles of the table
        paint.style = Paint.Style.FILL
        paint.isFakeBoldText = true
        paint.textSize = 10F
        canvas.drawText("Adjustable", 66F + (480/numCols)/2, 190F, paint)
        canvas.drawText("250 lbft", 66F + (480/numCols)/2, 205F, paint)

        canvas.drawText("Adjustable", 66F + (480/numCols)/2, 240F, paint)
        canvas.drawText("250 lbft", 66F + (480/numCols)/2, 255F, paint)

        canvas.drawText("Preset", 66F + (480/numCols)/2, 290F, paint)
        canvas.drawText("80 lbft", 66F + (480/numCols)/2, 305F, paint)

        canvas.drawText("Preset", 66F + (480/numCols)/2, 340F, paint)
        canvas.drawText("80 lbft", 66F + (480/numCols)/2, 355F, paint)

        canvas.drawText("Preset", 66F + (480/numCols)/2, 390F, paint)
        canvas.drawText("100 lbft", 66F + (480/numCols)/2, 405F, paint)

        canvas.drawText("Preset", 66F + (480/numCols)/2, 440F, paint)
        canvas.drawText("100 lbft", 66F + (480/numCols)/2, 455F, paint)

        canvas.drawText("Preset", 66F + (480/numCols)/2, 490F, paint)
        canvas.drawText("140 lbft", 66F + (480/numCols)/2, 505F, paint)

        canvas.drawText("Preset", 66F + (480/numCols)/2, 540F, paint)
        canvas.drawText("140 lbft", 66F + (480/numCols)/2, 555F, paint)

        canvas.drawText("Torque Wrench", 66F + 80 + 40, 140F, paint)
        canvas.drawText("Serial #", 66F + 80 + 40, 155F, paint)

        canvas.drawText("Verified", 66F + 160 + 40, 140F, paint)
        canvas.drawText("Reading 1", 66F + 160 + 40, 155F, paint)

        canvas.drawText("Verified", 66F + 240 + 40, 140F, paint)
        canvas.drawText("Reading 2", 66F + 240 + 40, 155F, paint)

        canvas.drawText("In Tolerance", 66F + 320 + 40, 140F, paint)
        canvas.drawText("+/- 4%", 66F + 320 + 40, 155F, paint)

        canvas.drawText("Additional", 66F + 400 + 40, 135F, paint)
        canvas.drawText("Repairs", 66F + 400 + 40, 150F, paint)
        canvas.drawText("Needed", 66F + 400 + 40, 165F, paint)

        //The beginning offset where the first values will be printed
        var vertOffset = 170

        //Prints an entire row of data in the table and then increments the vertOffset to print the following row
        torqueReport.Data.forEach { wrenchData ->
            canvas.drawText(if(wrenchData.SerialNumber != null) wrenchData.SerialNumber!! else "N/A" , 66F + 80F + 40F, vertOffset + 25F, paint)
            canvas.drawText(if(wrenchData.ReadingOne != null) wrenchData.ReadingOne!! else "N/A" , 66F + 160F + 40F, vertOffset + 25F, paint)
            canvas.drawText(if(wrenchData.ReadingTwo != null) wrenchData.ReadingTwo!! else "N/A" , 66F + 240F + 40F, vertOffset + 25F, paint)
            canvas.drawText(wrenchData.InTolerance.toString(), 66F + 320F + 40F, vertOffset + 25F, paint)
            canvas.drawText(wrenchData.NeedRepairs.toString(), 66F + 400F + 40F, vertOffset + 25F, paint)
            vertOffset += 50
        }

        //Finish the page and then save the pdf to the user device for use
        report.finishPage(page)
        file = File(Environment.getExternalStorageDirectory(), "/Report-" + torqueReport.Date + ".pdf")

        //Actually write the pdf to storage on the device
        try {
            report.writeTo(FileOutputStream(file))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        //Close the report and create an intent to launch an action view of the newly saved pdf on the device, gives the uesr the option to share it
        report.close()
        var intentV = Intent(Intent.ACTION_VIEW)
        intentV.setDataAndType(FileProvider.getUriForFile(c, "com.example.haltecreport.provider", file!!), "application/pdf")

        intentV.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intentV.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intentV.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        c.startActivity(intentV)
    }

}