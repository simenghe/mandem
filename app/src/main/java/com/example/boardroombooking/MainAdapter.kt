package com.example.boardroombooking

import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v7.widget.RecyclerView
import android.system.Os.remove
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.bookingitem.view.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.*
import kotlin.collections.ArrayList


class MainAdapter(val dataList: ArrayList<Data>) : RecyclerView.Adapter<MainAdapter.CustomViewHolder>() {

    //trying a get request....
    override fun getItemCount(): Int { //probably determined by a count through the database...
        return dataList.count()
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): CustomViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        val cellForRow = layoutInflater.inflate(R.layout.bookingitem, p0, false)
        return CustomViewHolder(cellForRow)
    }

    override fun onBindViewHolder(p0: CustomViewHolder, pos: Int) {
        //example date: 5/30/2019, 16:00:00
        val booking = dataList[pos]
        val pattern = "MM/dd/yyyy, HH:mm:ss"
        p0.itemView.txt_title?.text = "Title : " + booking.title
        p0.itemView.txt_NAME?.text = "Name : " + booking.userName
        //should format the date using some java library.
        val startTime = SimpleDateFormat(pattern).parse(booking.startingTime)
        val endTime = SimpleDateFormat(pattern).parse(booking.endingTime)
        println(startTime < endTime) // this should be outputting true
        p0.itemView.txt_duration?.text = "Duration : " + booking.startingTime?.substring(10, 16) + " - " +
                booking.endingTime?.substring(10, 16)


    }

    fun getOccupied(curDate: Date): Data? {
        dataList.forEach {
            val startTime = SimpleDateFormat(pattern1).parse(it.startingTime)
            val endTime = SimpleDateFormat(pattern1).parse(it.endingTime)
            val Occupied = (startTime <= curDate && endTime >= curDate)
            if (Occupied) {
                return it
            }
        }
        return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun removeOptions(): ArrayList<Date> {
        val curr = LocalDateTime.now()
        val dateFormatter = DateTimeFormatter.ofPattern(pattern1)
        val zeroFormatter = DateTimeFormatter.ofPattern(datePat)
        val formattedDate = curr.format(dateFormatter)
        val zeroDate = curr.format(zeroFormatter)
        val occupiedDates = ArrayList<Date>()
        println("THIS IS THE CURRENT DATE : " + formattedDate)
        val curDate = SimpleDateFormat(pattern1).parse(formattedDate)
        val occupied = getOccupied(curDate)
        val todayZero = SimpleDateFormat(datePat).parse(zeroDate)
        if (occupied != null) {
            val occStart = SimpleDateFormat(pattern1).parse(occupied?.startingTime.toString())
            val occEnd = SimpleDateFormat(pattern1).parse(occupied?.endingTime.toString())
            //println("OCC "+occStart.toString())
            val c = Calendar.getInstance()
            c.time = todayZero
            for (i in 0..48) { //range from 00:00 -> 24:00 create the list.
                println(c.time.toString())
                val prev = c.time
                if (prev < occEnd) {
                } else {
                    occupiedDates.add(c.time)
                }
                c.add(Calendar.MINUTE, 30)
            }
            //now filter the list of available times using some nasty algorithm
            occupiedDates.forEach {
                println("NEW LIST: " + it.toString())
            }
        } else {
            val c = Calendar.getInstance()
            c.time = todayZero
            for (i in 0..48) { //range from 00:00 -> 24:00 create the list.
                println(c.time.toString())
                val prev = c.time
                occupiedDates.add(c.time)
                c.add(Calendar.MINUTE, 30)
            }
        }
        return occupiedDates
    }

    class CustomViewHolder(v: View) : RecyclerView.ViewHolder(v) {

    }
}