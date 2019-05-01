package chicmic.com.locreminder.datamodel

import android.location.Location
import android.os.Parcel

import android.os.Parcelable


data class Task(val ID:Int, val name:String, val location: Location, val locationName:String, val date: String, val time:String, var notification:String, val alarm:String) :Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString(),
            parcel.readParcelable(Location::class.java.classLoader),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(ID)
        parcel.writeString(name)
        parcel.writeParcelable(location, flags)
        parcel.writeString(locationName)
        parcel.writeString(date)
        parcel.writeString(time)
        parcel.writeString(notification)
        parcel.writeString(alarm)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Task> {
        override fun createFromParcel(parcel: Parcel): Task {
            return Task(parcel)
        }

        override fun newArray(size: Int): Array<Task?> {
            return arrayOfNulls(size)
        }
    }
}
