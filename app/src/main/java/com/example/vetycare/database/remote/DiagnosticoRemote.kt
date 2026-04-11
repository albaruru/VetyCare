package com.example.vetycare.database.remote

import com.example.vetycare.model.entities.Diagnostico
import com.google.firebase.database.DatabaseReference

class DiagnosticoRemote (private val databaseReference: DatabaseReference){

    fun obtenerDiagnosticoPorId(
        idDiagnostico: String,
        onSuccess: (Diagnostico?) -> Unit,
        onError: (String?) -> Unit
        ) {
        databaseReference.child("diagnosticos").child(idDiagnostico).get()
            .addOnSuccessListener { snapshot ->
                val diagnostico = snapshot.getValue(Diagnostico::class.java)

                if (diagnostico != null) {
                    diagnostico.id = snapshot.key
                }

                onSuccess(diagnostico)
            }
            .addOnFailureListener {
                onError("ERROR al leer el diagnóstico")
            }
    }

    fun obtenerIdsDiagnosticosPorMascota(
        idMascota: String,
        onSuccess: (List<String>) -> Unit,
        onError: (String?) -> Unit
        ) {
        databaseReference.child("diagnosticosPorMascota").child(idMascota).get()
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
                onError("ERROR al buscar diagnósticos por mascota")
            }
    }

    fun registrarDiagnostico(
        idDiagnostico: String,
        diagnostico: Diagnostico,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
        ) {
        databaseReference.child("diagnosticos").child(idDiagnostico).setValue(diagnostico)
            .addOnSuccessListener {
                databaseReference.child("diagnosticosPorMascota")
                    .child(diagnostico.idMascota.toString())
                    .child(idDiagnostico)
                    .setValue(true)
                    .addOnSuccessListener {
                        onSuccess()
                    }
                    .addOnFailureListener {
                        onError("Diagnóstico creado, pero error al guardar índice por mascota")
                    }
            }
            .addOnFailureListener {
                onError("ERROR al crear el diagnóstico")
            }
    }

    fun actualizarDiagnostico(
        idDiagnostico: String,
        updates: Map<String, Any?>,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
        ) {
        databaseReference.child("diagnosticos").child(idDiagnostico).updateChildren(updates)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError("ERROR al actualizar el diagnóstico") }
    }

    fun generarIdDiagnostico(
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
        ) {
        databaseReference.child("diagnosticos").get()
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
                val nuevoId = "diag_" + String.format("%03d", nuevoNumero)

                onSuccess(nuevoId)
            }
            .addOnFailureListener {
                onError("ERROR al generar ID del diagnóstico")
            }
    }
}