/*package com.example.vetycare.ui.container

import com.example.vetycare.model.entities.Cita
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class CitasRepository {

    private val db = FirebaseDatabase.getInstance().reference
    private val auth = FirebaseAuth.getInstance()

    suspend fun getCitasDelUsuario(): List<Cita> {
        val uid = auth.currentUser?.uid ?: return emptyList()

        return try {
            // 1. Buscar el idPropietario a partir del authUid
            val propietarioSnap = db.child("propietariosPorAuthUid")
                .child("auth")
                .get().await()
            val idPropietario = propietarioSnap.value?.toString() ?: return emptyList()

            // 2. Obtener todas las citas y filtrar por idPropietario
            val citasSnap = db.child("citas").get().await()

            val citas = mutableListOf<Cita>()
            for (citaSnap in citasSnap.children) {
                val idCita = citaSnap.key ?: continue
                val idProp = citaSnap.child("idPropietario").value?.toString() ?: continue
                if (idProp != idPropietario) continue

                val cita = Cita(
                    id = idCita,
                    idMascota = citaSnap.child("idMascota").value?.toString() ?: "",
                    idClinica = citaSnap.child("idClinica").value?.toString() ?: "",
                    tipoCita = citaSnap.child("tipoCita").value?.toString() ?: "",
                    motivoConsulta = citaSnap.child("motivoConsulta").value?.toString() ?: "",
                    idVeterinario = citaSnap.child("idVeterinario").value?.toString() ?: "",
                    fechaHoraInicio = citaSnap.child("fechaHoraInicio").value?.toString() ?: "",
                    fechaHoraFin = citaSnap.child("fechaHoraFin").value?.toString() ?: "",
                    estadoCita = citaSnap.child("estadoCita").value?.toString() ?: "",
                    observaciones = citaSnap.child("observaciones").value?.toString() ?: "",
                    idPropietario = idProp
                )
                citas.add(cita)
            }
            citas

        } catch (e: Exception) {
            emptyList()
        }
    }
}

    // Para obtener el nombre real de la mascota
    suspend fun getNombreMascota(idMascota: String): String {
        return try {
            db.child("mascotas").child(idMascota).child("nombre")
                .get().await().value?.toString() ?: idMascota
        } catch (e: Exception) {
            idMascota
        }
    }

    // Para obtener el nombre real de la clínica
    suspend fun getNombreClinica(idClinica: String): String {
        return try {
            db.child("clinicas").child(idClinica).child("nombre")
                .get().await().value?.toString() ?: idClinica
        } catch (e: Exception) {
            idClinica
        }
    }
}*/
