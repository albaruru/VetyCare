package com.example.vetycare.database.remote

import com.example.vetycare.model.entities.Propietario
import com.google.firebase.database.DatabaseReference

class PropietarioRemote (private val databaseReference : DatabaseReference) {

    /* METODO PARA ACCEDER A LA RAMA DE <propietariosPorAuthUid>:
    RAMA => propietariosPorAuthUid
    */
    fun obtenerIdPropietarioPorAuthUid (
        authUid: String,
        onSuccess: (String?) -> Unit,
        onError: (String?) -> Unit
        ) {
        databaseReference.child("propietariosPorAuthUid").child(authUid).get()
            .addOnSuccessListener { snapshot ->
                val idPropietario = snapshot.getValue(String::class.java)
                onSuccess(idPropietario)
            }
            .addOnFailureListener {
                onError("ERROR al buscar el propietario por authUid")
            }
    }

    /* METODO PARA OBTENER DATOS AL PROPIETARIO SEGUN ID: ...
    RAMA => propietarios
    */
    fun obtenerPropietarioPorId (
        idPropietario: String,
        onSuccess: (Propietario?) -> Unit,
        onError: (String?) -> Unit
        ) {
        databaseReference.child("propietarios").child(idPropietario).get()
            .addOnSuccessListener { snapshot ->
                val propietario = snapshot.getValue(Propietario::class.java)
                onSuccess(propietario)
            }
            .addOnFailureListener {
                onError("ERROR al leer el propietario")
            }
    }

    /* METODO CREAR NUEVO PROPIETARIO: ...
    RAMA => propietarios
    */
    fun crearPropietario (
        idPropietario: String,
        propietario: Propietario,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
        ) {
        databaseReference.child("propietarios").child(idPropietario).setValue(propietario)
            .addOnSuccessListener {
                databaseReference.child("propietariosPorAuthUid").child(propietario.authUid.toString()).setValue(idPropietario)
                    .addOnSuccessListener { onSuccess()
                    }
                    .addOnFailureListener { onError("Propietario creado, pero error al guardar índice por authUid")
                    }
            }
            .addOnFailureListener {
                onError("ERROR al crear el propietario")
            }
    }

    /* METODO ACTUALIZAR LOS DATOS DEL PROPIETARIO: ...
    RAMA => propietarios
    */
    fun actualizarPropietario (
        idPropietario: String,
        updates: Map <String, Any?>,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
        ) {
        databaseReference.child("propietarios").child(idPropietario).updateChildren(updates)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError("ERROR al utilizar el propietario") }
    }

    /* METODO PARA GENERAR EL SIGUIENTE ID DEL PROPIETARIO:
    RAMA => propietarios
    */
    fun generarIdPropietario(
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        databaseReference.child("propietarios").get()
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
                val nuevoId = "prop_" + String.format("%03d", nuevoNumero)

                onSuccess(nuevoId)
            }
            .addOnFailureListener {
                onError("ERROR al generar ID del propietario")
            }
    }
}