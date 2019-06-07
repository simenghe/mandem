package com.example.boardroombooking

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.RequiresApi
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner

import kotlinx.android.synthetic.main.activity_make_booking.*
import kotlinx.android.synthetic.main.content_make_booking.*
import java.io.Serializable
import java.lang.Error
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneId.*
import java.time.format.DateTimeFormatter
import java.util.*


@Suppress("DEPRECATION")
class MakeBooking() : AppCompatActivity(),Serializable,Parcelable{
    @RequiresApi(Build.VERSION_CODES.O)
    fun convertToLocalDateTime(dateToConvert: Date): LocalDateTime {
        return dateToConvert.toInstant()
            .atZone(systemDefault())
            .toLocalDateTime()
    }
    constructor(parcel: Parcel) : this() {
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        val dataList = intent.getParcelableArrayListExtra<Data>("datalist")
        println("SIZE: " +dataList.count())
        dataList.forEach {
            println(it.startingTime)
        }
        val startTimeList = MainAdapter(dataList).removeOptions()
        val length = startTimeList.count()
        val startAMList = ArrayList<String>()
        val startPMList = ArrayList<String>()
        for ((i, value) in startTimeList.withIndex()) {
            val ldt = convertToLocalDateTime(startTimeList[i])
            val custSpinner = "h:mm a"
            val ldtFormatter = DateTimeFormatter.ofPattern(custSpinner)
            val formattedSpinner = ldt.format(ldtFormatter)
            if(formattedSpinner.takeLast(2)=="AM"){
                startAMList.add(formattedSpinner.dropLast(2)) //remove the last two
                println("AM has been Spotted")
            }else if(formattedSpinner.takeLast(2)=="PM"){
                println("PM has been Spotted")
                startPMList.add(formattedSpinner.dropLast(2)) //remove the last two
            }else{
                println(Error().toString())
            }
            println(formattedSpinner)
        }
        println("RATNU COMMENCING" + startAMList.count())
        startAMList.forEach {
            println(it)
        }
        println("PM COMMENCING")
        startPMList.forEach {
            println(it)
        }
        //Create an AM list, and PM list for this to work...




        requestedOrientation = (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_make_booking)
        val meridian = listOf("AM","PM")
        val times = listOf("10:00","11:00","12:00")
        spinner_start.adapter = ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,startPMList)
        spinner_start.onItemSelectedListener = SpinnerAdapter()
        //Fill the spinner_end adapter
        spinner_end.adapter = ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,times)
        spinner_end.onItemSelectedListener = SpinnerAdapter()
        //Start the AM parts
        spinner_amstart.adapter = ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,meridian)
        spinner_amstart.onItemSelectedListener = AMstartAdapter(startAMList,startPMList)
        spinner_amstart.onItemSelectedListener {
            println("NO FAILURE PLEASE")
        }

        //am end.
        spinner_amend.adapter =  ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,meridian)
        spinner_amend.onItemSelectedListener = SpinnerAdapter()
        //On click listener for SAVE BUTTON, Triggers for valid entries in the boxes.

        btn_save.setOnClickListener(){
            //val remainingTimes
            //Create the date objects from the selected fields...
            val curr = LocalDateTime.now()
            val zeroDate = DateTimeFormatter.ofPattern(datePat)
            val zeroFormat = curr.format(zeroDate)
            val dateFormatter = DateTimeFormatter.ofPattern(pattern1)
            val formattedDate = curr.format(dateFormatter)
            val curDate = SimpleDateFormat(pattern1).parse(formattedDate) //current date object.
            println(curDate.toString())
            val startString = spinner_start.selectedItem.toString() +" "+ spinner_amstart.selectedItem.toString() +" "+ zeroFormat
            val endString = spinner_end.selectedItem.toString() +" "+ spinner_amend.selectedItem.toString() +" " +zeroFormat
            println(endString)
            val selectPattern = spinnerPat + datePat
            val f = DateTimeFormatter.ofPattern(selectPattern)
            val tDate = SimpleDateFormat(selectPattern).parse(endString)
            println(tDate.toString())
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MakeBooking> {
        override fun createFromParcel(parcel: Parcel): MakeBooking {
            return MakeBooking(parcel)
        }

        override fun newArray(size: Int): Array<MakeBooking?> {
            return arrayOfNulls(size)
        }
    }

}

private fun Spinner.onItemSelectedListener(function: () -> Unit) {

}

private operator fun AdapterView.OnItemClickListener.invoke(function: () -> Unit) {

}
