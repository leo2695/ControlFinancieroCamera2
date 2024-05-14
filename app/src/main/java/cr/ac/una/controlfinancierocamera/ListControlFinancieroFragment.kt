package cr.ac.menufragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import cr.ac.una.controlfinanciero.adapter.MovimientoAdapter
import cr.ac.una.controlfinancierocamera.AddControlFinancieroFragment
import cr.ac.una.controlfinancierocamera.R
import cr.ac.una.controlfinancierocamera.controller.MovimientoController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListControlFinancieroFragment : Fragment() {
    lateinit var adapter: MovimientoAdapter
    val movimientoController = MovimientoController()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_control_financiero, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val botonNuevo = view.findViewById<Button>(R.id.botonNuevo)
        botonNuevo.setOnClickListener {
            insertEntity()
        }
        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                movimientoController.listMovimientos()
                val list = view.findViewById<ListView>(R.id.listaMovimientos)
                adapter = MovimientoAdapter(requireContext(), movimientoController.listMovimientos())
                list.adapter = adapter
            }
        }
    }

    private fun insertEntity() {
        val transactionFragment = AddControlFinancieroFragment()
        val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
        transaction.replace(R.id.home_content, transactionFragment)
        transaction.commit()
    }
}