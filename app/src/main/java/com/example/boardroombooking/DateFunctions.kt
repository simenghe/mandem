package com.example.boardroombooking

import android.os.Build
import android.support.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class DateFunctions{
    //Creates a string of the current time/date of the desired pattern.
    public fun getCurrentTimeUsingDate(pattern:String = datePat):String {
        val date = Date()
        val strDateFormat = "hh:mm:ss a"
        val dateFormat = SimpleDateFormat(pattern)
        val formattedDate = dateFormat.format(date)
        return formattedDate
    }
    fun formatTimeUsingDate(date:Date,pattern:String = datePat):String {
        val strDateFormat = "hh:mm:ss a"
        val dateFormat = SimpleDateFormat(pattern)
        val formattedDate = dateFormat.format(date)
        return formattedDate
    }
    fun strToDate(date:String?,pattern:String? = pattern1): Date { //Changes the string to a date object.
        return SimpleDateFormat(pattern).parse(date)

    }
    fun isToday(curDate:Date,Booking:Data):Boolean{
        val pattern2 = "dd MMMMM yyyy"
        val dateFormat = SimpleDateFormat(pattern2)
        val todayStart = dateFormat.parse(getCurrentTimeUsingDate())
        val todayEnd = Date(todayStart.time + TimeUnit.DAYS.toMillis(1))
        println("THE DATE IS :"+todayStart)
        println("THE LATE IS "+todayEnd)
        val startTime = strToDate(Booking.startingTime)
        if(startTime>=todayStart&&startTime<=todayEnd){
            println("Within TODAY")
            return true
        }
        return false

    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun sortByTime(dataList:ArrayList<Data>):ArrayList<Data>{
        //also remove entries that are not in the day...
        val pattern2 = "dd MMMMM yyyy"
        val dateFormat = SimpleDateFormat(pattern2)
        val todayStart = dateFormat.parse(getCurrentTimeUsingDate())
        val todayEnd = Date(todayStart.time + TimeUnit.DAYS.toMillis(1))
        println("THE DATE IS :"+todayStart)
        println("THE LATE IS "+todayEnd)
        val newData = ArrayList<Data>()
        dataList.sortBy { strToDate(it.startingTime) }
        dataList.forEach {
            println(it.startingTime)
            val startTime = strToDate(it.startingTime)
            if(startTime>=todayStart&&startTime<=todayEnd){
                println("ALLOWED")
                newData.add(it)
            }
        }
        newData.forEach {
            println("AYY")
            println(it.startingTime)
        }
        return newData
    }
    fun sortTime(dataList: ArrayList<Data>){
        dataList.sortBy { strToDate(it.startingTime) }
    }
    fun getOccupied(curDate: Date = Calendar.getInstance().time): Data? {
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
    fun isOccupied(bookDate:Data,curDate: Date = Calendar.getInstance().time): Boolean {
        val startTime = SimpleDateFormat(pattern1).parse(bookDate.startingTime)
        val endTime = SimpleDateFormat(pattern1).parse(bookDate.endingTime)
        if (startTime <= curDate && endTime >= curDate) {
            return true
        }
        return false
    }
}