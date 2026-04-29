package com.example.vetycare.model.entities

import java.io.Serializable
/*
    EXPLICACION:
        Modelo de datos que representa un veterinario, incluyendo información personal,
        profesional y su relación con clínica y colegio.
 */
data class Veterinario(
    var id: String? = null, // Se usa con 'var' para poder asignarlo al leer
    val activa: Boolean? = null,
    val apellido: String? = null,
    val authUid: String? = null,
    val correo: String? = null,
    val especialidad: String? = null,
    val idClinica: String? = null,
    val idColegio: String? = null,
    val nombre: String? = null,
    val numeroColegiado: String? = null,
    val telefono: Long? = null
) : Serializable {
    constructor() : this(
        "",
        false,
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        0L
    )
}