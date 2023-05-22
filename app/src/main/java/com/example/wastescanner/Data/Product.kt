package com.example.wastescanner.Data
import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

enum class WasteEnum(val englishName: String, val lithuanianName: String) {
    Plastic("Plastic", "Plastikas"),
    Paper("Paper", "Popierius"),
    Glass("Glass", "Stiklas"),
    Metal("Metal", "Metalas"),
    Wood("Wood", "Medis");

    override fun toString(): String {
        return englishName
    }
    fun toLtString(): String {
        return lithuanianName
    }
}


data class Product(
    val Barcode: Long? = null,
    val Description: String? = null,
    val Name: String? = null,
    val WasteCategory: String? = null,
    val Weight: Double = 0.0
): Serializable, Parcelable
{
    companion object CREATOR : Parcelable.Creator<Product> {
        override fun createFromParcel(parcel: Parcel): Product {
            return Product(parcel)
        }

        override fun newArray(size: Int): Array<Product?> {
            return arrayOfNulls(size)
        }
    }

    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readDouble()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        Barcode?.let { parcel.writeLong(it.toLong()) }
        parcel.writeString(Description)
        parcel.writeString(Name)
        parcel.writeString(WasteCategory)
        parcel.writeDouble(Weight)
    }

    override fun describeContents(): Int {
        return 0
    }

    fun toCsvString(): String {
        return "$Barcode,$Name,$Description,$WasteCategory,$Weight"
    }
}
