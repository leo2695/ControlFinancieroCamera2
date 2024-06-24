package cr.ac.una.controlfinancierocameraleoandarturo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import cr.ac.una.controlfinancierocameraleoandarturo.R
import cr.ac.una.controlfinancierocameraleoandarturo.entity.Lugar
import cr.ac.una.controlfinancierocameraleoandarturo.viewmodel.CantidadFrecuenteViewModel

class FrecuenteFragment : Fragment() {

    private lateinit var cantidadFrecuenteViewModel: CantidadFrecuenteViewModel
    private lateinit var textViewCantidadFrecuente: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_frecuente, container, false)
        textViewCantidadFrecuente = root.findViewById(R.id.textViewCantidadFrecuente)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cantidadFrecuenteViewModel = ViewModelProvider(this).get(CantidadFrecuenteViewModel::class.java)
        cantidadFrecuenteViewModel.init(requireContext())

        cantidadFrecuenteViewModel.lugarMasFrecuente.observe(viewLifecycleOwner, Observer { lugarMasFrecuente ->
            if (lugarMasFrecuente != null) {
                textViewCantidadFrecuente.text = "Lugar más frecuente: ${lugarMasFrecuente.nombreLugar}"
            } else {
                textViewCantidadFrecuente.text = "No hay lugares frecuentes disponibles."
            }
        })
    }
}
