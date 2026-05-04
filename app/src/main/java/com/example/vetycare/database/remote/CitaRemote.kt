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

}
