package cr.ac.una.controlfinancierocameraleoandarturo.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cr.ac.una.controlfinancierocameraleoandarturo.db.AppDatabase
import cr.ac.una.controlfinancierocameraleoandarturo.entity.Lugar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import cr.ac.una.controlfinancierocameraleoandarturo.dao.LugarDAO

class CantidadFrecuenteViewModel : ViewModel() {

    private lateinit var lugarDao: LugarDAO
    private val _lugarMasFrecuente = MutableLiveData<Lugar?>()
    val lugarMasFrecuente: LiveData<Lugar?>
        get() = _lugarMasFrecuente

    fun init(context: Context) {
        lugarDao = AppDatabase.getInstance(context).lugarDao()
        obtenerLugarMasFrecuente()
    }

    private fun obtenerLugarMasFrecuente() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val lugarMasFrecuente = lugarDao.getLugarMasFrecuente()
                _lugarMasFrecuente.postValue(lugarMasFrecuente)
            } catch (e: Exception) {
                // Manejar el error si es necesario
                _lugarMasFrecuente.postValue(null)
            }
        }
    }
}
