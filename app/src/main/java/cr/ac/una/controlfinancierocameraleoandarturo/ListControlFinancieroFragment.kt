package cr.ac.menufragment

import android.content.Intent
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import cr.ac.una.controlfinancierocameraleoandarturo.R
import cr.ac.una.controlfinancierocameraleoandarturo.WebViewActivity
import cr.ac.una.controlfinancierocameraleoandarturo.adapter.BuscadorAdapter
import cr.ac.una.controlfinancierocameraleoandarturo.clases.page
import cr.ac.una.controlfinancierocameraleoandarturo.controller.PageController
import cr.ac.una.controlfinancierocameraleoandarturo.db.AppDatabase
import cr.ac.una.controlfinancierocameraleoandarturo.entity.Lugar
import cr.ac.una.controlfinancierocameraleoandarturo.dao.LugarDAO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class ListControlFinancieroFragment : Fragment(), BuscadorAdapter.OnItemClickListener {

    private lateinit var buscadorAdapter: BuscadorAdapter
    private val pageController = PageController()
    private lateinit var botonBuscar: Button
    private lateinit var buscadorView: SearchView
    private lateinit var lugarDao: LugarDAO

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list_control_financiero, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lugarDao = AppDatabase.getInstance(requireContext()).lugarDao()

        botonBuscar = view.findViewById(R.id.botonIngresar)
        buscadorView = view.findViewById(R.id.buscadorView)

        botonBuscar.setOnClickListener {
            var textoBusqueda = buscadorView.query.toString()
            textoBusqueda = textoBusqueda.replace(" ", "_")
            Log.d("TextoBusqueda", textoBusqueda)
            insertEntity(textoBusqueda)
        }

        val listView: ListView = view.findViewById(R.id.listaMovimientos)
        buscadorAdapter = BuscadorAdapter(requireContext(), mutableListOf(), this)
        listView.adapter = buscadorAdapter

        // Manejar búsqueda desde los argumentos
        val searchQuery = arguments?.getString("search_query")
        if (searchQuery != null) {
            buscadorView.setQuery(searchQuery, false) // Establecer el texto en la barra de búsqueda
            insertEntity(searchQuery)
        }
    }

    private fun insertEntity(textoBusqueda: String) {
        lifecycleScope.launch {
            try {
                val resultadoBusqueda = withContext(Dispatchers.IO) {
                    pageController.Buscar(textoBusqueda)
                }
                // Transformar los resultados en instancias de Lugar y guardar en la base de datos
                val lugares = resultadoBusqueda.map { page ->
                    Lugar(
                        coordenadas = "",
                        fechaHora = Date(),
                        nombreArticuloWikipedia = page.title,
                        nombreLugar = page.extract
                    )
                }
                lugarDao.insertAllLugares(lugares) // Asumiendo que tienes una función insertAllLugares

                // Actualizar el adaptador con los resultados de búsqueda
                withContext(Dispatchers.Main) {
                    buscadorAdapter.clear()
                    buscadorAdapter.addAll(lugares)
                }
            } catch (e: Exception) {
                Log.e("ERROR", "Error: ${e.message}")
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
