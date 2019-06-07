package com.example.boardroombooking

import android.R
import android.app.Activity
import android.os.Parcel
import android.os.Parcelable
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.content_make_booking.*

class AMstartAdapter(val startAMList:ArrayList<String>,val startPMList:ArrayList<String>) : Activity(), AdapterView.OnItemSelectedListener {
    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        //id 0 -> AM
        //id 1 -> PM
        println("AM ID: $id")
        meridianSelected(id)
    }
    override fun onNothingSelected(parent: AdapterView<*>?) {
        print("None selected")

    }
    fun meridianSelected(id: Long){
        val AM:Long = 0
        val PM:Long = 1
        runOnUiThread{
            if(id == AM){
                //spinner_start.adapter = ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,startAMList)
            }else if(id == PM){
                //spinner_start.adapter = ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,startPMList)
            }
        }

    }

}