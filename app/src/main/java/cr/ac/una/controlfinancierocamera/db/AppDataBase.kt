package cr.ac.una.controlfinancierocamera.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import cr.ac.una.controlfinancierocamera.converter.Converters
import cr.ac.una.controlfinancierocamera.entity.Movimiento
import cr.ac.una.jsoncrud.dao.MovimientoDAO

@Database(entities = [Movimiento::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun ubicacionDao(): MovimientoDAO

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return try {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "DB-Movimiento"
                ).build()
            } catch (ex: Exception) {
                // Manejar excepciones durante la creación de la base de datos
                throw ex
            }
        }
    }
}
