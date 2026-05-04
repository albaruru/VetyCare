package com.example.vetycare.model.entities

import java.io.Serializable

/*
    EXPLICACION:
        Entidad que representa un propietario dentro del sistema.
        Contiene información personal y de contacto como nombre, apellidos, email,
        teléfono, DNI y datos de registro.
        Se utiliza como modelo de datos para la gestión de usuarios propietarios en la aplicación.
 */
data class Propietario(
    var id: String? = null, // Se usa con 'var' para poder asignarlo al leer
    val activa: Boolean? = null,
    val apellido: String? = null,
    val authUid: String? = null,
    val dni: String? = null,
    val email: String? = null,
    val fechaRegistro: String? = null,
    val fechaNacimiento: String? = null,
    val nombre: String? = null,
    val telefono: Long? = null,
    val sexo: String? = null,
    val urlFotoProp: String? = null
): Serializable {
    constructor() : this(
        "",
        false,
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        0L,
        "",
        ""
    )
}