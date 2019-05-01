package chicmic.com.locreminder.database


import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteCursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.location.Location
import android.util.Log
import android.widget.Toast
import chicmic.com.locreminder.datamodel.Task
import chicmic.com.locreminder.utils.CONST
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*


class DataBase (context: Context) : SQLiteOpenHelper(context, CONST.DATABASE, null,CONST.DATABASE_VERSION) {


    lateinit var mDataBase: SQLiteDatabase
    val mWrite = writableDatabase
    val mRead = readableDatabase
    val mContext = context
    override fun onCreate(db: SQLiteDatabase?) {

        db?.execSQL(CONST.TABLE_CREATE)
        mDataBase = db!!

    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {

    }


    fun addTask(task: Task) {
        val accountValues = ContentValues().apply {
            put(CONST.TASK_ID, task.ID)
            put(CONST.TASK_NAME, task.name)
            put(CONST.DATE, task.date)
            put(CONST.TIME, task.time)
            put(CONST.LOCATION_NAME, task.locationName)
            put(CONST.LATITUDE, task.location.latitude.toString())
            put(CONST.LONGITUDE, task.location.longitude.toString())
            put(CONST.NOTIFICATION, CONST.ENABLE)
            put(CONST.ALARM,CONST.ENABLE)
        }
        mWrite.insert(CONST.TABLE_NAME, null, accountValues)
        Log.d("database", "inserted")

    }




    fun updateTask(id:Int,notification:String,alarm:String) {
        val contactValues = ContentValues().apply {
            put(CONST.NOTIFICATION,notification)
            put(CONST.ALARM,alarm)
        }
        mWrite.update(CONST.TABLE_NAME, contactValues, "${CONST.TASK_ID}=$id", null)
        Log.d("database", "updated")
    }

    fun getTaskList(): MutableList<Task> {
        val selectUser = "select * from ${CONST.TABLE_NAME}  where ${CONST.ALARM}=='${CONST.ENABLE}'"
        val cursor = mRead.rawQuery(selectUser, null)
        Log.d("database", cursor.count.toString())
        return getlist(cursor)
    }

    fun getTodayTask() :MutableList<Task>{
        var curDate:Date= Calendar.getInstance().getTime()
        var date =SimpleDateFormat("dd/MM/yyyy").format(curDate)
        date.trim()
        val selectUser = "select * from ${CONST.TABLE_NAME} where ${CONST.DATE}=='${date}' AND ${CONST.ALARM}=='${CONST.ENABLE}'"
        val cursor = mRead.rawQuery(selectUser, null)
        Log.d("databasetoday", cursor.count.toString())
        return getlist(cursor)
    }

    fun getTaskById(request: Int): Task {

        var list= mutableListOf<Task>()
        val cursor = mRead.query(CONST.TABLE_NAME, arrayOf<String>(CONST.TASK_ID, CONST.TASK_NAME, CONST.LOCATION_NAME, CONST.LONGITUDE, CONST.LATITUDE, CONST.DATE, CONST.TIME,CONST.NOTIFICATION,CONST.ALARM), CONST.TASK_ID + "=?",
                arrayOf(request.toString()), null, null, null, null)

        list=getlist(cursor)
        return list[0]
    }

    fun getlist(cursor: Cursor):MutableList<Task>
    {
        var list= mutableListOf<Task>()
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                val id=cursor.getInt(cursor.getColumnIndex(CONST.TASK_ID))
                val name = cursor.getString(cursor.getColumnIndex(CONST.TASK_NAME))
                val lat = cursor.getString(cursor.getColumnIndex(CONST.LONGITUDE)).toDouble()
                val long = cursor.getString(cursor.getColumnIndex(CONST.LATITUDE)).toDouble()

                val loc=Location("")
                loc.latitude=lat
                loc.longitude=long
                val locName=cursor.getString(cursor.getColumnIndex(CONST.LOCATION_NAME))
                val date = cursor.getString(cursor.getColumnIndex(CONST.DATE))
                val time=cursor.getString(cursor.getColumnIndex(CONST.TIME))
                val notification=cursor.getString(cursor.getColumnIndex(CONST.NOTIFICATION))
                val alarm=cursor.getString(cursor.getColumnIndex(CONST.ALARM))
                val task = Task(id,name, loc,locName, date,time,notification,alarm)
                list.add(task)

            }
            return list
        }
        return list
    }
//    fun delete(pId:Int)
//    {
//        mWrite.delete(CONSTANTS.CONTACTS, "${ CONSTANTS.CONTACT_ID} =$pId",null)
//    }
}
