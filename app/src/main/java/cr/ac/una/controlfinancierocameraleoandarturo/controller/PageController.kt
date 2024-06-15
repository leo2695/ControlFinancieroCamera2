package cr.ac.una.controlfinancierocameraleoandarturo.controller

import cr.ac.una.controlfinancierocameraleoandarturo.clases.Page
import cr.ac.una.controlfinancierocameraleoandarturo.service.PagesService

class PageController {
    private val pagesService = PagesService()

    suspend fun Buscar(terminoBusqueda: String, limit: Int): List<Page> {
        return pagesService.apiWikiService.Buscar(terminoBusqueda, limit).pages ?: emptyList()
    }
}