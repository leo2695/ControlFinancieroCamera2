package cr.ac.una.controlfinancierocamera

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import java.util.Calendar
import java.util.Locale
import android.app.DatePickerDialog
import cr.ac.una.controlfinancierocamera.entity.Movimiento
import cr.ac.una.controlfinancierocamera.controller.MovimientoController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class EditControlFinancieroFragment : Fragment() {
    private lateinit var tipoGastoEditText: EditText
    private lateinit var montoEditText: EditText
    private lateinit var fechaEditText: EditText
    private lateinit var guardarButton: Button
    private val movimientoController = MovimientoController()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tipoGastoEditText = view.findViewById(R.id.tipoGastoEditText)
        montoEditText = view.findViewById(R.id.montoEditText)
        fechaEditText = view.findViewById(R.id.fechaEditText)
        guardarButton = view.findViewById(R.id.guardarButton)

        // Obtener datos del API
        val uuid = arguments?.getString("uuid")
        GlobalScope.launch(Dispatchers.Main) {
            val transaction = movimientoController.getMovimiento(uuid)
            tipoGastoEditText.setText(transaction.tipo)
            montoEditText.setText(transaction.monto)
            fechaEditText.setText(transaction.fecha)
        }

        //
        ////FECHA/////
        val fechaEditText = view.findViewById<EditText>(R.id.fechaEditText)

        // Set onClickListener to show date picker
        fechaEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(Calendar.YEAR, year)
                selectedDate.set(Calendar.MONTH, monthOfYear)
                selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val dateFormat = java.text.SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                fechaEditText.setText(dateFormat.format(selectedDate.time))
            }, year, month, dayOfMonth)

            datePickerDialog.show()
        }
        //

        guardarButton.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                val transaction = movimientoController.getMovimiento(uuid)
                transaction.tipo = tipoGastoEditText.text.toString()
                transaction.monto = montoEditText.text.toString()
                transaction.fecha = fechaEditText.text.toString()

                movimientoController.updateMovimiento(transaction)

                // Notificar al fragmento anterior (TransactionFragment) que los datos han sido actualizados
                parentFragmentManager.popBackStack()
            }
        }

    }
}
