package cr.ac.una.controlfinancierocameraleoandarturo.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "lugares")
data class Lugar(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val coordenadas: String,
    val fechaHora: Date,
    val nombreArticuloWikipedia: String,
    val nombreLugar: String
)
