package cr.ac.una.controlfinancierocamera.entity

import android.os.Parcel
import android.os.Parcelable

data class Movimiento(
    var _uuid :String?,
    var tipo: String,
    var monto: String,
    var fecha: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(tipo)
        parcel.writeString(monto)
        parcel.writeString(fecha)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Movimiento> {
        override fun createFromParcel(parcel: Parcel): Movimiento {
            return Movimiento(parcel)
        }

        override fun newArray(size: Int): Array<Movimiento?> {
            return arrayOfNulls(size)
        }
    }
}