package cr.ac.una.controlfinanciero.adapter

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.LifecycleCoroutineScope
import cr.ac.una.controlfinancierocamera.EditControlFinancieroFragment
import cr.ac.una.controlfinancierocamera.MainActivity
import cr.ac.una.controlfinancierocamera.R
import cr.ac.una.controlfinancierocamera.db.AppDatabase
import cr.ac.una.controlfinancierocamera.entity.Movimiento
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MovimientoAdapter (context:Context, movimientos:List<Movimiento>, private val lifecycleScope: LifecycleCoroutineScope):
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
            confirmarBorrar(getItem(position))
        }
        var bottonUpdate = view.findViewById<ImageButton>(R.id.button_update)
        bottonUpdate.setOnClickListener {
            context as MainActivity
            GlobalScope.launch(Dispatchers.Main) {
                val movimiento = getItem(position)
                Log.d("Boton Update", "Movimiento: $movimiento")
                editarMovimiento(movimiento)
            }
        }
        return view
    }

    private fun confirmarBorrar(movimiento: Movimiento?) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Eliminar")
        builder.setMessage("¿Deseas borrar este dato?")
        builder.setPositiveButton("Sí") { dialog, which ->
            movimiento?.let { borrarTransaccion(it) }
        }
        builder.setNegativeButton("No") { dialog, which -> }
        val dialog = builder.create()
        dialog.show()
    }

    private fun borrarTransaccion(movimiento: Movimiento) {
        val movimientoDao = AppDatabase.getInstance(context).ubicacionDao()
        val movimientos = this
        lifecycleScope.launch {
            withContext(Dispatchers.Default) {
                movimientoDao.delete(movimiento)
            }
            movimientos.remove(movimiento) // Suponiendo que movimientos es la lista que alimenta el Adapter
            notifyDataSetChanged() // Notificar al adapter que los datos han cambiado
        }
    }

    private fun editarMovimiento(movimiento: Movimiento?){
        val fragment = EditControlFinancieroFragment()
        val args = Bundle()
        args.putSerializable("movimiento", movimiento)
        fragment.arguments = args
        val fragmentManager = (context as MainActivity).supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.home_content, fragment)
        transaction.addToBackStack(null) // Agrega la transacción a la pila de retroceso
        transaction.commit()
    }
}