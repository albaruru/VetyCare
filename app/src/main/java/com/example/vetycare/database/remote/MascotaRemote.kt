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
}