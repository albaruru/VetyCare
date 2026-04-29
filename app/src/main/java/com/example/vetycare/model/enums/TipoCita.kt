package com.example.vetycare.model.enums
import com.example.vetycare.R

/*
    EXPLICACION
        Enum que representa los tipos de cita disponibles en el sistema.
        Cada tipo está asociado a un color para su representación en la interfaz.
 */

enum class TipoCita(val colorRes: Int) {
    VACUNACION(R.color.Vacunacion),
    REVISION(R.color.Revision),
    CONSULTA(R.color.Consulta),
    PRUEBAS(R.color.Pruebas),
    MEDICAMENTOS(R.color.Medicamentos)
}