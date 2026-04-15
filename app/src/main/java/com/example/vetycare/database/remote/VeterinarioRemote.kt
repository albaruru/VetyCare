package com.example.vetycare.database.remote

import com.example.vetycare.model.entities.Veterinario
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference

class VeterinarioRemote(private val databaseReference: DatabaseReference) {

    private fun extraerIdsBoolean(snapshot: DataSnapshot): List<String> {
        val listaIds = mutableListOf<String>()

        for (child in snapshot.children) {
            val esValido = child.getValue(Boolean::class.java) ?: false
            if (esValido) {
                child.key?.let { listaIds.add(it) }
            }
        }

        return listaIds
    }

    fun obtenerTodosLosVeterinarios(
        onSuccess: (List<Veterinario>) -> Unit,
        onError: (String?) -> Unit
    ) {
        databaseReference.child("veterinarios").get()
            .addOnSuccessListener { snapshot ->
                val lista = mutableListOf<Veterinario>()

                for (child in snapshot.children) {
                    val veterinario = child.getValue(Veterinario::class.java)
                    if (veterinario != null) {
                        veterinario.id = child.key ?: ""
                        lista.add(veterinario)
                    }
                }

                onSuccess(lista)
            }
            .addOnFailureListener {
                onError("ERROR al leer todos los veterinarios")
            }
    }

    fun obtenerVeterinarioPorId(
        idVeterinario: String,
        onSuccess: (Veterinario?) -> Unit,
        onError: (String?) -> Unit
    ) {
        databaseReference.child("veterinarios").child(idVeterinario).get()
            .addOnSuccessListener { snapshot ->
                val veterinario = snapshot.getValue(Veterinario::class.java)
                if (veterinario != null) {
                    veterinario.id = idVeterinario
                }
                onSuccess(veterinario)
            }
            .addOnFailureListener {
                onError("ERROR al leer el veterinario")
            }
    }

    fun obtenerIdsVeterinariosPorClinica(
        idClinica: String,
        onSuccess: (List<String>) -> Unit,
        onError: (String?) -> Unit
    ) {
        databaseReference.child("veterinariosPorClinica").child(idClinica).get()
            .addOnSuccessListener { snapshot ->
                onSuccess(extraerIdsBoolean(snapshot))
            }
            .addOnFailureListener {
                onError("ERROR al buscar veterinarios por clínica")
            }
    }

    fun obtenerIdsVeterinariosPorColegio(
        idColegio: String,
        onSuccess: (List<String>) -> Unit,
        onError: (String?) -> Unit
    ) {
        databaseReference.child("veterinariosPorColegio").child(idColegio).get()
            .addOnSuccessListener { snapshot ->
                onSuccess(extraerIdsBoolean(snapshot))
            }
            .addOnFailureListener {
                onError("ERROR al buscar veterinarios por colegio")
            }
    }

    fun registrarVeterinario(
        idVeterinario: String,
        veterinario: Veterinario,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
    ) {
        val idClinica = veterinario.idClinica
        val idColegio = veterinario.idColegio

        if (idClinica.isNullOrEmpty()) {
            onError("El veterinario no tiene idClinica")
            return
        }

        if (idColegio.isNullOrEmpty()) {
            onError("El veterinario no tiene idColegio")
            return
        }

        val updates = hashMapOf<String, Any?>(
            "/veterinarios/$idVeterinario" to veterinario,
            "/veterinariosPorClinica/$idClinica/$idVeterinario" to true,
            "/veterinariosPorColegio/$idColegio/$idVeterinario" to true
        )

        databaseReference.updateChildren(updates)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onError("ERROR al registrar el veterinario")
            }
    }

    fun actualizarVeterinario(
        idVeterinario: String,
        cambios: Map<String, Any?>,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
    ) {
        databaseReference.child("veterinarios").child(idVeterinario).updateChildren(cambios)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onError("ERROR al actualizar el veterinario")
            }
    }

    fun activarVeterinario(
        idVeterinario: String,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
    ) {
        databaseReference.child("veterinarios").child(idVeterinario).child("activa").setValue(true)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onError("ERROR al activar el veterinario")
            }
    }

    fun desactivarVeterinario(
        idVeterinario: String,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
    ) {
        databaseReference.child("veterinarios").child(idVeterinario).child("activa").setValue(false)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onError("ERROR al desactivar el veterinario")
            }
    }

    fun actualizarClinicaVeterinario(
        idVeterinario: String,
        idClinicaAnterior: String,
        idClinicaNueva: String,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
    ) {
        val updates = hashMapOf<String, Any?>(
            "/veterinarios/$idVeterinario/idClinica" to idClinicaNueva,
            "/veterinariosPorClinica/$idClinicaAnterior/$idVeterinario" to null,
            "/veterinariosPorClinica/$idClinicaNueva/$idVeterinario" to true
        )

        databaseReference.updateChildren(updates)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onError("ERROR al actualizar la clínica del veterinario")
            }
    }

    fun actualizarColegioVeterinario(
        idVeterinario: String,
        idColegioAnterior: String,
        idColegioNuevo: String,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
    ) {
        val updates = hashMapOf<String, Any?>(
            "/veterinarios/$idVeterinario/idColegio" to idColegioNuevo,
            "/veterinariosPorColegio/$idColegioAnterior/$idVeterinario" to null,
            "/veterinariosPorColegio/$idColegioNuevo/$idVeterinario" to true
        )

        databaseReference.updateChildren(updates)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onError("ERROR al actualizar el colegio del veterinario")
            }
    }
}