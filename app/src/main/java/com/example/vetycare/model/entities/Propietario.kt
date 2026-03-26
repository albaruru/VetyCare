package com.example.vetycare.model.entities

data class Propietario(
    var id: String? = null, // Se usa con 'var' para poder asignarlo al leer
    val activo: Boolean? = null,
    val apellido: String? = null,
    val authUid: String? = null,
    val dni: String? = null,
    val email: String? = null,
    val fechaCreacion: Long? = null,
    val fechaNacimiento: String? = null,
    val nombre: String? = null,
    val telefono: Long? = null,
    val urlFotoProp: String? = null
) {
    constructor() : this(
        "",false, "", "",
        "", "", 0L, "",
        "", 0L,""
    )
}