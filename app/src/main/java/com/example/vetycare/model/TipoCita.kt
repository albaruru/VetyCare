package com.example.vetycare.model
import com.example.vetycare.R

enum class TipoCita(val colorRes: Int) {
    VACUNACION(R.color.Vacunacion),
    REVISION(R.color.Revision),
    CONSULTA(R.color.Consulta),
    PRUEBAS(R.color.Pruebas),
    MEDICAMENTOS(R.color.Medicamentos)
}