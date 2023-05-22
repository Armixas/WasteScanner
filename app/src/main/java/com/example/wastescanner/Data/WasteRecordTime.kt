package com.example.wastescanner.Data

import android.os.Parcel
import android.os.Parcelable

data class WasteRecordTime(
    val chronology: Map<String, Any>? = null,
    val dayOfMonth: Int? = null,
    val dayOfWeek: String? = null,
    val dayOfYear: Int? = null,
    val hour: Int? = null,
    val minute: Int? = null,
    val month: String? = null,
    val nano: Int? = null,
    val second: Int? = null,
    val year: Int? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readHashMap(Any::class.java.classLoader) as Map<String, Any>?,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? String,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int
    ) {
    }
    //------------------------------------------------------------------
    constructor(map: Map<String, Any>) : this(
        map["chronology"] as? Map<String, Any>,
        map["dayOfMonth"] as? Int,
        map["dayOfWeek"] as? String,
        map["dayOfYear"] as? Int,
        map["hour"] as? Int,
        map["minute"] as? Int,
        map["month"] as? String,
        map["nano"] as? Int,
        map["second"] as? Int,
        map["year"] as? Int
    )
    //------------------------------------------------------------------
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeMap(chronology)
        parcel.writeValue(dayOfMonth)
        parcel.writeString(dayOfWeek)
        parcel.writeValue(dayOfYear)
        parcel.writeValue(hour)
        parcel.writeValue(minute)
        parcel.writeValue(month)
        parcel.writeValue(nano)
        parcel.writeValue(second)
        parcel.writeValue(year)
    }
    //------------------------------------------------------------------
    override fun describeContents(): Int {
        return 0
    }
    //------------------------------------------------------------------
    companion object CREATOR : Parcelable.Creator<WasteRecordTime> {
        override fun createFromParcel(parcel: Parcel): WasteRecordTime {
            return WasteRecordTime(parcel)
        }

        override fun newArray(size: Int): Array<WasteRecordTime?> {
            return arrayOfNulls(size)
        }
    }
    //------------------------------------------------------------------
}