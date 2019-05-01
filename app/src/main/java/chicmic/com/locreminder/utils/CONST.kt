package chicmic.com.locreminder.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.Toast
import chicmic.com.locreminder.broadcastlistener.BroadCastListener
import chicmic.com.locreminder.datamodel.Task
import chicmic.com.locreminder.location.LocationMonitor
import java.util.*
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.util.Log
import java.util.*
object CONST{

    const val SPLASH_TIME =3000
    const val TABLE_NAME="TaskLocation"
   const val TASK_NAME="Name"
    const val TASK_ID="Id"
    const val DATE="Date"
    const val TIME="time"
    const val LATITUDE="latitude"
    const val LONGITUDE="longitude"
    const val LOCATION_NAME="locationName"
 const val NOTIFICATION="notification"
 const val ALARM="alarm"
    const val ENABLE="enable"
    const val DISABLE="disable"

    const val TABLE_CREATE="CREATE TABLE IF NOT EXISTS $TABLE_NAME($TASK_ID INTEGER PRIMARY KEY,$TASK_NAME TEXT,$LOCATION_NAME TEXT,$DATE TEXT,$LATITUDE TEXT,$LONGITUDE TEXT,$TIME TEXT ,$NOTIFICATION TEXT,$ALARM TEXT)"
    const val DATABASE="TaskLOCATION.db"
    const val DATABASE_VERSION=1
    const val BROADCAST="com.chicmic.ALARM_INTENT"

    const val BROADCAST_CODE="code"
    const val CHANNEL_ID="task"
    const val TASK_OBJECT="taskObject"
 const val REQUEST_CREATE=5

 fun getmilisec(year: Int, month: Int, day: Int, hours: Int, min: Int,sec:Int=0):Long {

  val calendar = Calendar.getInstance()
     var sec=calendar.get(Calendar.SECOND)
        sec=sec*1000
  calendar.set(year, month, day, hours, min)
  val c = currentTime()

  return  (calendar.timeInMillis - c.timeInMillis)-sec
 }
 fun currentTime():Calendar
 {
  val c = Calendar.getInstance()
  val mYear = c.get(Calendar.YEAR)
  val mMonth = c.get(Calendar.MONTH)
  val mDay = c.get(Calendar.DAY_OF_MONTH)
  val mHour = c.get(Calendar.HOUR_OF_DAY)
  val mMinute = c.get(Calendar.MINUTE)
  c.set(mYear, mMonth, mDay, mHour, mMinute)
  return c
 }
 fun setAlarm(task: Task, time:Long, context: Context,code:Int) {
  var intent = Intent(context, BroadCastListener::class.java)
  //intent.putExtra(CONST.TASK_OBJECT,task)
  intent.putExtra(CONST.BROADCAST_CODE,code)
  var pendingIntent = PendingIntent.getBroadcast(context,code , intent, 0)
  var alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
  alarm.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + time, pendingIntent)
  Toast.makeText(context, time.toString(), Toast.LENGTH_SHORT).show()
  // start service for location tracking

 }
   fun geoHelper(context: Context,locationName:String):Location?{
        val geocoder=Geocoder(context, Locale.getDefault())
        var location=Location("")
            var addr:Address? = null
            try {
                if (!locationName.isEmpty()) {
                    var list: List<Address> = ArrayList<Address>(0)
                    list = geocoder.getFromLocationName(locationName, 1)
                    if (list != null && !list.isEmpty()) {
                        addr = list.get(0)
                      location.longitude=addr.longitude
                        location.latitude=addr.latitude
                        Log.e("address",location.longitude.toString()+" "+location.latitude.toString())
                        return location
                    }
                }
            }catch (ex:Exception){
                Log.e("Data","Error in geocoder: "+ex)
            }

            return null

    }
}