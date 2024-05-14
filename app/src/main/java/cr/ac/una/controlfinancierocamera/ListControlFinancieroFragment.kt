package cr.ac.menufragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
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
    companion object {
        private const val TAG = "ListControlFinancieroFragment" // Definir TAG como una constante en el companion object
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list_control_financiero, container, false)
        movimientoDao = AppDatabase.getInstance(requireContext()).ubicacionDao()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val botonNuevo = view.findViewById<Button>(R.id.botonNuevo)
        val listView = view.findViewById<ListView>(R.id.listaMovimientos)

        viewModel = ViewModelProvider(this).get(ControlFinancieroViewModel::class.java)
        viewModel.textLiveData.observe(viewLifecycleOwner, Observer { newText ->
            // Aquí puedes hacer algo con el nuevo texto, por ejemplo, mostrarlo en un TextView
            //textview.text = newText
        })

        botonNuevo.setOnClickListener {
            insertEntity()
        }
        lifecycleScope.launch {
            try {
                val ubicaciones = withContext(Dispatchers.Default) {
                    movimientoDao.getAll() // Obtener los datos de la base de datos
                }
                val adapter = MovimientoAdapter(requireContext(), ubicaciones as List<Movimiento>, lifecycleScope)
                listView.adapter = adapter
            } catch (e: Exception) {
                // Manejar errores adecuadamente, como mostrar un mensaje de error al usuario
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