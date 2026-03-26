package com.example.vetycare.model.entities

data class Cita(
    var id: String? = null, // FIXME: ¿ID HAY QUE PONERLO, LO HE PUESTO EN TODOS COMO VAR?
    val estadoCita: String? = null,
    val fechaCreacion: Long? = null,
    val fechaHoraFin: String? = null,
    val fechaHoraInicio: String? = null,
    val idClinica: String? = null,
    val idMascota: String? = null,
    val idPropietario: String? = null,
    val idVeterinario: String? = null,
    val motivoConsulta: String? = null,
    val observaciones: String? = null,
    val tipoCita: String? = null,
    val resumenMascota: ResumenMascota? = ResumenMascota(), // Objeto anidado en JSON
    val resumenVeterinario: ResumenVeterinario? = ResumenVeterinario() // Objeto anidado en JSON
) {
    constructor() : this(
        "","", 0L, "",
        "", "", "", "",
        "", "", "", "",
        ResumenMascota(), ResumenVeterinario()
    )
}

data class ResumenMascota(
    val especie: String? = "",
    val nombre: String? = ""
)

data class ResumenVeterinario(
    val apellido: String? = "",
    val nombre: String? = ""
)