package com.example.vetycare.database.repository

import com.example.vetycare.database.remote.MedicamentoRemote
import com.example.vetycare.model.entities.Medicamento

class MedicamentoRepository (private val remoteMedicamento: MedicamentoRemote) {

    fun obtenerMedicamentoPorId(
        idMedicamento: String,
        success: (Medicamento) -> Unit,
        error: (String?) -> Unit
        ) {
        remoteMedicamento.obtenerMedicamentoPorId(
            idMedicamento,
            { medicamento ->
                if (medicamento != null) {
                    success(medicamento)
                } else {
                    error("No se encontró el medicamento")
                }
            },
            error
        )
    }

    fun obtenerTodosLosMedicamentos(
        success: (List<Medicamento>) -> Unit,
        error: (String?) -> Unit
        ) {
        remoteMedicamento.obtenerTodosLosMedicamentos(success, error)
    }

    fun generarIdMedicamento(
        success: (String) -> Unit,
        error: (String) -> Unit
        ) {
        remoteMedicamento.generarIdMedicamento(success, error)
    }

    fun registrarMedicamento(
        idMedicamento: String,
        medicamento: Medicamento,
        success: () -> Unit,
        error: (String?) -> Unit
        ) {
        remoteMedicamento.registrarMedicamento(
            idMedicamento,
            medicamento,
            success,
            error
        )
    }

    fun actualizarMedicamento(
        idMedicamento: String,
        updates: Map<String, Any?>,
        success: () -> Unit,
        error: (String?) -> Unit
        ) {
        remoteMedicamento.actualizarMedicamento(
            idMedicamento,
            updates,
            success,
            error
        )
    }
}