package chicmic.com.locreminder.broadcastlistener

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import chicmic.com.locreminder.alarm.ViewAlarm
import chicmic.com.locreminder.datamodel.Task
import chicmic.com.locreminder.utils.CONST


class BroadCastListener : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
      //  var task=intent!!.getParcelableExtra<Task>(CONST.TASK_OBJECT)
        var code=intent!!.getIntExtra(CONST.BROADCAST_CODE,-1)
          var intent1=Intent(context!!,ViewAlarm::class.java)
        //     intent1.putExtra(CONST.TASK_OBJECT,task)
            intent1.putExtra(CONST.BROADCAST_CODE,code)
            Log.d("Alarm triggered", "worked")
            context.startActivity(intent1)
    }
}