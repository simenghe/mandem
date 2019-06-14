package com.example.boardroombooking

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.*
import android.support.v7.app.AppCompatActivity
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.text.Html
import android.util.Log
import android.util.TypedValue
import android.view.View
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import kotlinx.android.synthetic.main.activity_bar.*
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import org.json.JSONException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


const val datePat = "dd MMMM yyyy"
const val timePat = "HH:mm"
const val spinnerPat = "hh:mm a"
const val username = "ratnu"
const val password = "mandem"
const val delay:Long = 10000
var location = "Shaftesbury"
const val pattern1 = "MM/dd/yyyy, HH:mm:ss"
var counter = 0
var dataList = ArrayList<Data>()
val adapter = MainAdapter(dataList)
var Occupation = false

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(), Runnable {
    override fun run() {}
    companion object {
        const val REQUEST_CODE_SAVE_MEETING = 1
        const val EXTRA_NEW_MEETING_DATA = "new_meeting_data"
    }
    public fun getCurrentTimeUsingDate(pattern:String = datePat):String {
        val date = Date()
        val strDateFormat = "hh:mm:ss a"
        val dateFormat = SimpleDateFormat(pattern)
        val formattedDate = dateFormat.format(date)
        return formattedDate
    }
    fun getOccupied(dataList:ArrayList<Data>, curDate: Date): Data? {
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
    fun isOccupied(curDate: Date,bookDate:Data): Boolean {
        val startTime = SimpleDateFormat(pattern1).parse(bookDate.startingTime)
        val endTime = SimpleDateFormat(pattern1).parse(bookDate.endingTime)
        println("Occupied start: $startTime and Occ End = $endTime")
        if (startTime <= curDate && endTime >= curDate) {
            println("ITS TRUE")
            return true
        }
        return false
    }
    fun isEnd(bookDate: Data,curDate: Date = Calendar.getInstance().time): Boolean{
        val endDate = strToDate(bookDate.endingTime)
        if(curDate>endDate){
            return true
        }
        return false
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bar)
        window.statusBarColor = ContextCompat.getColor(this, R.color.blackdu)
        recycle_main.layoutManager = LinearLayoutManager(this)
        //fetchJson()
        recycle_main.addItemDecoration(VerticalSpaceItemDecoration(20))
        recycle_main.adapter = adapter
        recycle_main.setHasFixedSize(true)
        txt_room.text = Html.fromHtml("Room: <b>$location</b>")
        startRepeating(recycle_main)
        swiper.setOnRefreshListener {
            refreshList()
            swiper.isRefreshing = false
        }
        fab.setOnClickListener { view ->
            buttonClicked(view)
        }
        Handler().postDelayed({
            //check if the first option is already occupied....
            val startDate = Calendar.getInstance().time
            println("INCREMENTING TIME, $startDate")
            //dataList.removeAt(0)
            //adapter.notifyItemChanged(0)

        }, 3000)
    }




    val handler = Handler()
    fun startRepeating(v:View){
        runnable.run()
    }
    fun stopRepeating(v:View){
        handler.removeCallbacks(runnable)
    }
    val runnable = object: Runnable { //Refreshes every .. seconds
        @RequiresApi(Build.VERSION_CODES.O)
        override fun run() {
            val startDate = Calendar.getInstance().time
            counter++
            println("Current count : $counter")
            //check if the current time is occupied....
            println("INCREMENTING TIME, $startDate")
            refreshList()
            handler.postDelayed(this, delay)
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_SAVE_MEETING && resultCode == Activity.RESULT_OK) {
            // Get data from bundle and update recyclerview here
            data?.getParcelableExtra<Data>(EXTRA_NEW_MEETING_DATA)?.let {
                Log.d("MainActivity", "Meeting Saved:${it.title}")
                dataList.add(it)
                doOnListUpdate()
                Handler().postDelayed({
                    recycle_main.scrollToPosition(dataList.size - 1)
                }, 400)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun buttonClicked(view: View){
        val intent = Intent(this,MakeBooking::class.java).apply {
            putParcelableArrayListExtra("datalist",dataList)
        }
        startActivityForResult(intent, REQUEST_CODE_SAVE_MEETING)
    }
    fun strToDate(date:String?,pattern:String? = pattern1): Date { //Changes the string to a date object.
        return SimpleDateFormat(pattern).parse(date)

    }
    fun fetchJson(){ //function to fetch json from the web.
        var keys:Iterator<String>
        println("FETCHING JSON")
        //val url = "https://api.myjson.com/bins/9wzmf"
        var url ="https://ratnuback.appspot.com/getBooking/"+ location
        val credential = Credentials.basic(username, password);
        val request = Request.Builder().url(url).header("Authorization",credential).build()
        val client = OkHttpClient()
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
                        //we can remove the options that are not in the day here.
                        val curDate = Calendar.getInstance().time
                        val booking = gson.fromJson(jsonObject[it].toString(), Data::class.java)
                        if(isEnd(booking,curDate)){
                            println("This booking has already ended")
                        }
                        else if(DateFunctions().isToday(curDate,booking)){
                            println("Adding entry since it is today!")
                            dataList.add(booking)
                        }
                        //dataList.add(gson.fromJson(jsonObject[it].toString(), Data::class.java))
                    }
                } catch (err: JSONException) {
                    Log.d("Error", err.toString())
                }
                dataList.sortBy { strToDate(it.startingTime) }
                val occ = getOccupied(dataList,Calendar.getInstance().time)
                if(occ!=null){
                    runOnUiThread{
                        txt_bookedTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP,50f)
                        val endingString = DateFunctions().
                            formatTimeUsingDate(strToDate(occ.endingTime), custSpinnerPat)
                        txt_nextBooking.text = "Booked until ${endingString}"
                        txt_bookedTitle.text = occ.title
                        txt_bookingUser.text = occ.userName
                    }
                }else{
                    runOnUiThread{
                        txt_bookedTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP,70f)
                        txt_bookedTitle.text = "FREE"
                        if(!dataList.isEmpty()){
                            //try to get the smallest entry.
                            val nextBooking = dataList[0]
                            val startingString = DateFunctions().
                                formatTimeUsingDate(strToDate(nextBooking.startingTime), custSpinnerPat)
                            txt_nextBooking.text = "Next booking: ${startingString}"
                            txt_bookingUser.text = ""
                        }else{
                            txt_nextBooking.text = "No more bookings today!"
                            txt_bookingUser.text = ""
                        }
                    }
                }
                dataList.forEach {
                    println("title : ${it.title}")
                }
                //dataList = sortByTime(dataList)
                doOnListUpdate()
            }
        })
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun doOnListUpdate() {
        //dataList = DateFunctions().sortByTime(dataList)
        //val s= MainAdapter(dataList).removeOptions()
        if(!dataList.isEmpty()){
            println("SORTING THE LIST")
            dataList.sortBy { strToDate(it.startingTime) }
        }
        runOnUiThread{
            println("DATALIST SIZE : " + dataList.count())
            //recycle_main.adapter =  MainAdapter(dataList) //Run the the recycler view code.
            adapter.notifyDataSetChanged();
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun refreshList(){
        dataList.clear()
        fetchJson()
    }
    fun sortByTime(dataList: java.util.ArrayList<Data>): java.util.ArrayList<Data> {
        //also remove entries that are not in the day...
        val pattern2 = "dd MMMMM yyyy"
        val dateFormat = SimpleDateFormat(pattern2)
        val todayStart = dateFormat.parse(getCurrentTimeUsingDate())
        val todayEnd = Date(todayStart.time + TimeUnit.DAYS.toMillis(1))
        println("THE DATE IS :"+todayStart)
        println("THE LATE IS "+todayEnd)
        val newData = java.util.ArrayList<Data>()
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

}
class Data (
    @SerializedName("a")
    var a:Content? = null,
    @SerializedName("endingTime")
    var endingTime:String? = null,
    @SerializedName("floor")
    var floor:Int? = null,
    @SerializedName("meetingRoom")
    var meetingRoom:String?= null,
    @SerializedName("meetingState")
    var meetingState:String? = null,
    @SerializedName("startingTime")
    var startingTime:String? = null,
    @SerializedName("title")
    var title:String? = null,
    @SerializedName("userName")
    var userName:String? = null
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

