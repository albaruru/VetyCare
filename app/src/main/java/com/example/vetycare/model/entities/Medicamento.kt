package com.example.vetycare.model.entities

import java.io.Serializable

/*
    EXPLICACION
        Modelo de datos que representa un medicamento, incluyendo su composición,
        presentación, disponibilidad y requisitos de prescripción.
 */

data class Medicamento(
    var id: String? = null, // Se usa con 'var' para poder asignarlo al leer
    val concentracion: String? = null,
    val disponible: Boolean? = null,
    val fechaCreacion: String? = null,
    val formaFarmaceutica: String? = null,
    val laboratorio: String? = null,
    val nombreComercial: String? = null, // TODO: ES EL QUE COGEMOS PARA DIAGNOSTICO
    val principioActivo: String? = null,
    val requiereReceta: Boolean? = null
) : Serializable {
    constructor() : this(
        "",
        "",
        false,
        "",
        "",
        "",
        "",
        "",
        false
    )
}