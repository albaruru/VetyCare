package com.example.vetycare.model.relational

import java.io.Serializable

/*
    EXPLICACION
        Modelo relacional que representa la relación entre un tratamiento y un medicamento.
        Contiene información detallada de la pauta de administración como dosis, frecuencia,
        duración y vía de administración.
 */

data class MedicamentoPorTratamiento(
    var idTratamiento: String? = null,    // Es la llave 'trat_001'
    var idMedicamento: String? = null,    // Es la llave 'med_001'
    val dosis: String? = null,
    val duracion: String? = null,
    val fechaFin: String? = null,
    val fechaInicio: String? = null,
    val frecuencia: String? = null,
    val indicaciones: String? = null,
    val viaAdministracion: String? = null
) : Serializable {
    constructor() : this(
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