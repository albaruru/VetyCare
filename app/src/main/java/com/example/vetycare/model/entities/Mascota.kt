package com.example.vetycare.model.entities

import java.io.Serializable

/*
    EXPLICACION
        Modelo de datos que representa una mascota, incluyendo sus datos básicos,
        características y relación con su propietario.
 */

data class Mascota(
    var id: String? = null, // Se usa con 'var' para poder asignarlo al leer
    val activa: Boolean? = null,
    val castracion: Boolean? = null,
    val especie: String? = null,
    val fechaRegistro: String? = null,
    val fechaNacimiento: String? = null,
    val idPropietario: String? = null,
    val microchip: String? = null,
    val nombre: String? = null,
    val pesoActual: Double? = null,
    val raza: String? = null,
    val sexo: String? = null,
    val urlFotoMasc: String? = null
) : Serializable {
    constructor() : this(
        "",
        false,
        false,
        "",
        "",
        "",
        "",
        "",
        "",
        0.0,
        "",
        "",
        ""
    )
}