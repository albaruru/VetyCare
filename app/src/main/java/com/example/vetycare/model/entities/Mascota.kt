package com.example.vetycare.model.entities

data class Mascota(
    var id: String? = null, // Se usa con 'var' para poder asignarlo al leer
    val activa: Boolean? = null,
    val castracion: Boolean? = null,
    val especie: String? = null,
    val fechaCreacion: Long? = null,
    val fechaNacimiento: String? = null,
    val idPropietario: String? = null,
    val microchip: String? = null,
    val nombre: String? = null,
    val pasoActual: Double? = null,
    val raza: String? = null,
    val sexo: String? = null,
    val urlFotoMasc: String? = null
) {
    constructor() : this(
        "",false, false, "",
        0L, "", "", "",
        "", 0.0, "", "",""
    )
}