package com.example.vetycare.model.entities

import java.io.Serializable

data class Patologia(
    var id: String? = null, // Se usa con 'var' para poder asignarlo al leer
    val activa: Boolean? = null,
    val categoriaPatologica: String? = null,
    val codigoClinico: String? = null,
    val descripcion: String? = null,
    val esCronica: Boolean? = null,
    val esZoonotica: Boolean? = null,
    val nivelGravedad: Int? = null,
    val nombre: String? = null,
) : Serializable {
    constructor() : this(
        "",
        false,
        "",
        "",
        "",
        false,
        false,
        0,
        ""
    )
}
