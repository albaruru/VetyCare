package com.example.vetycare.model.entities

data class Tratamiento(
    var id: String? = null, // Se usa con 'var' para poder asignarlo al leer
    val estado: String? = null,
    val fechaFin: String? = null,
    val fechaInicio: String? = null,
    val idDiagnostico: String? = null,
    val idMascota: String? = null,
    val objetivoTerapeutico: String? = null,
    val observaciones: String? = null,
    val tipoTratamiento: String? = null // TODO: ES EL QUE COGEMOS PARA DIAGNOSTICO Y PARA TRATAMIENTO
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
        ""
    )
}