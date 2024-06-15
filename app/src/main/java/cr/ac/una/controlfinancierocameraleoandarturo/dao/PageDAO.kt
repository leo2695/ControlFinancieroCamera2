package cr.ac.una.controlfinancierocameraleoandarturo.dao

import cr.ac.una.controlfinancierocameraleoandarturo.clases.pages
import retrofit2.http.GET
import retrofit2.http.Query

interface PageDAO {
        @GET("search/page")
        suspend fun Buscar(
                @Query("q") query: String,
                @Query("limit") limit: Int
        ): pages
}