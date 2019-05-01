package chicmic.com.locreminder.location

import android.Manifest
import android.annotation.SuppressLint
import android.app.IntentService
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.Toast
import chicmic.com.locreminder.datamodel.Task
import chicmic.com.locreminder.utils.CONST
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*

class LocationService:IntentService("intent service"), GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
LocationListener{

   lateinit var mTask:Task
    internal lateinit var mLocationCallback: LocationCallback
    internal var mGoogleApiClient: GoogleApiClient? = null
    internal lateinit var mLocationRequest: LocationRequest
    internal var mFusedLocationClient: FusedLocationProviderClient? = null

    override fun onConnected(p0: Bundle?) {
        mLocationRequest = LocationRequest()
        mLocationRequest.interval = 1000 * 3
        mLocationRequest.fastestInterval = 1000 * 3
        mLocationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onLocationChanged(location: Location?) {

        //  Toast.makeText(this,"CALL",Toast.LENGTH_SHORT).show()
        var d = distance(location!!.latitude, location!!.longitude, mTask.location.latitude, mTask.location.longitude)
        Log.d("distance", d.toString())
        // Toast.makeText(this,d.toString(),Toast.LENGTH_SHORT).show()
        if (d < 500) {
            sendBroadcast()
            stopSelf()
        }
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onProviderEnabled(provider: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onProviderDisabled(provider: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun onCreate() {
        super.onCreate()
        Log.d("intentService","oncreate")
        buildGoogleApiClient()

    }
    override fun onHandleIntent(intent: Intent?) {
        Log.d("intentService","onhandle intent")
       // sendBroadcast()
      //  stopSelf()

        mTask=intent!!.getSerializableExtra(CONST.TASK_OBJECT) as Task
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations){

                    onLocationChanged(location)
                }
            }
        }


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
        Log.d("intentService","ondestroy")
    }

    fun sendBroadcast()
    {

        var intent=Intent()
        intent.action=CONST.BROADCAST
        sendBroadcast(intent)
    }

    private fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val theta = lon1 - lon2
        var dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + (Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta)))
        dist = Math.acos(dist)
        dist = rad2deg(dist)
        dist = dist * 60.0 * 1.1515
        //into meters
        dist=dist*1000
        return dist
    }

    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    private fun rad2deg(rad: Double): Double {
        return rad * 180.0 / Math.PI
    }

}


