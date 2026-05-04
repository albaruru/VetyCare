package com.example.vetycare.model.entities

import com.example.vetycare.model.relational.CitaClinica
import com.example.vetycare.model.relational.CitaMascota
import com.example.vetycare.model.relational.CitaVeterinario
import java.io.Serializable

/*
    EXPLICACION
        Modelo de datos que representa una cita en el sistema, incluyendo su información básica
        y las relaciones con mascota, clínica y veterinario.
 */
data class Cita(
    var id: String? = null,
    val estadoCita: String? = null,
    val fechaCreacion: String? = null,
    val fechaHoraFin: String? = null,
    val fechaHoraInicio: String? = null,
    val idClinica: String? = null,
    val idMascota: String? = null,
    val idPropietario: String? = null,
    val idVeterinario: String? = null,
    val motivoConsulta: String? = null, // TODO: ES EL QUE COGEMOS PARA DIAGNOSTICO
    val observaciones: String? = null,
    val tipoCita: String? = null,
    var clinica: CitaClinica? = null,
    var veterinario: CitaVeterinario? = null,
    var mascota: CitaMascota? = null
) : Serializable {
    constructor() : this(
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
    )
}