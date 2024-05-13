package cr.ac.menufragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import cr.ac.una.controlfinanciero.adapter.MovimientoAdapter
import cr.ac.una.controlfinancierocamera.AddControlFinancieroFragment
import cr.ac.una.controlfinancierocamera.EditControlFinancieroFragment
import cr.ac.una.controlfinancierocamera.MainActivity
import cr.ac.una.controlfinancierocamera.R
import cr.ac.una.controlfinancierocamera.controller.MovimientoController
import cr.ac.una.controlfinancierocamera.db.AppDatabase
import cr.ac.una.controlfinancierocamera.entity.Movimiento
import cr.ac.una.jsoncrud.dao.MovimientoDAO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.internal.notify

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"


class ListControlFinancieroFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    lateinit var adapter: MovimientoAdapter
    val movimientoController = MovimientoController()
    //

    private lateinit var movimientoDao: MovimientoDAO
    companion object {
        private const val TAG = "ListControlFinancieroFragment" // Definir TAG como una constante en el companion object
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)

        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
    //return inflater.inflate(R.layout.fragment_list_control_financiero, container, false)
        val view = inflater.inflate(R.layout.fragment_list_control_financiero, container, false)
        movimientoDao = AppDatabase.getInstance(requireContext()).ubicacionDao()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val botonNuevo = view.findViewById<Button>(R.id.botonNuevo)
        val listView = view.findViewById<ListView>(R.id.listaMovimientos)

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

    fun actualizarData() {
        val mainActivity = context as MainActivity
        //mainActivity.adapter.notifyDataSetChanged()
    }


}