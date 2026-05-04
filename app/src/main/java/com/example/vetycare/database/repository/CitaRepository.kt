package com.example.vetycare.database.repository

import com.example.vetycare.database.remote.CitaRemote
import com.example.vetycare.model.entities.Cita

class CitaRepository (private val remoteCita: CitaRemote) {

    /* EXPLICACIÓN DEL METODO <generarIdCita()> : despliega para leer...
        El mEtodo generarIdCita actúa como intermediario entre la capa actual y remoteCita.
        Recibe las funciones onSuccess y onError para gestionar el resultado de la operación.
        Después llama al mEtodo generarIdCita de remoteCita, delegando en él la generación real del id.
        Finalmente, el resultado correcto o el error se devuelve usando los mismos callbacks recibidos.
    */
    fun generarIdCita(
        onSuccess: (String) -> Unit,
        onError: (String?) -> Unit
    ) {
        remoteCita.generarIdCita(
            onSuccess = onSuccess,
            onError = onError
        )
    }

    /* EXPLICACIÓN DEL METODO <registrarCita()> : despliega para leer...
        El metodo registrarCita actúa como intermediario para registrar una cita desde la capa actual.
        Recibe el idCita, el objeto cita y los callbacks de resultado.
        Después llama directamente al metodo registrarCita de remoteCita, delegando en él el registro real en la base de datos.
        Finalmente, si el proceso se completa correctamente se ejecuta onSuccess, y si ocurre un error se devuelve mediante onError.
    */
    fun registrarCita(
        idCita: String,
        cita: Cita,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
    ) {
        remoteCita.registrarCita(
            idCita = idCita,
            cita = cita,
            onSuccess = onSuccess,
            onError = onError
        )
    }

    /* EXPLICACIÓN DEL METODO <cancelarCita()> : despliega para leer...
        El metodo cancelarCita actúa como intermediario para cancelar una cita desde la capa actual.
        Recibe el idCita y los callbacks onSuccess y onError para gestionar el resultado.
        Después llama directamente al metodo cancelarCita de remoteCita, delegando en él la cancelación real de la cita.
        Finalmente, si la operación se completa correctamente ejecuta onSuccess, y si ocurre un error lo devuelve mediante onError.
    */
    fun cancelarCita(
        idCita: String,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
    ) {
        remoteCita.cancelarCita(
            idCita = idCita,
            onSuccess = onSuccess,
            onError = onError
        )
    }

    /* EXPLICACIÓN DEL METODO <obtenerCitasPorPropietario()> : despliega para leer...
        El metodo obtenerCitasPorPropietario actúa como intermediario para obtener las citas de un propietario concreto.
        Recibe el idPropietario y los callbacks necesarios para gestionar el resultado de la operación.
        Después llama directamente al metodo obtenerCitasPorPropietario de remoteCita, delegando en él la búsqueda real.
        Finalmente devuelve la lista de citas mediante onSuccess, o comunica el error mediante onError si la operación falla.
    */
    fun obtenerCitasPorPropietario(
        idPropietario: String,
        onSuccess: (List<Pair<String, Cita>>) -> Unit,
        onError: (String?) -> Unit
    ) {
        remoteCita.obtenerCitasPorPropietario(
            idPropietario = idPropietario,
            onSuccess = onSuccess,
            onError = onError
        )
    }

}
