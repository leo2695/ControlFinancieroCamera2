package cr.ac.una.controlfinancierocamera.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.Date

@Entity
data class Movimiento(
    @PrimaryKey(autoGenerate = true) val id: Long?,
    //var _uuid :String?,
    var tipo: String,
    var monto : String,
    var fecha :String,
) : Serializable