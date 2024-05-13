package cr.ac.una.jsoncrud.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import cr.ac.una.controlfinancierocamera.entity.Movimiento
import cr.ac.una.controlfinancierocamera.entity.Movimientos
import retrofit2.http.*

@Dao
interface MovimientoDAO {

        @Insert
        fun insert(entity: Movimiento)

        @Query("SELECT * FROM movimiento")
        fun getAll(): List<Movimiento?>?

        @Update
        fun update(entity: Movimiento)

        @Delete
        fun delete(entity: Movimiento)
}
