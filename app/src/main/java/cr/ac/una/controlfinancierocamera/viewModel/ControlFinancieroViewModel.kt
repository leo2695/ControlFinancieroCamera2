package cr.ac.una.controlfinancierocamera.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cr.ac.una.controlfinancierocamera.entity.Movimiento

class ControlFinancieroViewModel : ViewModel() {
    private val _movimientosLiveData = MutableLiveData<List<Movimiento>>()
    val movimientosLiveData: LiveData<List<Movimiento>> get() = _movimientosLiveData

    fun actualizarMovimientos(nuevaLista: List<Movimiento?>?) {
        _movimientosLiveData.value = nuevaLista as List<Movimiento>?
    }
}