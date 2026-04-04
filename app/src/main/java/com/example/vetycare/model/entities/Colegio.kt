package com.example.vetycare.model.entities

data class Colegio(
    var id: String? = null, // Se usa con 'var' para poder asignarlo al leer
    val activa: Boolean? = null, // Cada vez que se elimina un colegio se pone en FALSE
    val comunidadAutonoma: String? = null,
    val correo: String? = null,
    val direccion: String? = null,
    val fechaCreacion: String? = null,
    val nombre: String? = null,
    val provincia: String? = null,
    val telefono: Long? = null
) {
    constructor() : this(
        "",
        false,
        "",
        "",
        "",
        "",
        "",
        "",
        0L
    )
}