package com.example.vetycare.database.remote

import com.example.vetycare.model.entities.Clinica
import com.google.firebase.database.DatabaseReference

class ClinicaRemote(private val databaseReference: DatabaseReference) {

    /* EXPLICACIÓN DEL METODO <obtenerTodasLasClinicas()> : despliega para leer...
        El metodo obtenerTodasLasClinicas obtiene todos los registros guardados dentro del nodo "clinicas" de la base de datos.
        Si la lectura se realiza correctamente, recorre cada clínica encontrada y la convierte en un objeto de tipo Clinica.
        Después asigna a cada clínica su id usando la clave del nodo correspondiente.
        Finalmente añade todas las clínicas a una lista y la devuelve mediante onSuccess, o muestra un mensaje de error con onError si falla la lectura.
    */
    fun obtenerTodasLasClinicas(
        onSuccess: (List<Clinica>) -> Unit,
        onError: (String?) -> Unit
        ) {
        databaseReference.child("clinicas").get()
            .addOnSuccessListener { snapshot ->
                val lista = mutableListOf<Clinica>()

                for (child in snapshot.children) {
                    val clinica = child.getValue(Clinica::class.java)
                    if (clinica != null) {
                        clinica.id = child.key ?: ""
                        lista.add(clinica)
                    }
                }

                onSuccess(lista)
            }
            .addOnFailureListener {
                onError("ERROR al leer todas las clínicas")
            }
    }

    /* EXPLICACIÓN DEL METODO <obtenerClinicaPorId()> : despliega para leer...
        El metodo obtenerClinicaPorId busca una clínica concreta en la base de datos usando el idClinica recibido.
        Accede al nodo "clinicas" y selecciona el registro que coincide con ese id.
        Si la lectura es correcta, convierte los datos obtenidos en un objeto de tipo Clinica.
        Después asigna manualmente el id al objeto y lo devuelve mediante onSuccess.
        Si ocurre algún error durante la lectura, devuelve un mensaje mediante onError.
    */
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

    /* EXPLICACIÓN DEL METODO <obtenerIdsClinicasPorComunidad()> : despliega para leer...
        El metodo obtenerIdsClinicasPorComunidad busca las clínicas asociadas a una comunidad autónoma usando su claveComunidad.
        Accede al nodo "clinicasPorComunidadAutonoma" y obtiene los registros vinculados a esa comunidad.
        Después recorre cada hijo y comprueba si su valor es true, indicando que la relación es válida.
        Si es válido, añade el id de la clínica a una lista.
        Finalmente devuelve la lista de ids mediante onSuccess, o un mensaje de error con onError si falla la lectura.
    */
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

}