package com.example.vetycare.database.remote

import com.example.vetycare.model.entities.Clinica
import com.google.firebase.database.DatabaseReference

class ClinicaRemote(private val databaseReference: DatabaseReference) {

    fun obtenerClinicaPorId(
        idClinica: String,
        onSuccess: (Clinica?) -> Unit,
        onError: (String?) -> Unit
        ) {
        databaseReference.child("clinicas").child(idClinica).get()
            .addOnSuccessListener { snapshot ->
                val clinica = snapshot.getValue(Clinica::class.java)
                if (clinica != null) {
                    clinica.id = idClinica
                }
                onSuccess(clinica)
            }
            .addOnFailureListener {
                onError("ERROR al leer la clínica")
            }
    }

    fun obtenerIdsClinicasPorComunidad(
        claveComunidad: String,
        onSuccess: (List<String>) -> Unit,
        onError: (String?) -> Unit
        ) {
        databaseReference.child("clinicasPorComunidadAutonoma").child(claveComunidad).get()
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
                onError("ERROR al buscar clínicas por comunidad autónoma")
            }
    }

    fun registrarClinica(
        idClinica: String,
        clinica: Clinica,
        claveComunidad: String,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
    ) {
        val updates = hashMapOf<String, Any?>(
            "/clinicas/$idClinica" to clinica,
            "/clinicasPorComunidadAutonoma/$claveComunidad/$idClinica" to true
        )

        databaseReference.updateChildren(updates)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onError("ERROR al registrar la clínica")
            }
    }

    fun actualizarClinica(
        idClinica: String,
        cambios: Map<String, Any?>,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
    ) {
        databaseReference.child("clinicas").child(idClinica).updateChildren(cambios)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onError("ERROR al actualizar la clínica")
            }
    }

    fun activarClinica(
        idClinica: String,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
    ) {
        databaseReference.child("clinicas").child(idClinica).child("activa").setValue(true)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onError("ERROR al activar la clínica")
            }
    }

    fun desactivarClinica(
        idClinica: String,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
    ) {
        databaseReference.child("clinicas").child(idClinica).child("activa").setValue(false)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onError("ERROR al desactivar la clínica")
            }
    }

    fun actualizarCoordenadasClinica(
        idClinica: String,
        latitud: Double,
        longitud: Double,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
    ) {
        val updates = mapOf(
            "coordenadas/latitud" to latitud,
            "coordenadas/longitud" to longitud
        )

        databaseReference.child("clinicas").child(idClinica).updateChildren(updates)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onError("ERROR al actualizar las coordenadas de la clínica")
            }
    }

    fun eliminarIndiceComunidad(
        idClinica: String,
        claveComunidad: String,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
    ) {
        databaseReference.child("clinicasPorComunidadAutonoma")
            .child(claveComunidad)
            .child(idClinica)
            .removeValue()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onError("ERROR al eliminar el índice de comunidad")
            }
    }

    fun actualizarComunidadClinica(
        idClinica: String,
        claveComunidadAnterior: String,
        claveComunidadNueva: String,
        comunidadAutonomaVisible: String,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
    ) {
        val updates = hashMapOf<String, Any?>(
            "/clinicas/$idClinica/comunidadAutonoma" to comunidadAutonomaVisible,
            "/clinicasPorComunidadAutonoma/$claveComunidadAnterior/$idClinica" to null,
            "/clinicasPorComunidadAutonoma/$claveComunidadNueva/$idClinica" to true
        )

        databaseReference.updateChildren(updates)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onError("ERROR al actualizar la comunidad autónoma de la clínica")
            }
    }
}