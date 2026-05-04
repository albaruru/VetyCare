package com.example.vetycare.database.remote

import com.example.vetycare.model.entities.Diagnostico
import com.google.firebase.database.DatabaseReference

class DiagnosticoRemote (private val databaseReference: DatabaseReference){

    /* EXPLICACIÓN DEL METODO <obtenerDiagnosticoPorId()> : despliega para leer...
        El metodo obtenerDiagnosticoPorId busca un diagnóstico concreto en la base de datos usando el idDiagnostico recibido.
        Accede al nodo "diagnosticos" y selecciona el registro que coincide con ese id.
        Si la lectura es correcta, convierte los datos obtenidos en un objeto de tipo Diagnostico.
        Después asigna al objeto su id usando la clave del nodo y lo devuelve mediante onSuccess.
        Si ocurre algún error durante la lectura, devuelve un mensaje mediante onError.
    */
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

    /* EXPLICACIÓN DEL METODO <obtenerIdsDiagnosticosPorMascota()> : despliega para leer...
        El metodo obtenerIdsDiagnosticosPorMascota busca los diagnósticos asociados a una mascota concreta usando su idMascota.
        Accede al nodo "diagnosticosPorMascota" y obtiene los registros vinculados a esa mascota.
        Después recorre cada hijo y comprueba si su valor es true, indicando que la relación es válida.
        Si es válido, añade el id del diagnóstico a una lista.
        Finalmente devuelve la lista de ids mediante onSuccess, o un mensaje de error con onError si falla la lectura.
    */
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

}