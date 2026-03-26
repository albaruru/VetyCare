/*package com.example.vetycare.ui.container

import com.example.vetycare.model.entities.Cita
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class CitasRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun getCitasDelUsuario(): List<Cita> {
        val uid = auth.currentUser?.uid ?: return emptyList()
        return try {
            val resultado = db.collection("citas")
                .whereEqualTo("idPropietario", uid)
                .get()
                .await()

            // Añadimos el id del documento a cada cita
            resultado.documents.mapNotNull { doc ->
                doc.toObject(Cita::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}*/