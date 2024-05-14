package cr.ac.menufragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import cr.ac.una.controlfinanciero.adapter.MovimientoAdapter
import cr.ac.una.controlfinancierocamera.AddControlFinancieroFragment
import cr.ac.una.controlfinancierocamera.MainActivity
import cr.ac.una.controlfinancierocamera.R
import cr.ac.una.controlfinancierocamera.db.AppDatabase
import cr.ac.una.controlfinancierocamera.entity.Movimiento
import cr.ac.una.controlfinancierocamera.viewModel.ControlFinancieroViewModel
import cr.ac.una.jsoncrud.dao.MovimientoDAO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListControlFinancieroFragment : Fragment() {
    private lateinit var movimientoDao: MovimientoDAO
    private lateinit var viewModel: ControlFinancieroViewModel
    private lateinit var adapter: MovimientoAdapter
    private val movimientosList = mutableListOf<Movimiento>()
    companion object {
        private const val TAG = "ListControlFinancieroFragment" // Definir TAG como una constante en el companion object
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list_control_financiero, container, false)
        movimientoDao = AppDatabase.getInstance(requireContext()).ubicacionDao()
        viewModel = ViewModelProvider(this).get(ControlFinancieroViewModel::class.java)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val botonNuevo = view.findViewById<Button>(R.id.botonNuevo)
        val listView = view.findViewById<ListView>(R.id.listaMovimientos)

        botonNuevo.setOnClickListener {
            insertEntity()
        }

        // Observar LiveData para actualizar la lista de movimientos
        viewModel.movimientosLiveData.observe(viewLifecycleOwner) { movimientos ->
            adapter.clear()
            adapter.addAll(movimientos)
        }
        // Obtener los movimientos inicialmente
        obtenerMovimientos()

        adapter = MovimientoAdapter(requireContext(), movimientosList, lifecycleScope)
        listView.adapter = adapter
    }

    private fun obtenerMovimientos() {
        lifecycleScope.launch {
            try {
                val ubicaciones = withContext(Dispatchers.Default) {
                    movimientoDao.getAll()
                }
                viewModel.actualizarMovimientos(ubicaciones)
            } catch (e: Exception) {
                Log.e(TAG, "Error en la base de datos: ${e.message}")
            }
        }
    }
    private fun insertEntity() {
        val fragment = AddControlFinancieroFragment()
        val fragmentManager = (context as MainActivity).supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.home_content, fragment)
        transaction.addToBackStack(null) // Agrega la transacción a la pila de retroceso
        transaction.commit()
    }
}