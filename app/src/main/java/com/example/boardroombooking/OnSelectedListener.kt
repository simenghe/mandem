package com.example.boardroombooking

import android.app.Activity
import android.os.Parcel
import android.os.Parcelable
import android.view.View
import android.widget.AdapterView

class OnSelectedListener() : Activity(), AdapterView.OnItemSelectedListener {
    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        println("This is the pos $pos")
        println("The size of start list is ${endList.count()}")
    }
    override fun onNothingSelected(parent: AdapterView<*>?) {
        print("None selected")
    }

}