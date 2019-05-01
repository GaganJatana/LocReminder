package chicmic.com.locreminder.location

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.Manifest
import android.annotation.SuppressLint
import android.app.IntentService
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.Toast
import chicmic.com.locreminder.database.DataBase
import chicmic.com.locreminder.datamodel.Task
import chicmic.com.locreminder.utils.CONST
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import android.database.sqlite.SQLiteDatabase


class LocationMonitor: Service() , GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
LocationListener{
    internal lateinit var mLocationCallback: LocationCallback
    internal var mGoogleApiClient: GoogleApiClient? = null
    internal lateinit var mLocationRequest: LocationRequest
    internal var mFusedLocationClient: FusedLocationProviderClient? = null
    lateinit var mDataBase: DataBase
   lateinit var list: MutableList<Task>
    var size=0
    var mCounter=0
    override fun onConnected(p0: Bundle?) {
        mLocationRequest = LocationRequest()
        mLocationRequest.interval = 1000 * 9
        mLocationRequest.fastestInterval = 1000 * 5
        mLocationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            mFusedLocationClient!!.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        // Got last known location. In some rare situations this can be null.

                        onLocationChanged(location!!)
                    }
            mFusedLocationClient!!.requestLocationUpdates(mLocationRequest!!,mLocationCallback!!,null)
            //      mFusedLocationClient!!.removeLocationUpdates(mLocationCallback)
        }
        Log.d("LocationService","onconnect")
    }

    override fun onConnectionSuspended(p0: Int) {

    }

    override fun onConnectionFailed(p0: ConnectionResult) {

    }

    override fun onLocationChanged(location: Location?) {

        if (size==0)
        {
            stopSelf()
        }
        else
        {
            for(i in list.indices)
            {
                checkLoc(location, list[i],i)

            }

        }
        //  Toast.makeText(this,"CALL",Toast.LENGTH_SHORT).show()

    }
fun checkLoc(location: Location?,task:Task,index:Int)
{
    var d:Double = distance(location!!.latitude,  task.location.latitude,task.location.longitude, location!!.longitude,"K")
    Log.d("distance", d.toString()+" "+task.locationName)
    Log.d("distanceu",location!!.latitude.toString()+" "+ location!!.longitude.toString())
    Log.d("distancet",task.location!!.latitude.toString()+" "+ task.location!!.longitude.toString())
    // Toast.makeText(this,d.toString(),Toast.LENGTH_SHORT).show()
    if (d < 500 &&task.notification==CONST.ENABLE) {
        sendBroadcast(task)
        list[index].notification=CONST.DISABLE

    }
}
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

    }


    override fun onProviderEnabled(provider: String?) {

    }

    override fun onProviderDisabled(provider: String?) {

    }
    override fun onBind(intent: Intent?): IBinder? {

        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("service","onCreate")
        buildGoogleApiClient()
        mDataBase = DataBase(this)
    }

    override  fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("service","onstart")
        list=mDataBase.getTodayTask()
        size=list.size
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations){

                    onLocationChanged(location)
                }
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }
    @Synchronized
    protected fun buildGoogleApiClient() {
        Log.d("location changed", "call to BuildClient")
        mGoogleApiClient = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build()
        mGoogleApiClient!!.connect()
    }
    override fun onDestroy() {
        super.onDestroy()
        Log.d("service","ondestroy")

    }
    fun sendBroadcast(task: Task)
    {

        var intent=Intent()
        intent.action=CONST.BROADCAST
        intent.putExtra(CONST.TASK_OBJECT,task)
        sendBroadcast(intent)


    }

    private fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double, unit: String): Double {
        if (lat1 == lat2 && lon1 == lon2) {
            return 0.0
        } else {
            val theta = lon1 - lon2
            var dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta))
            dist = Math.acos(dist)
            dist = Math.toDegrees(dist)
            dist = dist * 60.0 * 1.1515
            if (unit === "K") {
                dist = dist * 1.609344
            }
            return dist*1000
        }
    }


}