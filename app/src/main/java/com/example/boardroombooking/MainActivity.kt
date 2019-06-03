package com.example.boardroombooking

import android.content.pm.ActivityInfo
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.support.annotation.RequiresApi
import android.support.design.widget.Snackbar
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
const val datePat = "dd MMMM yyyy"
const val timePat = "HH:mm"
const val username = "ratnu"
const val password = "mandem"
var location = "Alcorn"
                                                                                                
class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recycle_main.layoutManager= LinearLayoutManager(this)
        recycle_main.adapter =  MainAdapter() //Run the the recycler view code.
        val curr =  LocalDateTime.now()
        val dateFormatter = DateTimeFormatter.ofPattern(datePat)
        val timeFormat = DateTimeFormatter.ofPattern(timePat)
        val formattedDate = curr.format(dateFormatter)
        val formattedTime = curr.format(timeFormat)
        //does not update unless app is closed....
        txt_curDate.setText(formattedDate)
        recycle_main.addItemDecoration(VerticalSpaceItemDecoration(40))
        //Onclick Action For the fab..
        fab.setOnClickListener { view ->
            Toast.makeText(this,"Creating a meeting!",Toast.LENGTH_SHORT).show()
            fetchJson()
        }


    }
    fun fetchJson(){ //function to fetch json from the web.
        println("FETCHING JSON")
        val url = "https://ratnuback.appspot.com/getData"
        val credential = Credentials.basic(username, password);
        val request = Request.Builder().url(url).header("Authorization",credential).build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object:Callback{
            override fun onFailure(call: Call, e: IOException) {
                println("epic fail")
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response?.body?.string()
                println(body)
                val gson = GsonBuilder().create()
                txt_instructions.setText(body)
            }
        })

    }
}
