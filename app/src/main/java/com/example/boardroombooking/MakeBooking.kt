package com.example.boardroombooking

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter

import kotlinx.android.synthetic.main.activity_make_booking.*
import kotlinx.android.synthetic.main.content_make_booking.*

class MakeBooking : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
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


    }

}