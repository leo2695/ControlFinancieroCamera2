package cr.ac.menufragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import cr.ac.una.controlfinancierocameraleoandarturo.R
import cr.ac.una.controlfinancierocameraleoandarturo.WebViewActivity
import cr.ac.una.controlfinancierocameraleoandarturo.adapter.BuscadorAdapter
import cr.ac.una.controlfinancierocameraleoandarturo.db.AppDatabase
import cr.ac.una.controlfinancierocameraleoandarturo.entity.Lugar
import cr.ac.una.controlfinancierocameraleoandarturo.dao.LugarDAO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListaLugaresFragment : Fragment(), BuscadorAdapter.OnItemClickListener {

    private lateinit var buscadorAdapter: BuscadorAdapter
    private lateinit var lugarDao: LugarDAO

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_lista_lugares, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lugarDao = AppDatabase.getInstance(requireContext()).lugarDao()

        val listView: ListView = view.findViewById(R.id.listaLugares)
        buscadorAdapter = BuscadorAdapter(requireContext(), mutableListOf(), this)
        listView.adapter = buscadorAdapter

        // Cargar la lista de lugares almacenados
        cargarLugares()
    }

    private fun cargarLugares() {
        lifecycleScope.launch {
            try {
                val lugares = withContext(Dispatchers.IO) {
                    lugarDao.getAllLugares()
                }
                // Actualizar el adaptador con la lista de lugares obtenidos
                withContext(Dispatchers.Main) {
                    buscadorAdapter.clear()
                    buscadorAdapter.addAll(lugares)
                }
            } catch (e: Exception) {
                Log.e("ERROR", "Error al cargar lugares: ${e.message}")
                // Manejar el error, por ejemplo, mostrar un Toast
            }
        }
    }

    override fun onItemClick(lugar: Lugar) {
        val url = "https://es.wikipedia.org/wiki/${lugar.nombreArticuloWikipedia}"
        val intent = Intent(requireContext(), WebViewActivity::class.java).apply {
            putExtra("url", url)
        }
        startActivity(intent)
    }
}
