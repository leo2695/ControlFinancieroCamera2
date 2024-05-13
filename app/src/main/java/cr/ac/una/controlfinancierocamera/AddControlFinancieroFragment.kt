package cr.ac.una.controlfinancierocamera

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import android.app.DatePickerDialog
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import cr.ac.menufragment.ListControlFinancieroFragment
import cr.ac.una.controlfinanciero.adapter.MovimientoAdapter
import cr.ac.una.controlfinancierocamera.controller.MovimientoController
import cr.ac.una.controlfinancierocamera.entity.Movimiento
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Locale
import cr.ac.una.controlfinancierocamera.db.AppDatabase
import cr.ac.una.jsoncrud.dao.MovimientoDAO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddControlFinancieroFragment : Fragment() {
    private lateinit var fechaEditText: EditText
    private lateinit var guardarButton: Button
    private lateinit var volverButton: Button
    private lateinit var tipoGasto: TextView
    private lateinit var monto: EditText
    lateinit var adapter: MovimientoAdapter
    val movimientoController = MovimientoController()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_control_financiero, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lateinit var movimientoDao: MovimientoDAO
        movimientoDao = AppDatabase.getInstance(requireContext()).ubicacionDao()

        fechaEditText = view.findViewById(R.id.fechaEditText)
        guardarButton = view.findViewById(R.id.guardarButton)
        volverButton = view.findViewById(R.id.goBack)
        tipoGasto = view.findViewById(R.id.auto_complete)
        monto = view.findViewById(R.id.montoEditText)

        //Guardar transaccion
        guardarButton.setOnClickListener {
            // Obtener los datos ingresados por el usuario
            val tipoGasto = tipoGasto.text.toString()
            val monto = monto.text.toString()
            val fecha = fechaEditText.text.toString()

            // Crear la transacción con los datos ingresados
            val transaccion = Movimiento(null, tipoGasto, monto, fecha)

            // Lanzar una corrutina para insertar la transacción en la base de datos
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    // Insertar la transacción en la base de datos
                    //movimientoController.insertMovimiento(transaccion)
                    movimientoDao.insert(transaccion)

                    // Mostrar una notificación de éxito en el hilo principal
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Transacción agregada", Toast.LENGTH_SHORT).show()
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
                    // Mostrar una notificación de error si ocurre algún problema en el hilo principal
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Error al agregar la transacción", Toast.LENGTH_SHORT).show()
                    }
                    Log.e("AddControlFinanciero", "Error al insertar la transacción", e)
                }
            }
        }






        val tipo = listOf("Credito", "Debito")

        val autoComplete : AutoCompleteTextView = view.findViewById(R.id.auto_complete)

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, tipo)
        autoComplete.setAdapter(adapter)

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

        autoComplete.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, id ->
            val tipoSelected = adapterView.getItemAtPosition(i)
            Toast.makeText(requireContext(), "Item: $tipoSelected", Toast.LENGTH_SHORT).show()
        }

        val agregarTransaccion = volverButton
        agregarTransaccion.setOnClickListener {
            // Crea una instancia del fragmento que deseas abrir
            val transaction_fragment = ListControlFinancieroFragment()
            val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
            transaction.replace(R.id.home_content, transaction_fragment)
            transaction.commit()
        }

    }
}