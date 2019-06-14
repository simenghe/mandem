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
@Suppress("DEPRECATION")
class MakeBooking() : AppCompatActivity() {
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
        Log.d("cringe",length.toString())
        val startList = ArrayList<String>(length)
        for ((i, value) in startTimeList.withIndex()) {
            val formattedSpinner = DateFunctions().formatTimeUsingDate(startTimeList[i],custSpinnerPat)
            startList.add(formattedSpinner)
            println(formattedSpinner)
        }
        requestedOrientation = (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_booking)
        window.statusBarColor = ContextCompat.getColor(this, R.color.blackdu)
        txt_room2.text = Html.fromHtml("Room: <b>$location</b>")
        spinner_start.adapter = ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,startList)
        spinner_start.onItemSelectedListener = SpinnerAdapter()
        //Fill the spinner_end adapter
        spinner_end.adapter = ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,startList)
        spinner_end.onItemSelectedListener = SpinnerAdapter()
        //On click listener for SAVE BUTTON, Triggers for valid entries in the boxes.
        btn_save.setOnClickListener(){
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

            val url = "https://ratnuback.appspot.com/addBooking/Shaftesbury"
            val params = HashMap<String,String>()
            val placeHolderA = HashMap<String,String>()
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
                    Toast.makeText(this, "Volleying", Toast.LENGTH_SHORT).show()
                }, credentials)
            request.retryPolicy = DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                // 0 means no retry
                0, // DefaultRetryPolicy.DEFAULT_MAX_RETRIES = 2
                1f // DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
            VolleySingleton.getInstance(this).addToRequestQueue(request)
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
