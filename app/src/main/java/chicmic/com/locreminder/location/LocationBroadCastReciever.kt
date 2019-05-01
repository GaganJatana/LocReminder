package chicmic.com.locreminder.location

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.util.Log
import chicmic.com.locreminder.R
import chicmic.com.locreminder.database.DataBase
import chicmic.com.locreminder.datamodel.Task
import chicmic.com.locreminder.utils.CONST

class LocationBroadCastReciever : BroadcastReceiver() {
    lateinit var mDataBase: DataBase
   lateinit var mTask: Task
    override fun onReceive(context: Context?, intent: Intent?) {
        var pattern=longArrayOf(500, 500, 500, 500, 500, 500, 500, 500, 500)
         mTask =intent!!.getParcelableExtra<Task>(CONST.TASK_OBJECT)
        val builder = NotificationCompat.Builder(context!!, CONST.CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_template_icon_bg)
                .setContentTitle(mTask.name)
                .setContentText(mTask.locationName)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                //.setContentIntent(pendingIntent)

                .setVibrate(pattern)
                .setAutoCancel(true)
        with(NotificationManagerCompat.from(context!!)) {
            // notificationId is a unique int for each notification that you must define
            notify(mTask.ID, builder?.build())
            Log.d("notification","triggered")
        }
        mDataBase = DataBase(context)
        mDataBase.updateTask(mTask.ID,CONST.DISABLE,CONST.ENABLE)

    }
}