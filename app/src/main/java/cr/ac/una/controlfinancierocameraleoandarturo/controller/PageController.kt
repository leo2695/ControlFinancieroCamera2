package cr.ac.una.controlfinancierocameraleoandarturo.controller

import cr.ac.una.controlfinancierocameraleoandarturo.clases.page
import cr.ac.una.controlfinancierocameraleoandarturo.service.PagesService

class PageController {
    private val pagesService = PagesService()

    suspend fun Buscar(terminoBusqueda: String, limit: Int): List<page> {
        return pagesService.apiWikiService.Buscar(terminoBusqueda, limit).pages ?: emptyList()
    }
}