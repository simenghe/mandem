package com.example.boardroombooking

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.RequiresApi
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.app.AppCompatActivity;
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response

import kotlinx.android.synthetic.main.activity_make_booking.*
import kotlinx.android.synthetic.main.content_make_booking.*
import org.json.JSONObject
import java.io.Serializable
import java.lang.Error
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneId.*
import java.time.format.DateTimeFormatter
import java.util.*


const val credentials = "ratnu:mandem"
const val dataPattern = "M/d/yyyy, HH:mm:ss"
const val selectPattern = spinnerPat +" "+ datePat
@Suppress("DEPRECATION")
class MakeBooking() : AppCompatActivity(),Serializable,Parcelable{
    @RequiresApi(Build.VERSION_CODES.O)
    fun formatToDataPattern(date:String): String? {
        val startToLDC = convertToLocalDateTime(SimpleDateFormat(selectPattern).parse(date))
        val dataFormat = DateTimeFormatter.ofPattern(dataPattern)
        val formattedStart = startToLDC.format(dataFormat)
        return formattedStart
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun convertToLocalDateTime(dateToConvert: Date): LocalDateTime {
        return dateToConvert.toInstant()
            .atZone(systemDefault())
            .toLocalDateTime()
    }
    constructor(parcel: Parcel) : this() {
    }
    fun changeActivity(){
        val intent = Intent(this,MainActivity::class.java.apply {  })
        startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        val dataList = intent.getParcelableArrayListExtra<Data>("datalist")
        dataList.forEach {
            println(it.startingTime)
        }
        val startTimeList = MainAdapter(dataList).removeOptions()
        val length = startTimeList.count()
        val startList = ArrayList<String>(length)
        for ((i, value) in startTimeList.withIndex()) {
            val ldt = convertToLocalDateTime(startTimeList[i])
            val custSpinner = "h:mm a"
            val ldtFormatter = DateTimeFormatter.ofPattern(custSpinner)
            val formattedSpinner = ldt.format(ldtFormatter)
            startList.add(formattedSpinner)
            println(formattedSpinner)
        }
        requestedOrientation = (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_make_booking)
        val meridian = listOf("AM","PM")
        val times = listOf("10:00","11:00","12:00")
        spinner_start.adapter = ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,startList)
        spinner_start.onItemSelectedListener = SpinnerAdapter()
        //Fill the spinner_end adapter
        spinner_end.adapter = ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,startList)
        spinner_end.onItemSelectedListener = SpinnerAdapter()
        //Start the AM parts
        spinner_amstart.adapter = ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,meridian)
        spinner_amstart.onItemSelectedListener = SpinnerAdapter()

        //am end.
        spinner_amend.adapter =  ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,meridian)
        spinner_amend.onItemSelectedListener = SpinnerAdapter()
        //On click listener for SAVE BUTTON, Triggers for valid entries in the boxes.
        btn_save.setOnClickListener(){
            //val remainingTimes
            //Create the date objects from the selected fields...
            val curr = LocalDateTime.now()
            val zeroDate = DateTimeFormatter.ofPattern(datePat)
            val zeroFormat = curr.format(zeroDate)
            val dateFormatter = DateTimeFormatter.ofPattern(pattern1)
            val formattedDate = curr.format(dateFormatter)
            val curDate = SimpleDateFormat(pattern1).parse(formattedDate) //current date object.
            println(curDate.toString())
            val startString = spinner_start.selectedItem.toString()+" "+ zeroFormat
            val endString = spinner_end.selectedItem.toString() +" " +zeroFormat
            println("The end string"+startString)
            val selectPattern = spinnerPat +" "+ datePat
            Log.d("Pattern1",selectPattern)

            val formattedStart = formatToDataPattern(startString)
            val formattedEnd = formatToDataPattern(endString)
            Log.d("timers",formattedStart.toString())
            //Log.d("Pattern2",formattedStart)
            val tDate = SimpleDateFormat(selectPattern).parse(endString)




            val url = "https://ratnuback.appspot.com/addBooking/Alcorn"
            val params = HashMap<String,String>()
            val placeHolderA = HashMap<String,String>()
            params["ratnu"] = "Ban"
            //dependent on location.
            params["floor"] = "2"
            params["startingTime"] = formattedStart.toString()
            params["endingTime"] = formattedEnd.toString()
            params["title"] = edit_title.text.toString()
            params["userName"] = edit_name.text.toString()
            val jsonObject = JSONObject(params)
            val request = CustomJsonObjectRequestBasicAuth(
                Request.Method.POST,url,jsonObject,
                Response.Listener { response ->
                    val s = response.toString()
                    // Process the json
                    try {
                         println("Response: $s")
                    }catch (e:Exception){
                        println("Exception: $e" + "$response")
                    }

                }, Response.ErrorListener{
                    // Error in request
                    println("Volley error: $it")
                }, credentials)
            request.retryPolicy = DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                // 0 means no retry
                0, // DefaultRetryPolicy.DEFAULT_MAX_RETRIES = 2
                1f // DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
            VolleySingleton.getInstance(this).addToRequestQueue(request)

            //In the end, just switch activity back to the old one and it should update it.\
            changeActivity()
        }
        btn_cancel.setOnClickListener{
            changeActivity()
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MakeBooking> {
        override fun createFromParcel(parcel: Parcel): MakeBooking {
            return MakeBooking(parcel)
        }

        override fun newArray(size: Int): Array<MakeBooking?> {
            return arrayOfNulls(size)
        }
    }

}

private fun Spinner.onItemSelectedListener(function: () -> Unit) {

}

private operator fun AdapterView.OnItemClickListener.invoke(function: () -> Unit) {

}
