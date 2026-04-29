package com.example.vetycare.database.remote

import com.example.vetycare.model.entities.Cita
import com.example.vetycare.model.entities.Clinica
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference

class CitaRemote (private val databaseReference: DatabaseReference) {

    /* EXPLICACIÓN DEL METODO <generarIdCita()> : despliega para leer...
    El metodo generarIdCita accede al nodo "citas" de la base de datos y obtiene todos los registros existentes.
    Después revisa las claves de las citas, extrae el número de cada una y busca el mayor.
    A partir de ese número genera un nuevo id sumando 1 y aplicando el formato "cita_001", "cita_002", etc.
    Si va bien devuelve el id con onSuccess, y si ocurre un error devuelve un mensaje mediante onError.
    */
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

    /* EXPLICACIÓN DEL METODO <registrarCita()> : despliega para leer...
        El metodo registrarCita recibe una cita y comprueba primero que tenga idMascota e idVeterinario, ya que son necesarios para relacionarla correctamente.
        Si alguno de esos datos está vacío, detiene el proceso y devuelve un error mediante onError.
        Después crea un mapa de actualizaciones para guardar la cita en "citas" y enlazarla también con su mascota y veterinario.
        Con updateChildren(updates) realiza todas las escrituras a la vez en la base de datos.
        Si el registro se completa correctamente ejecuta onSuccess, y si falla devuelve un mensaje de error.
    */
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

    /* EXPLICACIÓN DEL METODO <cambiarEstadoCita()> : despliega para leer...
        El metodo cambiarEstadoCita actualiza únicamente el estado de una cita concreta usando su idCita.
        Accede al nodo "citas", selecciona la cita correspondiente y entra en el campo "estadoCita".
        Después asigna el nuevo valor recibido en nuevoEstado mediante setValue.
        Si el cambio se realiza correctamente ejecuta onSuccess, y si ocurre un error devuelve un mensaje mediante onError.
    */
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

    /* EXPLICACIÓN DEL METODO <cancelarCita()> : despliega para leer...
        El metodo cancelarCita se encarga de cancelar una cita concreta usando su idCita.
        Para hacerlo, llama al metodo cambiarEstadoCita y le pasa como nuevo estado el valor "cancelada".
        No modifica directamente la base de datos, sino que reutiliza la lógica ya creada para cambiar el estado de una cita.
        Si el cambio se realiza correctamente ejecuta onSuccess, y si ocurre un error lo devuelve mediante onError.
    */
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

    /* EXPLICACIÓN DEL METODO <obtenerCitasPorPropietario()> : despliega para leer...
        El metodo obtenerCitasPorPropietario busca todas las citas cuyo campo idPropietario coincida con el propietario recibido.
        Para ello consulta el nodo "citas" usando orderByChild("idPropietario") y equalTo(idPropietario).
        Después recorre los resultados, convierte cada registro en un objeto Cita y le asigna su id correspondiente.
        Cada cita se guarda junto a su id en una lista de pares Pair<String, Cita>.
        Finalmente devuelve la lista ordenada por fechaHoraInicio, o un mensaje de error si la búsqueda falla.
    */
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

    /* FIXME: BORRAR => MÉTODO NO UTILIZADO
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
    */
    /* FIXME: BORRAR => MÉTODO NO UTILIZADO
    /* EXPLICACIÓN DEL METODO <obtenerCitaPorId()> : despliega para leer...
        El metodo obtenerCitaPorId busca en la base de datos una cita concreta usando el idCita recibido.
        Si la lectura se realiza correctamente, convierte los datos obtenidos en un objeto de tipo Cita.
        Después asigna manualmente el id de la cita al objeto, ya que normalmente Firebase no lo guarda dentro del propio objeto.
        Finalmente devuelve la cita mediante onSuccess, o un mensaje de error mediante onError si falla la lectura.
    */
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
    */
    /* FIXME: BORRAR => MÉTODO NO UTILIZADO
    /* EXPLICACIÓN DEL METODO <obtenerIdsCitasPorMascota()> : despliega para leer...
        El metodo obtenerIdsCitasPorMascota busca en la base de datos las citas asociadas a una mascota concreta usando su idMascota.
        Recorre los registros encontrados y comprueba si cada cita está marcada como válida con valor true.
        Si es válida, añade el id de esa cita a una lista de resultados.
        Finalmente devuelve la lista de ids con onSuccess, o un mensaje de error con onError si la lectura falla.
    */
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
    */
    /* FIXME: BORRAR => MÉTODO NO UTILIZADO
    /* EXPLICACIÓN DEL METODO <obtenerIdsCitasPorVeterinario()> : despliega para leer...
        El metodo obtenerIdsCitasPorVeterinario busca en la base de datos las citas asociadas a un veterinario concreto usando su idVeterinario.
        Accede al nodo "citasPorVeterinario" y obtiene los registros relacionados con ese veterinario.
        Después recorre cada hijo y comprueba si su valor es true, indicando que la cita está vinculada correctamente.
        Si el registro es válido, añade el id de la cita a una lista.
        Finalmente devuelve la lista de ids mediante onSuccess, o un mensaje de error con onError si falla la lectura.
    */
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
    */
    /* FIXME: BORRAR => MÉTODO NO UTILIZADO
    /* EXPLICACIÓN DEL METODO <actualizarCita()> : despliega para leer...
        El metodo actualizarCita modifica los datos de una cita concreta usando su idCita.
        Accede al nodo "citas" de la base de datos y selecciona la cita correspondiente.
        Después aplica los cambios recibidos en el mapa updates, actualizando solo los campos indicados.
        Si la actualización se realiza correctamente ejecuta onSuccess, y si ocurre algún error devuelve un mensaje mediante onError.
    */
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
    */
    /* FIXME: BORRAR => MÉTODO NO UTILIZADO
    /* EXPLICACIÓN DEL METODO <reprogramarCita()> : despliega para leer...
        El metodo reprogramarCita se encarga de cambiar la fecha y hora de una cita existente usando su idCita.
        Primero crea un mapa updates con la nueva hora de inicio, la nueva hora de fin y el estado "programada".
        Después llama al metodo actualizarCita, reutilizando su lógica para modificar solo esos campos en la base de datos.
        Si la actualización se realiza correctamente ejecuta onSuccess, y si ocurre un error lo devuelve mediante onError.
    */
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
    */
    /* FIXME: BORRAR => MÉTODO NO UTILIZADO
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
    */
}
