package com.example.vetycare.database.repository

import com.example.vetycare.database.remote.MedicamentoRemote
import com.example.vetycare.model.entities.Medicamento

class MedicamentoRepository (private val remoteMedicamento: MedicamentoRemote) {

    /* EXPLICACIÓN DEL METODO <obtenerMedicamentoPorId()> : despliega para leer...
        El metodo obtenerMedicamentoPorId busca un medicamento concreto usando el idMedicamento recibido.
        Para ello llama al metodo obtenerMedicamentoPorId de remoteMedicamento, delegando en él la lectura real de la base de datos.
        Si el medicamento existe, lo devuelve mediante success.
        Si no se encuentra ningún medicamento, devuelve un mensaje de error; si falla la lectura, comunica el error mediante error.
    */
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

    /* FIXME: BORRAR => MÉTODO NO UTILIZADO
    fun obtenerTodosLosMedicamentos(
        success: (List<Medicamento>) -> Unit,
        error: (String?) -> Unit
        ) {
        remoteMedicamento.obtenerTodosLosMedicamentos(success, error)
    }
    */
    /* FIXME: BORRAR => MÉTODO NO UTILIZADO
    fun generarIdMedicamento(
        success: (String) -> Unit,
        error: (String) -> Unit
        ) {
        remoteMedicamento.generarIdMedicamento(success, error)
    }
    */
    /* FIXME: BORRAR => MÉTODO NO UTILIZADO
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
    */
    /* FIXME: BORRAR => MÉTODO NO UTILIZADO
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
    */

}