package com.example.vetycare.database.remote

import com.example.vetycare.model.entities.Mascota
import com.google.firebase.database.DatabaseReference

class MascotaRemote (private val databaseReference: DatabaseReference) {

    fun obtenerMascotaPorId (
        idMascota: String,
        onSuccess: (Mascota?) -> Unit,
        onError: (String?) -> Unit
        ) {
        databaseReference.child("mascotas").child(idMascota).get()
            .addOnSuccessListener { snapshot ->
                val mascota = snapshot.getValue(Mascota::class.java)
                onSuccess(mascota)
            }
            .addOnFailureListener {
                onError("ERROR al leer la mascota")
            }
    }

    fun obtenerIdsMascotasPorPropietario (
        idPropietario: String,
        onSuccess: (List<String>) -> Unit,
        onError: (String?) -> Unit
        ) {
        databaseReference.child("mascotasPorPropietario").child(idPropietario).get()
            .addOnSuccessListener { snapshot ->
                val listaIds = mutableListOf<String>()
                for (child in snapshot.children) {
                    val esValido = child.getValue(Boolean::class.java) ?: false
                    if (esValido) {
                        child.key?.let {
                            listaIds.add(it)
                        }
                    }
                }
                onSuccess(listaIds)
            }
            .addOnFailureListener {
                onError("ERROR al buscar mascotas por propietario")
            }
    }

    fun registrarMascota (
        idMascota: String,
        mascota: Mascota,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
        ){
        databaseReference.child("mascotas").child(idMascota).setValue(mascota)
            .addOnSuccessListener {
                databaseReference.child("mascotasPorPropietario").child(mascota.idPropietario.toString()).child(idMascota).setValue(true)
                    .addOnSuccessListener {
                        onSuccess() }
                    .addOnFailureListener {
                        onError("Mascota creada, pero error al guardar índice por propietario")
                    }
            }
            .addOnFailureListener {
                onError("ERROR al crear la mascota")
            }
    }

    fun actualizarMascota (
        idMascota: String,
        updates: Map<String,Any?>,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
        ){
        databaseReference.child("mascotas").child(idMascota).updateChildren(updates)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError("EEROR al actualizar la mascota") }
    }

    fun generarIdMascota(
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        databaseReference.child("mascotas").get()
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
                val nuevoId = "masc_" + String.format("%03d", nuevoNumero)

                onSuccess(nuevoId)
            }
            .addOnFailureListener {
                onError("ERROR al generar ID de mascota")
            }
    }

    fun eliminarMascota(
        idMascota: String,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
    ) {
        // Referenciamos el nodo exacto de la mascota y lo eliminamos
        databaseReference.child("mascotas").child(idMascota).removeValue()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError("Error al eliminar la mascota en el servidor") }
    }
}