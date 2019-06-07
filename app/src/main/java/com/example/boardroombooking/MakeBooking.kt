package com.example.boardroombooking

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter

import kotlinx.android.synthetic.main.activity_make_booking.*
import kotlinx.android.synthetic.main.content_make_booking.*
import java.io.Serializable

class MakeBooking() : AppCompatActivity(),Serializable,Parcelable{

    constructor(parcel: Parcel) : this() {
    }

    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        val dataList = intent.getParcelableArrayListExtra<Data>("datalist")
        println("SIZE: " +dataList.count())
        dataList.forEach {
            println(it.startingTime)
        }
        requestedOrientation = (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_make_booking)
        val meridian = listOf("AM","PM")
        val times = listOf("10:00","11:00","12:00")
        spinner_start.adapter = ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,times)
        spinner_start.onItemSelectedListener = SpinnerAdapter()
        //Fill the spinner_end adapter
        spinner_end.adapter = ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,times)
        spinner_end.onItemSelectedListener = SpinnerAdapter()
        //Start the AM parts
        spinner_amstart.adapter = ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,meridian)
        spinner_amstart.onItemSelectedListener = SpinnerAdapter()
        //am end.
        spinner_amend.adapter =  ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,meridian)
        spinner_amend.onItemSelectedListener = SpinnerAdapter()
        //On click listener for SAVE BUTTON, Triggers for valid entries in the boxes.
        btn_cancel.setOnClickListener(){
            val intent = Intent(this,MainActivity::class.java).apply {
                putExtra("Ratnu","Main ting")
            }
            startActivity(intent)
        }
        btn_save.setOnClickListener(){
            //val remainingTimes
            //Create the date objects from the selected fields...
            val startString = spinner_start.selectedItem.toString() + spinner_amstart.selectedItem.toString()
            val endString:String = spinner_end.selectedItem.toString() + spinner_amstart.selectedItem.toString()
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
