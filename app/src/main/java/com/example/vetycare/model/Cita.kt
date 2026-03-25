package com.example.vetycare.model

data class Cita(
    val id: String = "",
    val idMascota: String = "",
    val idClinica: String = "",
    val tipoCita: String = "",
    val motivoConsulta: String = "",
    val idVeterinario: String = "",
    val fechaHoraInicio: String = "",
    val fechaHoraFin: String = "",
    val estadoCita: String = "",
    val observaciones: String = "",
    val idPropietario: String = ""
) {
    constructor() : this("", "", "", "", "", "", "", "", "", "", "")
}