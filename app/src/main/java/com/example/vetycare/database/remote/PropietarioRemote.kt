package com.example.vetycare.database.remote

import com.example.vetycare.model.entities.Propietario
import com.google.firebase.database.DatabaseReference

class PropietarioRemote (private val databaseReference : DatabaseReference) {

    /* METODO PARA ACCEDER A LA RAMA DE <propietariosPorAuthUid>:
    */
    fun obtenerIdPropietarioPorAuthUid (
        authUid: String,
        onSuccess: (String?) -> Unit,
        onError: (String?) -> Unit) {

        databaseReference.child("propietariosPorAuthId").child(authUid).get()
            .addOnSuccessListener { snapshot ->
                val idPropietario = snapshot.getValue(String::class.java)
                onSuccess(idPropietario)
            }
            .addOnFailureListener {
                onError("ERROR al buscar el propietario por authUid")
            }
    }

    fun obtenerPropietarioPorId (
        idPropietario: String,
        onSuccess: (Propietario?) -> Unit,
        onError: (String?) -> Unit ) {

        databaseReference.child("propietarios").child(idPropietario).get()
            .addOnSuccessListener { snapshot ->
                val propietario = snapshot.getValue(Propietario::class.java)
                onSuccess(propietario)
            }
            .addOnFailureListener {
                onError("ERROR al leer el propietario")
            }
    }
}