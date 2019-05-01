package chicmic.com.locreminder.alarm

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import chicmic.com.locreminder.R
import chicmic.com.locreminder.database.DataBase
import chicmic.com.locreminder.datamodel.Task
import chicmic.com.locreminder.location.LocationMonitor
import chicmic.com.locreminder.utils.CONST
import kotlinx.android.synthetic.main.activity_dashboard.*


class Dashboard : AppCompatActivity() {

    lateinit var list:MutableList<Task>
    lateinit var mAdapter:AdapterRecycleView
    lateinit var mDataBase: DataBase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        mDataBase = DataBase(this)
        list = mDataBase.getTaskList()
        mAdapter=AdapterRecycleView(list,this)
        rview.layoutManager= LinearLayoutManager(this, LinearLayout.VERTICAL,false)
        rview.setHasFixedSize(true)
        rview.adapter=mAdapter
        checkPermission()

    }

    fun checkPermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            service()
        } else {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    1)

        }

    }
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            1 -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    service()

                } else {
                    Toast.makeText(this,"without permission u cant get location notifications",Toast.LENGTH_SHORT).show()
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }
        }
    }

    fun service()
    {
        var serviceIntent= Intent(this,LocationMonitor::class.java)
        startService(serviceIntent)
    }
    fun addTask(view: View)
    {
        var intent=Intent(this,Create::class.java)
       startActivityForResult(intent,CONST.REQUEST_CREATE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // check if the request code is same as what is passed  here it is 2
        if(resultCode==0&&data==null)
        {

        }
        else if(requestCode == CONST.REQUEST_CREATE) {
            var task=data!!.getParcelableExtra(CONST.TASK_OBJECT) as Task
            list.add(task)
            mAdapter.notifyDataSetChanged()
            service()
        }

    }
}
