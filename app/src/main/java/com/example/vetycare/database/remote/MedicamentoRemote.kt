package com.example.vetycare.database.remote

import com.example.vetycare.model.entities.Medicamento
import com.google.firebase.database.DatabaseReference

class MedicamentoRemote (private val databaseReference: DatabaseReference) {

    /* EXPLICACIÓN DEL METODO <obtenerMedicamentoPorId()> : despliega para leer...
        El metodo obtenerMedicamentoPorId busca un medicamento concreto en la base de datos usando el idMedicamento recibido.
        Accede al nodo "medicamentos" y selecciona el registro que coincide con ese id.
        Si la lectura se realiza correctamente, convierte los datos obtenidos en un objeto de tipo Medicamento.
        Después asigna al medicamento su id usando la clave del nodo y lo devuelve mediante onSuccess;
        si falla la lectura, devuelve un mensaje con onError.
    */
    fun obtenerMedicamentoPorId(
        idMedicamento: String,
        onSuccess: (Medicamento?) -> Unit,
        onError: (String?) -> Unit
        ) {
        databaseReference.child("medicamentos").child(idMedicamento).get()
            .addOnSuccessListener { snapshot ->
                val medicamento = snapshot.getValue(Medicamento::class.java)

                if (medicamento != null) {
                    medicamento.id = snapshot.key
                }

                onSuccess(medicamento)
            }
            .addOnFailureListener {
                onError("ERROR al leer el medicamento")
            }
    }

}