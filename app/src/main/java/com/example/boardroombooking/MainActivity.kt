package com.example.boardroombooking

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.os.*
import android.support.v7.app.AppCompatActivity
import android.support.annotation.RequiresApi
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.Toast
import com.android.volley.toolbox.JsonObjectRequest
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import org.json.JSONException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


const val datePat = "dd MMMM yyyy"
const val timePat = "HH:mm"
const val spinnerPat = "hh:mm a"
const val username = "ratnu"
const val password = "mandem"
var location = "Alcorn"
const val pattern1 = "MM/dd/yyyy, HH:mm:ss"
class MainActivity : AppCompatActivity() {
    private fun getCurrentTimeUsingDate(pattern:String = datePat):String {
        val date = Date()
        val strDateFormat = "hh:mm:ss a"
        val dateFormat = SimpleDateFormat(pattern)
        val formattedDate = dateFormat.format(date)
        return formattedDate
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recycle_main.layoutManager= LinearLayoutManager(this)
        getCurrentTimeUsingDate()

        txt_curDate.setText(getCurrentTimeUsingDate())




        fetchJson()
//        val curr =  LocalDateTime.now()
//        val dateFormatter = DateTimeFormatter.ofPattern(datePat)
//        val timeFormat = DateTimeFormatter.ofPattern(timePat)
//        val formattedDate = curr.format(dateFormatter)
//        println("FORMATTED DATE" + formattedDate)
//        val formattedTime = curr.format(timeFormat)
        //does not update unless app is closed....
        //txt_curDate.setText(formattedDate)
        recycle_main.addItemDecoration(VerticalSpaceItemDecoration(40))
        //Onclick Action For the fab..
    }
    fun buttonClicked(view: View,dataList: ArrayList<Data>){
        val intent = Intent(this,MakeBooking::class.java).apply {
            putParcelableArrayListExtra("datalist",dataList)
        }
        startActivity(intent)
    }
    fun strToDate(date:String?,pattern:String? = pattern1): Date { //Changes the string to a date object.
        return SimpleDateFormat(pattern).parse(date)

    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun sortByTime(dataList:ArrayList<Data>):ArrayList<Data>{
        //also remove entries that are not in the day...
        val curr =  LocalDateTime.now()
        val dateFormatter = DateTimeFormatter.ofPattern(datePat)
        val curDate = curr.format(dateFormatter)
        val laterDate = curr.plusDays(1).format(dateFormatter)
        println("\n THE DATE IS "+curDate)
        println("\n THE LATE IS "+laterDate)
        val pattern2 = "dd MMMMM yyyy"
        //The dates that are shown in the app must be between these two.
        val todayStart = strToDate(curDate,pattern2)
        val todayEnd = strToDate(laterDate,pattern2)
        println(todayStart)
        println(todayEnd)
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
    fun fetchJson(){ //function to fetch json from the web.
        var keys:Iterator<String>
        println("FETCHING JSON")
        //val url = "https://api.myjson.com/bins/9wzmf"
        var url ="https://ratnuback.appspot.com/getBooking/Shaftesbury"
        val credential = Credentials.basic(username, password);
        val request = Request.Builder().url(url).header("Authorization",credential).build()
        val client = OkHttpClient()
        var dataList = ArrayList<Data>()
        client.newCall(request).enqueue(object:Callback{
            override fun onFailure(call: Call, e: IOException) {
                println("fail")
            }
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                try {
                    val jsonObject = JSONObject(body)
                    val gson = GsonBuilder().create()
                    jsonObject.keys().forEach {
                       // println("key: $it")
                        //println(jsonObject[it].toString())
                        dataList.add(gson.fromJson(jsonObject[it].toString(), Data::class.java))
                    }
                } catch (err: JSONException) {
                    Log.d("Error", err.toString())
                }

                dataList.forEach {
                    println("title : ${it.title}")
                }
                dataList = sortByTime(dataList)
                val s= MainAdapter(dataList).removeOptions()
                runOnUiThread{
                    println("DATALIST SIZE : " + dataList.count())
                    recycle_main.adapter =  MainAdapter(dataList) //Run the the recycler view code.
                    fab.setOnClickListener { view ->
                        buttonClicked(view,dataList)
                    }
                }
            }
        })
    }
}



class Data (
    @SerializedName("a")
    val a:Content? = null,
    @SerializedName("endingTime")
    val endingTime:String? = null,
    @SerializedName("floor")
    val floor:Int? = null,
    @SerializedName("meetingRoom")
    val meetingRoom:String?= null,
    @SerializedName("meetingState")
    val meetingState:String? = null,
    @SerializedName("startingTime")
    val startingTime:String? = null,
    @SerializedName("title")
    val title:String? = null,
    @SerializedName("userName")
    val userName:String? = null
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(Content::class.java.classLoader),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(a, flags)
        parcel.writeString(endingTime)
        parcel.writeValue(floor)
        parcel.writeString(meetingRoom)
        parcel.writeString(meetingState)
        parcel.writeString(startingTime)
        parcel.writeString(title)
        parcel.writeString(userName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Data> {
        override fun createFromParcel(parcel: Parcel): Data {
            return Data(parcel)
        }

        override fun newArray(size: Int): Array<Data?> {
            return arrayOfNulls(size)
        }
    }
}


class Content(
    @SerializedName("description")
    val description:String? = null,
    @SerializedName("id")
    val id:String? = null):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(description)
        parcel.writeString(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Content> {
        override fun createFromParcel(parcel: Parcel): Content {
            return Content(parcel)
        }

        override fun newArray(size: Int): Array<Content?> {
            return arrayOfNulls(size)
        }
    }
}

class Desc(val desc:String,val time:String):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(desc)
        parcel.writeString(time)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Desc> {
        override fun createFromParcel(parcel: Parcel): Desc {
            return Desc(parcel)
        }

        override fun newArray(size: Int): Array<Desc?> {
            return arrayOfNulls(size)
        }
    }
}

