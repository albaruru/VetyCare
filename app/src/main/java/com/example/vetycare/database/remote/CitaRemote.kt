package com.example.vetycare.database.remote

import com.example.vetycare.model.entities.Cita
import com.example.vetycare.model.entities.Clinica
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference

class CitaRemote (private val databaseReference: DatabaseReference) {

    private fun extraerIdsBoolean(snapshot: DataSnapshot): List<String> {
        val ids = mutableListOf<String>()
        for (child in snapshot.children) {
            val esValido = child.getValue(Boolean::class.java) ?: false
            if (esValido) {
                child.key?.let { ids.add(it) }
            }
        }
        return ids
    }

    fun generarIdCita(
        onSuccess: (String) -> Unit,
        onError: (String?) -> Unit
    ) {
        databaseReference.child("citas").get()
            .addOnSuccessListener { snapshot ->
                val ultimoNumero = snapshot.children
                    .mapNotNull { it.key?.removePrefix("cita_")?.toIntOrNull() }
                    .maxOrNull() ?: 0

                val nuevoId = "cita_%03d".format(ultimoNumero + 1)
                onSuccess(nuevoId)
            }
            .addOnFailureListener {
                onError("ERROR al generar el id de la cita")
            }
    }

    fun obtenerCitaPorId(
        idCita: String,
        onSuccess: (Cita?) -> Unit,
        onError: (String?) -> Unit
    ) {
        databaseReference.child("citas").child(idCita).get()
            .addOnSuccessListener { snapshot ->
                val cita = snapshot.getValue(Cita::class.java)
                if (cita != null) {
                    cita.id = idCita
                }
                onSuccess(cita)
            }
            .addOnFailureListener {
                onError("ERROR al leer la cita")
            }
    }

    fun obtenerIdsCitasPorMascota(
        idMascota: String,
        onSuccess: (List<String>) -> Unit,
        onError: (String?) -> Unit
    ) {
        databaseReference.child("citasPorMascota").child(idMascota).get()
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
                onError("ERROR al buscar citas por mascota")
            }
    }

    fun obtenerIdsCitasPorVeterinario(
        idVeterinario: String,
        onSuccess: (List<String>) -> Unit,
        onError: (String?) -> Unit
    ) {
        databaseReference.child("citasPorVeterinario").child(idVeterinario).get()
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
                onError("ERROR al buscar citas por veterinario")
            }
    }

    fun registrarCita(
        idCita: String,
        cita: Cita,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
    ) {
        val idMascota = cita.idMascota
        val idVeterinario = cita.idVeterinario

        if (idMascota.isNullOrEmpty()) {
            onError("La cita no tiene idMascota")
            return
        }

        if (idVeterinario.isNullOrEmpty()) {
            onError("La cita no tiene idVeterinario")
            return
        }

        val updates = hashMapOf<String, Any?>(
            "/citas/$idCita" to cita,
            "/citasPorMascota/$idMascota/$idCita" to true,
            "/citasPorVeterinario/$idVeterinario/$idCita" to true
        )

        databaseReference.updateChildren(updates)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError("ERROR al registrar la cita") }
    }

    fun actualizarCita(
        idCita: String,
        updates: Map<String, Any?>,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
    ) {
        databaseReference.child("citas").child(idCita).updateChildren(updates)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError("ERROR al actualizar la cita") }
    }

    fun cambiarEstadoCita(
        idCita: String,
        nuevoEstado: String,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
    ) {
        databaseReference.child("citas").child(idCita).child("estadoCita").setValue(nuevoEstado)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError("ERROR al cambiar el estado de la cita") }
    }

    fun cancelarCita(
        idCita: String,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
    ) {
        cambiarEstadoCita(
            idCita = idCita,
            nuevoEstado = "cancelada",
            onSuccess = onSuccess,
            onError = onError
        )
    }

    fun reprogramarCita(
        idCita: String,
        nuevaFechaHoraInicio: String,
        nuevaFechaHoraFin: String,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
    ) {
        val updates = mapOf(
            "fechaHoraInicio" to nuevaFechaHoraInicio,
            "fechaHoraFin" to nuevaFechaHoraFin,
            "estadoCita" to "programada"
        )

        actualizarCita(
            idCita = idCita,
            updates = updates,
            onSuccess = onSuccess,
            onError = onError
        )
    }

    fun eliminarCitaDeIndices(
        idCita: String,
        idMascota: String,
        idVeterinario: String,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
    ) {
        val updates = hashMapOf<String, Any?>(
            "/citasPorMascota/$idMascota/$idCita" to null,
            "/citasPorVeterinario/$idVeterinario/$idCita" to null
        )

        databaseReference.updateChildren(updates)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError("ERROR al eliminar la cita de los índices") }
    }

    fun obtenerCitasPorPropietario(
        idPropietario: String,
        onSuccess: (List<Pair<String, Cita>>) -> Unit,
        onError: (String?) -> Unit
    ) {
        databaseReference.child("citas")
            .orderByChild("idPropietario")
            .equalTo(idPropietario)
            .get()
            .addOnSuccessListener { snapshot ->
                val listaCitas = mutableListOf<Pair<String, Cita>>()

                for (child in snapshot.children) {
                    val cita = child.getValue(Cita::class.java)
                    val idCita = child.key ?: continue

                    if (cita != null) {
                        cita.id = idCita
                        listaCitas.add(idCita to cita)
                    }
                }

                onSuccess(listaCitas.sortedBy { it.second.fechaHoraInicio ?: "" })
            }
            .addOnFailureListener {
                onError("ERROR al buscar citas por propietario")
            }
    }
}
