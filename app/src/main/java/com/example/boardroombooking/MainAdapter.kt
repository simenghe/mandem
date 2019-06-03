package com.example.boardroombooking

import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import okhttp3.OkHttpClient
import android.R.string
import android.util.Log
import okhttp3.Request
import java.io.IOException


class MainAdapter: RecyclerView.Adapter<CustomViewHolder>(){
    //trying a get request....
    var resper = ""
    val url = "https://ratnuback.appspot.com/"
    override fun getItemCount(): Int { //probably determined by a count through the database...
        return 6
    }
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): CustomViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        val cellForRow = layoutInflater.inflate(R.layout.bookingitem,p0,false)

        return CustomViewHolder(cellForRow)
    }

    override fun onBindViewHolder(p0: CustomViewHolder, p1: Int) {

    }
}

class CustomViewHolder(v: View) : RecyclerView.ViewHolder(v){

}