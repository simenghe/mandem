package com.example.boardroombooking

import android.app.Activity
import android.view.View
import android.widget.AdapterView

class SpinnerAdapter : Activity(), AdapterView.OnItemSelectedListener {
    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {

        println(pos)
        println(id)
    }
    override fun onNothingSelected(parent: AdapterView<*>?) {
        print("None selected")
    }
}