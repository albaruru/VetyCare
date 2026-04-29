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

    /* FIXME: BORRAR => MÉTODO NO UTILIZADO
    /* EXPLICACIÓN DEL METODO <registrarClinica()> : despliega para leer...
        El metodo registrarClinica guarda una nueva clínica en la base de datos usando el idClinica recibido.
        Primero crea un mapa updates con dos registros: la clínica completa dentro de "clinicas" y su relación con la comunidad autónoma correspondiente.
        Esta relación se guarda en "clinicasPorComunidadAutonoma" usando claveComunidad y marcando el id de la clínica como true.
        Después aplica todos los cambios a la vez con updateChildren.
        Si el registro se completa correctamente ejecuta onSuccess, y si ocurre algún error devuelve un mensaje mediante onError.
    */
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
    */
    /* FIXME: BORRAR => MÉTODO NO UTILIZADO
    /* EXPLICACIÓN DEL METODO <actualizarClinica()> : despliega para leer...
        El metodo actualizarClinica modifica los datos de una clínica concreta usando su idClinica.
        Accede al nodo "clinicas" y selecciona la clínica correspondiente dentro de la base de datos.
        Después aplica los cambios recibidos en el mapa cambios, actualizando solo los campos indicados.
        Si la actualización se realiza correctamente ejecuta onSuccess, y si ocurre algún error devuelve un mensaje mediante onError.
    */
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
    */
    /* FIXME: BORRAR => MÉTODO NO UTILIZADO
    /* EXPLICACIÓN DEL METODO <activarClinica()> : despliega para leer...
        El metodo activarClinica cambia el estado de una clínica concreta para marcarla como activa.
        Accede al nodo "clinicas", selecciona la clínica mediante su idClinica y entra en el campo "activa".
        Después asigna el valor true, indicando que la clínica queda activada.
        Si el cambio se realiza correctamente ejecuta onSuccess, y si ocurre un error devuelve un mensaje mediante onError.
    */
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
    */
    /* FIXME: BORRAR => MÉTODO NO UTILIZADO
    /* EXPLICACIÓN DEL METODO <desactivarClinica()> : despliega para leer...
        El metodo desactivarClinica cambia el estado de una clínica concreta para marcarla como inactiva.
        Accede al nodo "clinicas", selecciona la clínica mediante su idClinica y entra en el campo "activa".
        Después asigna el valor false, indicando que la clínica queda desactivada.
        Si el cambio se realiza correctamente ejecuta onSuccess, y si ocurre un error devuelve un mensaje mediante onError.
    */
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
    */
    /* FIXME: BORRAR => MÉTODO NO UTILIZADO
    /* EXPLICACIÓN DEL METODO <actualizarCoordenadasClinica()> : despliega para leer...
        El metodo actualizarCoordenadasClinica actualiza la latitud y longitud de una clínica concreta usando su idClinica.
        Primero crea un mapa updates con los nuevos valores dentro del nodo "coordenadas".
        Después accede a la clínica correspondiente dentro de "clinicas" y aplica los cambios con updateChildren.
        Si la actualización se realiza correctamente ejecuta onSuccess, y si ocurre algún error devuelve un mensaje mediante onError.
    */
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
    */
    /* FIXME: BORRAR => MÉTODO NO UTILIZADO
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
    */
    /* FIXME: BORRAR => MÉTODO NO UTILIZADO
    /* EXPLICACIÓN DEL METODO <actualizarCoordenadasClinica()> : despliega para leer...
        El metodo actualizarComunidadClinica cambia la comunidad autónoma asociada a una clínica concreta usando su idClinica.
        Primero actualiza el campo "comunidadAutonoma" de la clínica con el nuevo valor visible.
        Después elimina la referencia de la clínica en la comunidad autónoma anterior, asignándole null.
        A continuación crea la nueva relación dentro de "clinicasPorComunidadAutonoma" usando la nueva clave de comunidad.
        Finalmente aplica todos los cambios a la vez con updateChildren, ejecutando onSuccess si va bien o onError si ocurre un fallo.
    */
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
    */
}