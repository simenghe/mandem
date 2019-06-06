package com.example.boardroombooking

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.bookingitem.view.*
import java.text.SimpleDateFormat


class MainAdapter(val dataList:ArrayList<Data>): RecyclerView.Adapter<CustomViewHolder>(){

    //trying a get request....
    override fun getItemCount(): Int { //probably determined by a count through the database...
        return dataList.count()
    }
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): CustomViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        val cellForRow = layoutInflater.inflate(R.layout.bookingitem,p0,false)
        return CustomViewHolder(cellForRow)
    }

    override fun onBindViewHolder(p0: CustomViewHolder, pos: Int) {
        //example date: 5/30/2019, 16:00:00
        val booking = dataList[pos]
        val pattern = "MM/dd/yyyy, HH:mm:ss"
        p0.itemView.txt_title?.text = "Title : "+ booking.title
        p0.itemView.txt_NAME?.text = "Name : " + booking.userName
        //should format the date using some java library.
        val startTime = SimpleDateFormat(pattern).parse(booking.startingTime)
        val endTime = SimpleDateFormat(pattern).parse(booking.endingTime)
        println(startTime<endTime) // this should be outputting true
        p0.itemView.txt_duration?.text ="Duration : " + booking.startingTime?.substring(10,16) + " - "+
                booking.endingTime?.substring(10,16)

    }
}

class CustomViewHolder(v: View) : RecyclerView.ViewHolder(v){

}