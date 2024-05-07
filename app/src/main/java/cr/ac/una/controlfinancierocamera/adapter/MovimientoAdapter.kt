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
        bottonUpdate.setOnClickListener{
            val mainActivity = context as MainActivity
            GlobalScope.launch(Dispatchers.Main) {
                movimiento?.let { it1 -> mainActivity.movimientoController.updateMovimiento(it1)
                    //Editar
                    val fragmentManager = (context as AppCompatActivity).supportFragmentManager
                    val editTransactionFragment = EditControlFinancieroFragment().apply {
                        arguments = Bundle().apply {
                            putParcelable("transaction", movimiento) // Pasar la transacción al fragmento de edición
                        }
                    }

                    fragmentManager.beginTransaction()
                        .replace(R.id.home_content, editTransactionFragment) // Reemplazar el fragmento actual con el fragmento de edición
                        .addToBackStack(null) // Agregar el fragmento actual a la pila de retroceso
                        .commit()
                    Log.d("TransactionListAdapter", "Editar transacción en la posición: $position")
                }

            }
        }
        return view
    }
}