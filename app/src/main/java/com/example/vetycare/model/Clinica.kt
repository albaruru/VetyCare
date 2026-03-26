package com.example.vetycare.model

data class Clinica(
    val activa: Boolean = false,
    val codigoPostal: Int = 0,
    val comunidadAutonoma: String = "",
    val direccion: String = "",
    val nombre: String = "",
    val provincia: String = "",
    val telefono: Long = 0L,
    val url: String = ""
) {
    constructor() : this(false, 0, "", "", "","",0L,"")
}

