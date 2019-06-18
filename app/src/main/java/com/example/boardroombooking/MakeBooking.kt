package com.example.boardroombooking

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.RequiresApi
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.app.AppCompatActivity;
import android.telecom.Call
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.SpinnerAdapter
import android.widget.Toast
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import kotlinx.android.synthetic.main.activity_bar.*
import kotlinx.android.synthetic.main.activity_main.*

import kotlinx.android.synthetic.main.activity_make_booking.*
import kotlinx.android.synthetic.main.activity_new_booking.*
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
import java.util.concurrent.TimeUnit


const val credentials = "ratnu:mandem"
const val dataPattern = "M/d/yyyy, HH:mm:ss"
const val selectPattern = spinnerPat +" "+ datePat
const val custSpinnerPat = "h:mm a"
var url = "https://ratnuback.appspot.com/addBooking/Shaftesbury"
var startList = ArrayList<String>()
var endList = ArrayList<String>()
@Suppress("DEPRECATION")
class MakeBooking() : AppCompatActivity() {
    //animations and activities
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.enter_from_left,R.anim.exit_to_right)
    }
    private fun changeActivity(data:Data? = null){
        data?.let {
            Intent(this,MainActivity::class.java).apply {
                putExtra(MainActivity.EXTRA_NEW_MEETING_DATA, data)
                setResult(Activity.RESULT_OK, this)
            }
        }
        finish()
        overridePendingTransition(R.anim.enter_from_left,R.anim.exit_to_right)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(R.anim.slide_in,R.anim.slide_out)
        val dataList = intent.getParcelableArrayListExtra<Data>("datalist")
        dataList.forEach {
            println(it.startingTime)
        }
        val startTimeList = MainAdapter(dataList).removeOptions()
        val length = startTimeList.count()
        startList.clear()
        endList.clear()
        for ((i, value) in startTimeList.withIndex()) {
                println("count $i")
            val formattedSpinner = DateFunctions().formatTimeUsingDate(startTimeList[i],custSpinnerPat)
            startList.add(formattedSpinner)
            if(i==0){
            }else{
                endList.add(formattedSpinner)
            }
            println(formattedSpinner)
        }
        Log.d("Comparison","Compare the string ${startList.count()} vs ${endList.count()}")
        //should be startList should always be endlist+1 size.
        requestedOrientation = (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_booking)
        window.statusBarColor = ContextCompat.getColor(this, R.color.blackdu)
        txt_room2.text = Html.fromHtml("Room: <b>$location</b>")
        val endAdapter = ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,endList)
        spinner_end.adapter = endAdapter
        spinner_end.onItemSelectedListener = object:AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
               //nothing needs to happen
            }
        }
        val startAdapter = ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,startList)
        spinner_start.adapter =  startAdapter
        spinner_start.onItemSelectedListener = object:AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                println("This is the pos $pos and this is id : $id") //identical
                println("The size of end list is ${endList.count()}")
                //Remove the entries alligned with the endlist. or recreate the list
                val startLength = startList.count()
                endList.clear()
                for (i in pos+1..startLength-1){
                    endList.add(startList[i])
                }
                startAdapter.notifyDataSetChanged()
                endAdapter.notifyDataSetChanged() //reallign the views.
                spinner_end.setSelection(0)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //nothing needs to happen
            }
        }

        //On click listener for SAVE BUTTON, Triggers for valid entries in the boxes.
        btn_save.setOnClickListener(){
            println("IS THIS CORRECT? ${edit_name.text}")
            var isValid = edit_name.text.isNotBlank() && edit_title.text.isNotBlank()
            println("IS IT VALID? : $isValid")
            val dateFormat = SimpleDateFormat(datePat)
            val todayStart = dateFormat.parse(DateFunctions().getCurrentTimeUsingDate())
            val todayEnd = Date(todayStart.time + TimeUnit.DAYS.toMillis(1))
            val zeroEnd = DateFunctions().formatTimeUsingDate(todayEnd, datePat)
            println("ZEROEND: $zeroEnd" )
            val zeroFormat = DateFunctions().getCurrentTimeUsingDate(datePat)
            var startString = spinner_start.selectedItem.toString()+" "+ zeroFormat
            var endString = spinner_end.selectedItem.toString() +" " +zeroFormat
            //special case for 12am.... 12:00 AM
            val midNight = "12:00 AM"
            if(spinner_start.selectedItem.toString() == midNight){
                println("MIDNIGHT")
                startString = spinner_start.selectedItem.toString()+" "+ zeroEnd
            }else if(spinner_end.selectedItem.toString()==midNight){
                println("MIDNIGHT")
                endString = spinner_end.selectedItem.toString() +" " +zeroEnd
            }
            println("THE STRING: $startString + $endString")
            println("The end string"+startString)
            val selectPattern = spinnerPat +" "+ datePat
            Log.d("Pattern1",selectPattern)
            val startDate = SimpleDateFormat(selectPattern).parse(startString)
            val endDate = SimpleDateFormat(selectPattern).parse(endString)
            val formattedStart = DateFunctions().formatTimeUsingDate(startDate, dataPattern)
            val formattedEnd = DateFunctions().formatTimeUsingDate(endDate, dataPattern)
            Log.d("timers",formattedStart)
            //make the dates from string
            var isover = false
            var overStart = Date()
            var overEnd = Date()
            dataList.forEach {
                if(!DateFunctions().isFine(it,startDate,endDate)){
                    println("OVERLAPPING ENTRY.")
                    isover = true
                }else{
                    println("DIDNT WORK")
                }
            }
            if(!isValid){
                Toast.makeText(this,"Please fill out the name and title!",Toast.LENGTH_SHORT).show()
            }else if(isover) {
                Toast.makeText(this,"Overlapping entry!",Toast.LENGTH_SHORT).show()
            } else
            {
                val params = HashMap<String,String>()
                //dependent on location.
                params["floor"] = "2"
                params["startingTime"] = formattedStart.toString()
                params["endingTime"] = formattedEnd.toString()
                params["title"] = edit_title.text.toString()
                params["userName"] = edit_name.text.toString()
                params["description"] = "Booked from the Android APP!"
                val jsonObject = JSONObject(params)
                val request = CustomJsonObjectRequestBasicAuth(
                    Request.Method.POST,url,jsonObject,
                    Response.Listener { response ->
                        val dataObject = Data()
                        dataObject.title = params["title"]
                        dataObject.endingTime = formattedEnd.toString()
                        dataObject.startingTime = formattedStart.toString()
                        dataObject.userName = params["userName"]
                        changeActivity(dataObject)
                    }, Response.ErrorListener{
                        // Error in request
                        println("Volley error: $it")
                        val dataObject = Data()
                        dataObject.title = params["title"]
                        dataObject.endingTime = formattedEnd.toString()
                        dataObject.startingTime = formattedStart.toString()
                        dataObject.userName = params["userName"]
                        changeActivity(dataObject)
                        Toast.makeText(this, "Creating the booking...", Toast.LENGTH_SHORT).show()
                    }, credentials)
                request.retryPolicy = DefaultRetryPolicy(
                    DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                    // 0 means no retry
                    0, // DefaultRetryPolicy.DEFAULT_MAX_RETRIES = 2
                    1f // DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
                VolleySingleton.getInstance(this).addToRequestQueue(request)
            }
        }
        btn_cancel.setOnClickListener{//Asks user with dialog if they want to cancel their booking.
                val builder = AlertDialog.Builder(this@MakeBooking)
                builder.setTitle("Exit Confirmation")
                builder.setMessage("Are you sure you want to exit?")
                builder.setPositiveButton("Yes"){dialog, which ->
                    changeActivity()
                }
                builder.setNegativeButton("No"){dialog, which ->
                    println("NOTHING WILL OCCUR....")
                }
                val dialog: AlertDialog = builder.create()
                dialog.show()
                println("You have things filled dialog should pop up.")
        }
    }
}
private fun Spinner.onItemSelectedListener(function: () -> Unit) {

}

private operator fun AdapterView.OnItemClickListener.invoke(function: () -> Unit) {
    Log.d("ONCLICKED","ACTION")
}
