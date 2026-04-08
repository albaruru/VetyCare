package com.example.vetycare.model.entities

import com.example.vetycare.model.relational.Coordenadas
import java.io.Serializable

data class Clinica(
    var id: String? = null, // Se usa con 'var' para poder asignarlo al leer
    val activa: Boolean? = null, // Cada vez que se elimina una clinica se pone en FALSE
    val codigoPostal: Int? = null,
    val comunidadAutonoma: String? = null,
    val coordenadas: Coordenadas? = null,
    val direccion: String? = null,
    val nombre: String? = null,
    val provincia: String? = null,
    val telefono: Long? = null,
    val url: String? = null
) : Serializable {
    constructor() : this(
        "",
        false,
        0,
        "",
        null,
        "",
        "",
        "",
        0L,
        ""
    )
}

