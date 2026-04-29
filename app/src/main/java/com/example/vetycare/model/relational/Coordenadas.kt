package com.example.vetycare.model.relational

import java.io.Serializable

/*
    EXPLICACION
        Modelo relacional que representa la información básica del veterinario asociado a una cita.
        Incluye datos simplificados como nombre y apellido.
 */

data class Coordenadas(
    val latitud: Double? = null,
    val longitud: Double? = null
) : Serializable