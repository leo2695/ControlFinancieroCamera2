package cr.ac.una.controlfinancierocameraleoandarturo.service

import cr.ac.una.controlfinancierocameraleoandarturo.dao.PageDAO
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PagesService {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://es.wikipedia.org/w/rest.php/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiWikiService = retrofit.create(PageDAO::class.java)
}