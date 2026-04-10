package com.example.vetycare.database.remote

import com.example.vetycare.model.entities.Tratamiento
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference

class TratamientoRemote (private val databaseReference: DatabaseReference) {

    fun obtenerTratamientoPorId(
        idTratamiento: String,
        onSuccess: (Tratamiento?) -> Unit,
        onError: (String?) -> Unit
        ) {
        databaseReference.child("tratamientos").child(idTratamiento).get()
            .addOnSuccessListener { snapshot ->
                val tratamiento = snapshot.getValue(Tratamiento::class.java)
                onSuccess(tratamiento)
            }
            .addOnFailureListener {
                onError("ERROR al leer el tratamiento")
            }
    }

    fun obtenerIdsTratamientosPorMascota(
        idMascota: String,
        onSuccess: (List<String>) -> Unit,
        onError: (String?) -> Unit
        ) {
        databaseReference.child("tratamientosPorMascota").child(idMascota).get()
            .addOnSuccessListener { snapshot ->
                val listaIds = mutableListOf<String>()

                for (child in snapshot.children) {
                    val esValido = child.getValue(Boolean::class.java) ?: false
                    if (esValido) {
                        child.key?.let { listaIds.add(it) }
                    }
                }

                onSuccess(listaIds)
            }
            .addOnFailureListener {
                onError("ERROR al buscar tratamientos por mascota")
            }
    }

    fun registrarTratamiento(
        idTratamiento: String,
        tratamiento: Tratamiento,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
        ) {
        databaseReference.child("tratamientos").child(idTratamiento).setValue(tratamiento)
            .addOnSuccessListener {
                databaseReference.child("tratamientosPorMascota")
                    .child(tratamiento.idMascota.toString())
                    .child(idTratamiento)
                    .setValue(true)
                    .addOnSuccessListener {
                        onSuccess()
                    }
                    .addOnFailureListener {
                        onError("Tratamiento creado, pero error al guardar índice por mascota")
                    }
            }
            .addOnFailureListener {
                onError("ERROR al crear el tratamiento")
            }
    }

    fun actualizarTratamiento(
        idTratamiento: String,
        updates: Map<String, Any?>,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
        ) {
        databaseReference.child("tratamientos").child(idTratamiento).updateChildren(updates)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError("ERROR al actualizar el tratamiento") }
    }

    fun obtenerMedicamentosPorTratamiento(
        idTratamiento: String,
        onSuccess: (DataSnapshot) -> Unit,
        onError: (String?) -> Unit
        ) {
        databaseReference.child("medicamentosPorTratamiento").child(idTratamiento).get()
            .addOnSuccessListener { snapshot ->
                onSuccess(snapshot)
            }
            .addOnFailureListener {
                onError("ERROR al leer medicamentos por tratamiento")
            }
    }

    fun guardarMedicamentoEnTratamiento(
        idTratamiento: String,
        idMedicamento: String,
        datosMedicamento: Map<String, Any?>,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
        ) {
        databaseReference.child("medicamentosPorTratamiento")
            .child(idTratamiento)
            .child(idMedicamento)
            .setValue(datosMedicamento)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener {
                onError("ERROR al guardar medicamento en el tratamiento")
            }
    }

    fun generarIdTratamiento(
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
        ) {
        databaseReference.child("tratamientos").get()
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
                val nuevoId = "trat_" + String.format("%03d", nuevoNumero)

                onSuccess(nuevoId)
            }
            .addOnFailureListener {
                onError("ERROR al generar ID del tratamiento")
            }
    }
}