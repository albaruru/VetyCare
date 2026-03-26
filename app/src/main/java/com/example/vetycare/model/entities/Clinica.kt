package com.example.vetycare.model.entities

data class Clinica(
    var id: String? = null, // Se usa con 'var' para poder asignarlo al leer
    val activa: Boolean? = null,
    val codigoPostal: Int? = null,
    val comunidadAutonoma: String? = null,
    val direccion: String? = null,
    val nombre: String? = null,
    val provincia: String? = null,
    val telefono: Long? = null,
    val url: String? = null
) {
    constructor() : this(
        "",false, 0, "",
        "", "","",0L,""
    )
}

