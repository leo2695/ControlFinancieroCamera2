package cr.ac.una.controlfinancierocameraleoandarturo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import cr.ac.una.controlfinancierocameraleoandarturo.R
import cr.ac.una.controlfinancierocameraleoandarturo.entity.Lugar
import com.bumptech.glide.Glide

class BuscadorAdapter(
    context: Context,
    lugares: List<Lugar>,
    private val listener: OnItemClickListener
) : ArrayAdapter<Lugar>(context, 0, lugares) {

    interface OnItemClickListener {
        fun onItemClick(lugar: Lugar)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_busqueda, parent, false)

        val title = view.findViewById<TextView>(R.id.titleView)
        val extract = view.findViewById<TextView>(R.id.extractView)
        val imageView = view.findViewById<ImageView>(R.id.image_view)

        val lugar = getItem(position)

        title.text = lugar?.nombreLugar ?: "Sin título"
        val extractText = lugar?.nombreArticuloWikipedia ?: "Sin descripción"
        extract.text = if (extractText.length > 300) extractText.substring(0, 300) + "..." else extractText

        view.setOnClickListener {
            lugar?.let { it1 -> listener.onItemClick(it1) }
        }

        return view
    }
}
