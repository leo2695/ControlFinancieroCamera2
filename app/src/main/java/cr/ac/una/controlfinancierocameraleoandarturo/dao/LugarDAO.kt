package cr.ac.una.controlfinancierocameraleoandarturo.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import cr.ac.una.controlfinancierocameraleoandarturo.entity.Lugar

@Dao
interface LugarDAO {
    @Insert
    suspend fun insertLugar(lugar: Lugar)

    @Insert
    suspend fun insertAllLugares(lugares: List<Lugar>)

    @Query("SELECT * FROM lugares")
    suspend fun getAllLugares(): List<Lugar>

    @Query("SELECT * FROM lugares ORDER BY fechaHora DESC LIMIT 1")
    suspend fun getLugarMasFrecuente(): Lugar?
}
