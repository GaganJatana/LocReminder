package chicmic.com.locreminder.alarm

import android.Manifest
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import chicmic.com.locreminder.R
import chicmic.com.locreminder.broadcastlistener.BroadCastListener
import chicmic.com.locreminder.database.DataBase
import chicmic.com.locreminder.datamodel.Task
import chicmic.com.locreminder.location.LocationMonitor
import chicmic.com.locreminder.utils.CONST
import kotlinx.android.synthetic.main.activity_create.*
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.Month
import java.time.Year
import java.util.*


class Create : AppCompatActivity() {
    var mDate: Date?=null
    var mId: Int=0
    lateinit var mName: String
    lateinit var mLocation: Location
    lateinit var mDataBase: DataBase
    lateinit var mlocName:String

    var mMonth = -1
    var mYear = -1
    var mDay = -1
    var mHour = -1
    var mMinute = -1
    var mWait: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)
        mDataBase = DataBase(this)
        mId=getId()
    }


    fun date(view: View) {
        // Get Current Date
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    mYear = year
                    mMonth = monthOfYear
                    mDay = dayOfMonth
                    datepicker.setText(dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year)
                }, year, month, day)
        datePickerDialog.show()

    }



    fun time(v: View) {


        // Get Current Time
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        // Launch Time Picker Dialog
        val timePickerDialog = TimePickerDialog(this,
                TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                    mHour = hourOfDay
                    mMinute = minute
                    var str=""
                    if (minute<10) {
                        str =   ":0" +minute
                    }
                    else{
                        str =   ":" +minute
                    }
                    if(hourOfDay>12)
                    {
                        str=(mHour-12).toString() +str+" PM"
                        }
                    else{
                        str= mHour.toString() +str+" AM"
                    }
                    timepicker.setText(str)
                }, hour, minute, false)
        timePickerDialog.show()
    }
fun validate():Boolean
{
    mName = name.text.toString()
    mlocName=location.text.toString()
    if (mName.isEmpty())
    {
        name.setError(getString(R.string.cant_empty))
        name.requestFocus()
        return false
    }
    else if(mlocName.isEmpty())
    {
        location.setError(getString(R.string.cant_empty))
        location.requestFocus()
        return false
    }
    else if (mDay==-1)
    {
        error(date_error,getString(R.string.cant_empty))
        //datepicker.requestFocus()
            return false
    }
    else if(mHour==-1)
    {
        error(time_error,getString(R.string.cant_empty))
       // timepicker.requestFocus()
        return false
    }
   else if(!checkDate())
    {
        return false
    }
    else if(!getLocation())
    {
        location.setError(getString(R.string.no_address))
        return false
    }
    return true
}
    fun error(textView: TextView,msg:String)
    {
        textView.visibility=View.VISIBLE
        textView.setText(msg)
    }

    fun checkDate():Boolean
    {
        val c=Calendar.getInstance()
        val y=c.get(Calendar.YEAR)
        val m=c.get(Calendar.MONTH)
        val d=c.get(Calendar.DAY_OF_MONTH)
        val h=c.get(Calendar.HOUR_OF_DAY)
        val mn=c.get(Calendar.MINUTE)
        if(mYear<y||mMonth<m||mDay<d)
        {
            error(date_error,getString(R.string.date_invalid))
            return false
        }
        else if(mHour<h||mMinute<mn)
        {
            error(time_error,getString(R.string.time_invalid))
            return false
        }
        return true
    }
    fun getLocation():Boolean
    {
        val loc=CONST.geoHelper(this,mlocName)
        if(loc!=null)
        {
            mLocation=loc
            return true
        }
        return false

    }
    fun check(): Task?{
        if(validate()) {
          //  mTime = SimpleDateFormat("hh:mm").parse(timepicker.text.toString())
            mDate = SimpleDateFormat("dd/MM/yyyy").parse(datepicker.text.toString())
            val date =SimpleDateFormat("dd/MM/yyyy").format(mDate)
            mWait = CONST.getmilisec(mYear, mMonth, mDay, mHour, mMinute )
            return Task(mId, mName, mLocation, mlocName, date,timepicker.text.toString(),CONST.ENABLE,CONST.ENABLE)
        }
        return null
    }
    fun getId():Int
    {
        val pref = applicationContext.getSharedPreferences(CONST.TASK_ID, 0)
        return  pref.getInt(CONST.BROADCAST_CODE,0)
    }
fun visible(){
    time_error.visibility=View.GONE
    date_error.visibility=View.GONE
    name.setError(null)
    location.setError(null)
}
    fun addToDatabase(view: View) {
        visible()
        val task=check()
        if(task!=null) {
            mDataBase.addTask(task)
            CONST.setAlarm(task, mWait, this,mId)
            mId++
            result(task)
        }

    }
    fun result(task: Task)
    {
        var intent= Intent()
        intent.putExtra(CONST.TASK_OBJECT,task)
        setResult(CONST.REQUEST_CREATE,intent)
        val pref = applicationContext.getSharedPreferences(CONST.TASK_ID, 0)
        pref.edit().putInt(CONST.BROADCAST_CODE,mId).apply()
         finish()
    }

}






