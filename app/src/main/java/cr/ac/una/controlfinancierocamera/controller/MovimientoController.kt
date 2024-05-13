package cr.ac.una.controlfinancierocamera.controller

import android.util.Log
import cr.ac.una.controlfinancierocamera.entity.Movimiento
import cr.ac.una.controlfinancierocamera.entity.Movimientos
import cr.ac.una.controlfinancierocamera.service.MovimientoService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MovimientoController {
    /*var movimientoService= MovimientoService()

    suspend fun insertMovimiento(movimiento: Movimiento){

            var movimientos: ArrayList<Movimiento> = arrayListOf()
            movimientos.add(movimiento)
            movimientoService.apiService.createItem(movimientos)

    }
    suspend fun  deleteMovimiento(movimiento: Movimiento){
        try {
            movimiento._uuid?.let { uuid ->
                movimientoService.apiService.deleteItem(uuid)

            }
        } catch (e: Exception) {

        }
    }
    suspend fun  listMovimientos():ArrayList<Movimiento>{
            return movimientoService.apiService.getItems().items as ArrayList<Movimiento>
    }

    suspend fun updateMovimiento(movimiento: Movimiento) {
        try {
             movimientoService.apiService.updateItem(movimiento._uuid ?: "", movimiento)
        } catch (e: Exception) {
            Log.e("MovimientoController", "Error al actualizar el movimiento", e)
            throw e // Re-lanzar la excepción para manejarla en el fragmento si es necesario
        }
    }


    suspend fun getMovimiento(uuid: String): Movimiento {
        return movimientoService.apiService.getItem(uuid)
    }*/
}