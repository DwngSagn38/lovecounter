package com.example.applove.roomdb.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Person")
data class PersonModel(
    @PrimaryKey(autoGenerate = false) var id: Int,
    @ColumnInfo(name = "name") var name: String?,
    @ColumnInfo(name = "gender") var gender: String?,
    @ColumnInfo(name = "birthday") var birthday: String?,
    @ColumnInfo(name = "image") var image: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(gender)
        parcel.writeString(birthday)
        parcel.writeString(image)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PersonModel> {
        override fun createFromParcel(parcel: Parcel): PersonModel {
            return PersonModel(parcel)
        }

        override fun newArray(size: Int): Array<PersonModel?> {
            return arrayOfNulls(size)
        }
    }
}
