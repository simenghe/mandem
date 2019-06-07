package com.example.boardroombooking

import android.app.Activity
import android.os.Parcel
import android.os.Parcelable
import android.view.View
import android.widget.AdapterView
import kotlinx.android.synthetic.main.content_make_booking.*

class SpinnerAdapter() : Activity(), AdapterView.OnItemSelectedListener,Parcelable{
    constructor(parcel: Parcel) : this() {

    }
    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        //id 0 -> AM
        //id 1 -> PM
        println("thats a playa: $id")

    }
    override fun onNothingSelected(parent: AdapterView<*>?) {
        print("None selected")
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SpinnerAdapter> {
        override fun createFromParcel(parcel: Parcel): SpinnerAdapter {
            return SpinnerAdapter(parcel)
        }

        override fun newArray(size: Int): Array<SpinnerAdapter?> {
            return arrayOfNulls(size)
        }
    }
}