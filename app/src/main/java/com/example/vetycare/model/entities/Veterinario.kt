package com.example.vetycare.model.entities

data class Veterinario(
    var id: String? = null, // Se usa con 'var' para poder asignarlo al leer
    val activo: Boolean? = null,
    val apellido: String? = null,
    val authUid: String? = null,
    val correo: String? = null,
    val especialidad: String? = null,
    val fechaCreacion: Long? = null,
    val idClinica: String? = null,
    val idColegio: String? = null,
    val nombre: String? = null,
    val numeroColegiado: String? = null,
    val telefono: Long? = null
) {
    constructor() : this(
        "",false, "", "",
        "", "", 0L, "",
        "", "", "", 0L
    )
}