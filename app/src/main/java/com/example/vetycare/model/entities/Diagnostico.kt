package com.example.vetycare.model.entities

import java.io.Serializable

/*
    EXPLICACION
        Modelo de datos que representa un diagnóstico clínico, incluyendo su información básica
        y sus relaciones con cita, mascota, patología y tratamiento.
 */

data class Diagnostico(
    var id: String? = null,
    val estado: String? = null,
    val fechaDiagnostico: String? = null,
    val fechaResolucion: String? = null,
    val gradoClinico: String? = null,
    val idCita: String? = null,
    val idMascota: String? = null,
    val idPatologia: String? = null,
    val idTratamiento: String? = null,
    val importeTotal: Double? = null,
    val valoracion: String? = null,
    val patologia: Patologia? = Patologia(),
    val cita: Cita? = Cita(),
    val tratamiento: Tratamiento? = Tratamiento(),
    val medicamento: Medicamento? = Medicamento()
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
        0.0,
        "",
        Patologia(),
        Cita(),
        Tratamiento(),
        Medicamento()
    )
}
