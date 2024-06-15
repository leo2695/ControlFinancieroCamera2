package cr.ac.una.controlfinancierocameraleoandarturo.clases

import java.io.Serializable

data class Thumbnail(
    val url: String?,
    val width: Int?,
    val height: Int?
): Serializable