package com.example.vetycare.model.entities

data class Colegio(
    var id: String? = null, // Se usa con 'var' para poder asignarlo al leer
    val activo: Boolean? = null,
    val comunidadAutonoma: String? = null,
    val correo: String? = null,
    val direccion: String? = null,
    val fechaCreacion: Long? = null,
    val nombre: String? = null,
    val provincia: String? = null,
    val telefono: Long? = null
) {
    constructor() : this(
        "",false, "", "",
        "", 0L, "", "", 0L
    )
}