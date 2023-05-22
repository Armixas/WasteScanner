package com.example.wastescanner.Data

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.io.Serializable
import java.util.*

data class WasteRecord(
    val Name: String? = "",
    val Count: Int? = null,
    val CreatedAt: WasteRecordTime? = null,
    val AspNetUserID: String? = null,
    val product: DocumentReference? = null
) : Serializable, Parcelable {
    //------------------------------------------------------------------
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readInt(),
        parcel.readParcelable(WasteRecordTime::class.java.classLoader),
        parcel.readString(),
        parcel.readString()?.let { FirebaseFirestore.getInstance().document(it) }
    )
    //------------------------------------------------------------------
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(Name)
        parcel.writeInt(Count!!)
        parcel.writeParcelable(CreatedAt, flags)
        parcel.writeString(AspNetUserID)
        parcel.writeString(product?.path)
    }

    override fun describeContents(): Int {
        return 0
    }
    //------------------------------------------------------------------
    companion object CREATOR : Parcelable.Creator<WasteRecord> {
        override fun createFromParcel(parcel: Parcel): WasteRecord {
            return WasteRecord(parcel)
        }

        override fun newArray(size: Int): Array<WasteRecord?> {
            return arrayOfNulls(size)
        }
    }
    //------------------------------------------------------------------
    fun toCsvString(): String {
        return "$Name,$Count,$CreatedAt,$product"
    }
    //------------------------------------------------------------------
}