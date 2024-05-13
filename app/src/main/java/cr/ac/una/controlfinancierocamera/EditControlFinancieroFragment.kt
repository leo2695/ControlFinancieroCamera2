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
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import cr.ac.menufragment.ListControlFinancieroFragment
import cr.ac.una.controlfinancierocamera.controller.MovimientoController
import cr.ac.una.controlfinancierocamera.db.AppDatabase
import cr.ac.una.controlfinancierocamera.entity.Movimiento
import cr.ac.una.jsoncrud.dao.MovimientoDAO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditControlFinancieroFragment : Fragment() {
    private lateinit var tipoGastoEditText: EditText
    private lateinit var montoEditText: EditText
    private lateinit var fechaEditText: EditText
    private lateinit var guardarButton: Button
    val movimientoController = MovimientoController()
    lateinit var transaction: Movimiento

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_control_financiero, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tipoGastoEditText = view.findViewById(R.id.tipoGastoEditText)
        montoEditText = view.findViewById(R.id.montoEditText)
        fechaEditText = view.findViewById(R.id.fechaEditText)
        guardarButton = view.findViewById(R.id.guardarButton)

        lateinit var movimientoDao: MovimientoDAO
        movimientoDao = AppDatabase.getInstance(requireContext()).ubicacionDao()
        // Aquí obtienes los datos de la transacción seleccionada
        transaction = arguments?.getSerializable("movimiento") as Movimiento

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

        // Ahora establece los valores de los EditText con los datos de la transacción seleccionada
        transaction.let {
            tipoGastoEditText.setText(it.tipo)
            montoEditText.setText(it.monto)
            fechaEditText.setText(it.fecha)
        }

        guardarButton.setOnClickListener {
            // Aquí obtienes los nuevos valores ingresados por el usuario
            val tipoGasto = tipoGastoEditText.text.toString()
            val monto = montoEditText.text.toString()
            val fecha = fechaEditText.text.toString()
            val uuid = transaction.id
            Log.d("UUID", "UUID del movimiento: $uuid")

            // Actualizar los datos en el API
            val updatedTransaction = Movimiento(uuid, tipoGasto, monto, fecha)
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    //movimientoController.updateMovimiento(updatedTransaction)
                    movimientoDao.update(updatedTransaction)
                    Log.d("Uptate Info", "Movimiento: $updatedTransaction")

                    // Mostrar una notificación de éxito
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Transacción actualizada", Toast.LENGTH_SHORT).show()
                    }
                    // Obtener el FragmentManager y realizar la transacción para volver al ListControlFinancieroFragment
                    withContext(Dispatchers.Main) {
                        requireActivity().supportFragmentManager.beginTransaction().apply {
                            replace(R.id.home_content, ListControlFinancieroFragment())
                            addToBackStack(null)  // Agrega este fragmento a la pila de retroceso
                            commit()
                        }
                    }
                } catch (e: Exception) {
                    // Mostrar una notificación de error si ocurre algún problema
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Error al actualizar", Toast.LENGTH_SHORT).show()
                    }
                    Log.e("EditControlFinanciero", "Error al actualizar la transacción", e)
                }
            }

            // Notifica al fragmento anterior (TransactionFragment) que los datos han sido actualizados
            //parentFragmentManager.popBackStack()
        }
    }
}
