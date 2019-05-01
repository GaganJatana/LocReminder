package chicmic.com.locreminder.alarm

import android.media.MediaPlayer
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import chicmic.com.locreminder.R
import chicmic.com.locreminder.database.DataBase
import chicmic.com.locreminder.datamodel.Task
import chicmic.com.locreminder.utils.CONST
import kotlinx.android.synthetic.main.activity_view_alarm.*


class ViewAlarm : AppCompatActivity() {
  lateinit var mTone: MediaPlayer
lateinit var mTask: Task
    lateinit var mDataBase: DataBase
    var mCode=-1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_alarm)
        mTone=MediaPlayer.create(this, Settings.System.DEFAULT_RINGTONE_URI)
        mDataBase = DataBase(this)
        mCode=intent.getIntExtra(CONST.BROADCAST_CODE,-1)
        mTask=mDataBase.getTaskById(mCode)
        atask.text=mTask.name
        alocation.text=mTask.locationName
        mTone.start()
        mDataBase.updateTask(mTask.ID,CONST.DISABLE,CONST.DISABLE)
    }
    fun stop(view: View)
    {
        mTone.stop()
        finish()
    }
    fun update(view: View)
    {
        val builder = AlertDialog.Builder(this)

        builder.setMessage(getString(R.string.snooze))
        builder.setPositiveButton(getString(R.string.ten_Minute)){dialog, which ->
            CONST.setAlarm(mTask,600000,this,mCode)
            mTone.stop()
            finish()
        }

        builder.setNeutralButton(getString(R.string.five_minute)){dialog,which ->
            CONST.setAlarm(mTask,300000,this,mCode)
            mTone.stop()
            finish()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

}
