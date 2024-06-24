package cr.ac.una.controlfinancierocameraleoandarturo.db

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import cr.ac.una.controlfinancierocameraleoandarturo.converter.Converters
import cr.ac.una.controlfinancierocameraleoandarturo.entity.Movimiento
import cr.ac.una.controlfinancierocameraleoandarturo.entity.Lugar
import cr.ac.una.controlfinancierocameraleoandarturo.dao.LugarDAO
import cr.ac.una.controlfinancierocameraleoandarturo.dao.MovimientoDAO

@Database(entities = [Lugar::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun lugarDao(): LugarDAO

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
                    "DB-Lugar"
                ).fallbackToDestructiveMigration().build()
            } catch (ex: Exception) {
                // Log the exception with a custom message
                Log.e("AppDatabase", "Error al construir la base de datos", ex)
                throw ex  // Re-lanzamos la excepción para manejarla en un nivel superior si es necesario
            }
        }
    }
}