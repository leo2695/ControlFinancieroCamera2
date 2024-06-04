package cr.ac.una.controlfinancierocameraleoandarturo.controller

import cr.ac.una.controlfinancierocameraleoandarturo.clases.page
import cr.ac.una.controlfinancierocameraleoandarturo.service.PagesService

class PageController {
    var pagesService = PagesService()

    suspend fun  Buscar(terminoBusqueda: String):ArrayList<page>{
        return pagesService.apiWikiService.Buscar(terminoBusqueda).pages as ArrayList<page>
    }
}