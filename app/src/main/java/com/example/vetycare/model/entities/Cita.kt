package com.example.vetycare.model.entities

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
    val clinica: Clinica? = Clinica(),
    val mascota: Mascota? = Mascota(),
    val veterinario: Veterinario? = Veterinario()
) {
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
        Clinica(),
        Mascota(),
        Veterinario()
    )
}