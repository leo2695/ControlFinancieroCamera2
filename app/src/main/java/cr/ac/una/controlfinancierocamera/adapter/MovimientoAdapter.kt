package cr.ac.una.controlfinanciero.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import cr.ac.menufragment.CameraFragment
import cr.ac.una.controlfinancierocamera.EditControlFinancieroFragment
import cr.ac.una.controlfinancierocamera.MainActivity
import cr.ac.una.controlfinancierocamera.R

import cr.ac.una.controlfinancierocamera.entity.Movimiento
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MovimientoAdapter (context:Context, movimientos:List<Movimiento>):
    ArrayAdapter<Movimiento>(context,0,movimientos){


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var view = LayoutInflater.from(context)
            .inflate(R.layout.list_item, parent, false)
        val monto = view.findViewById<TextView>(R.id.monto)
        val tipo = view.findViewById<TextView>(R.id.tipo)
        val fecha = view.findViewById<TextView>(R.id.fecha)

        var movimiento = getItem(position)
        monto.text = movimiento?.monto.toString()
        tipo.text = movimiento?.tipo.toString()
        fecha.text = movimiento?.fecha.toString()

        var bottonDelete = view.findViewById<ImageButton>(R.id.button_delete)
        bottonDelete.setOnClickListener{
            val mainActivity = context as MainActivity
            GlobalScope.launch(Dispatchers.Main) {
                movimiento?.let { it1 -> mainActivity.movimientoController.deleteMovimiento(it1) }
                clear()
                addAll(mainActivity.movimientoController.listMovimientos())
                notifyDataSetChanged()
                notifyDataSetChanged()
            }
        }
        var bottonUpdate = view.findViewById<ImageButton>(R.id.button_update)
        bottonUpdate.setOnClickListener {
            val mainActivity = context as MainActivity
            GlobalScope.launch(Dispatchers.Main) {
                movimiento?.let { it1 ->
                    // Obtener el movimiento específico del API
                    val movimientoFromAPI = it1._uuid?.let { it2 ->
                        mainActivity.movimientoController.getMovimiento(
                            it2
                        )
                    }

                    // Crear un nuevo fragmento de edición y pasar el movimiento obtenido
                    val fragmentManager = (context as AppCompatActivity).supportFragmentManager
                    val editControlFinancieroFragment = EditControlFinancieroFragment().apply {
                        arguments = Bundle().apply {
                            putParcelable("movimiento", movimientoFromAPI) // Pasar el movimiento al fragmento de edición
                        }
                    }

                    // Reemplazar el fragmento actual con el fragmento de edición en el FragmentManager
                    fragmentManager.beginTransaction()
                        .replace(R.id.home_content, editControlFinancieroFragment)
                        .addToBackStack(null)
                        .commit()
                }
            }
        }


        return view
    }
}