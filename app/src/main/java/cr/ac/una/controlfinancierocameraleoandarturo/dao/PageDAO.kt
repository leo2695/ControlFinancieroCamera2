package cr.ac.una.controlfinancierocameraleoandarturo.dao

import cr.ac.una.controlfinancierocameraleoandarturo.clases.pages
import retrofit2.http.GET
import retrofit2.http.Path

interface PageDAO {
        @GET("page/related/{title}")
        suspend fun Buscar(@Path("title") title: String): pages
}