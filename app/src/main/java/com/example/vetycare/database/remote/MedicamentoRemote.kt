package com.example.vetycare.database.remote

import com.example.vetycare.model.entities.Medicamento
import com.google.firebase.database.DatabaseReference

class MedicamentoRemote (private val databaseReference: DatabaseReference) {

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

    fun obtenerTodosLosMedicamentos(
        onSuccess: (List<Medicamento>) -> Unit,
        onError: (String?) -> Unit
        ) {
        databaseReference.child("medicamentos").get()
            .addOnSuccessListener { snapshot ->
                val listaMedicamentos = mutableListOf<Medicamento>()

                for (child in snapshot.children) {
                    val medicamento = child.getValue(Medicamento::class.java)
                    if (medicamento != null) {
                        medicamento.id = child.key
                        listaMedicamentos.add(medicamento)
                    }
                }

                onSuccess(listaMedicamentos)
            }
            .addOnFailureListener {
                onError("ERROR al obtener los medicamentos")
            }
    }

    fun generarIdMedicamento(
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
        ) {
        databaseReference.child("medicamentos").get()
            .addOnSuccessListener { snapshot ->
                var maxNumero = 0

                for (child in snapshot.children) {
                    val id = child.key ?: continue
                    val partes = id.split("_")

                    if (partes.size == 2) {
                        val numero = partes[1].toIntOrNull()
                        if (numero != null && numero > maxNumero) {
                            maxNumero = numero
                        }
                    }
                }

                val nuevoNumero = maxNumero + 1
                val nuevoId = "med_" + String.format("%03d", nuevoNumero)

                onSuccess(nuevoId)
            }
            .addOnFailureListener {
                onError("ERROR al generar ID del medicamento")
            }
    }

    fun registrarMedicamento(
        idMedicamento: String,
        medicamento: Medicamento,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
        ) {
        databaseReference.child("medicamentos").child(idMedicamento).setValue(medicamento)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onError("ERROR al registrar el medicamento")
            }
    }

    fun actualizarMedicamento(
        idMedicamento: String,
        updates: Map<String, Any?>,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
        ) {
        databaseReference.child("medicamentos").child(idMedicamento).updateChildren(updates)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onError("ERROR al actualizar el medicamento")
            }
    }
}