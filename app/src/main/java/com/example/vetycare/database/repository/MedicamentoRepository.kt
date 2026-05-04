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

}