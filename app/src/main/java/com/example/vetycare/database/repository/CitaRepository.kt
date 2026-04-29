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

    /* FIXME: BORRAR => METODO NO UTILIZADO
fun obtenerCitaPorId(
    idCita: String,
    onSuccess: (Cita) -> Unit,
    onError: (String?) -> Unit
) {
    remoteCita.obtenerCitaPorId(
        idCita = idCita,
        onSuccess = { cita ->
            if (cita != null) {
                onSuccess(cita)
            } else {
                onError("No se encontró la cita")
            }
        },
        onError = onError
    )
}
*/
    /* FIXME: BORRAR => MÉTODO NO UTILIZADO
    /* EXPLICACIÓN DEL METODO <obtenerCitasPorMascota()> : despliega para leer...
        El metodo obtenerCitasPorMascota obtiene primero los ids de las citas asociadas a una mascota usando idMascota.
        Si no encuentra ninguna cita, devuelve una lista vacía mediante onSuccess.
        Si hay ids, recorre cada uno y obtiene la cita completa llamando a obtenerCitaPorId.
        Cada cita encontrada se guarda junto a su id en una lista de pares Pair<String, Cita>.
        Cuando termina de cargar todas las citas, las ordena por fechaHoraInicio y las devuelve;
        si ocurre un error, lo comunica mediante onError.
    */
    fun obtenerCitasPorMascota(
        idMascota: String,
        onSuccess: (List<Pair<String, Cita>>) -> Unit,
        onError: (String?) -> Unit
    ) {
        remoteCita.obtenerIdsCitasPorMascota(
            idMascota = idMascota,
            onSuccess = { listaIds ->
                if (listaIds.isEmpty()) {
                    onSuccess(emptyList())
                    return@obtenerIdsCitasPorMascota
                }

                val listaCitas = mutableListOf<Pair<String, Cita>>()
                var restantes = listaIds.size
                var hayError = false

                listaIds.forEach { idCita ->
                    remoteCita.obtenerCitaPorId(
                        idCita = idCita,
                        onSuccess = { cita ->
                            if (cita != null) {
                                listaCitas.add(idCita to cita)
                            }

                            restantes--
                            if (restantes == 0 && !hayError) {
                                onSuccess(listaCitas.sortedBy { it.second.fechaHoraInicio ?: "" })
                            }
                        },
                        onError = { error ->
                            if (!hayError) {
                                hayError = true
                                onError(error)
                            }
                        }
                    )
                }
            },
            onError = onError
        )
    }
    */
    /* FIXME: BORRAR => MÉTODO NO UTILIZADO
    /* EXPLICACIÓN DEL METODO <obtenerCitasPorVeterinario()> : despliega para leer...
        El metodo obtenerCitasPorVeterinario obtiene primero los ids de las citas asociadas a un veterinario usando su idVeterinario.
        Si no encuentra ninguna cita, devuelve una lista vacía mediante onSuccess.
        Si hay ids, recorre cada uno y obtiene la cita completa llamando a obtenerCitaPorId.
        Cada cita encontrada se guarda junto a su id en una lista de pares Pair<String, Cita>.
        Cuando termina de cargar todas las citas, las ordena por fechaHoraInicio y las devuelve; si ocurre un error, lo comunica mediante onError.
    */
    fun obtenerCitasPorVeterinario(
        idVeterinario: String,
        onSuccess: (List<Pair<String, Cita>>) -> Unit,
        onError: (String?) -> Unit
    ) {
        remoteCita.obtenerIdsCitasPorVeterinario(
            idVeterinario = idVeterinario,
            onSuccess = { listaIds ->
                if (listaIds.isEmpty()) {
                    onSuccess(emptyList())
                    return@obtenerIdsCitasPorVeterinario
                }

                val listaCitas = mutableListOf<Pair<String, Cita>>()
                var restantes = listaIds.size
                var hayError = false

                listaIds.forEach { idCita ->
                    remoteCita.obtenerCitaPorId(
                        idCita = idCita,
                        onSuccess = { cita ->
                            if (cita != null) {
                                listaCitas.add(idCita to cita)
                            }

                            restantes--
                            if (restantes == 0 && !hayError) {
                                onSuccess(listaCitas.sortedBy { it.second.fechaHoraInicio ?: "" })
                            }
                        },
                        onError = { error ->
                            if (!hayError) {
                                hayError = true
                                onError(error)
                            }
                        }
                    )
                }
            },
            onError = onError
        )
    }
    */
    /* FIXME: BORRAR => MÉTODO NO UTILIZADO
fun actualizarCita(
    idCita: String,
    cambios: Map<String, Any?>,
    onSuccess: () -> Unit,
    onError: (String?) -> Unit
) {
    remoteCita.actualizarCita(
        idCita = idCita,
        updates = cambios,
        onSuccess = onSuccess,
        onError = onError
    )
}
*/
    /* FIXME: BORRAR => MÉTODO NO UTILIZADO
fun reprogramarCita(
    idCita: String,
    nuevaFechaHoraInicio: String,
    nuevaFechaHoraFin: String,
    onSuccess: () -> Unit,
    onError: (String?) -> Unit
) {
    remoteCita.reprogramarCita(
        idCita = idCita,
        nuevaFechaHoraInicio = nuevaFechaHoraInicio,
        nuevaFechaHoraFin = nuevaFechaHoraFin,
        onSuccess = onSuccess,
        onError = onError
    )
}
*/
    /* FIXME: BORRAR => MÉTODO NO UTILIZADO
    fun cambiarEstadoCita(
        idCita: String,
        nuevoEstado: String,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
    ) {
        remoteCita.cambiarEstadoCita(
            idCita = idCita,
            nuevoEstado = nuevoEstado,
            onSuccess = onSuccess,
            onError = onError
        )
    }
    */
    /* FIXME: BORRAR => MÉTODO NO UTILIZADO
    /* EXPLICACIÓN DEL METODO <obtenerProximasCitasPorVeterinario()> : despliega para leer...
        El metodo obtenerProximasCitasPorMascota obtiene primero todas las citas asociadas a una mascota usando su idMascota.
        Después filtra la lista para quedarse únicamente con las citas cuyo estado sea "programada", ignorando mayúsculas y minúsculas.
        Estas citas filtradas representan las próximas citas pendientes de la mascota.
        Finalmente devuelve el resultado mediante onSuccess, o comunica el error mediante onError si falla la búsqueda inicial.
    */

    fun obtenerProximasCitasPorMascota(
        idMascota: String,
        onSuccess: (List<Pair<String, Cita>>) -> Unit,
        onError: (String?) -> Unit
    ) {
        obtenerCitasPorMascota(
            idMascota = idMascota,
            onSuccess = { lista ->
                val filtradas = lista.filter {
                    it.second.estadoCita.equals("programada", ignoreCase = true)
                }
                onSuccess(filtradas)
            },
            onError = onError
        )
    }
     */
    /* FIXME: BORRAR => MÉTODO NO UTILIZADO
    /* EXPLICACIÓN DEL METODO <obtenerProximasCitasPorVeterinario()> : despliega para leer...
        El metodo obtenerProximasCitasPorVeterinario obtiene primero todas las citas asociadas a un veterinario usando su idVeterinario.
        Después filtra la lista para quedarse únicamente con las citas cuyo estado sea "programada", ignorando mayúsculas y minúsculas.
        Estas citas filtradas representan las próximas citas pendientes del veterinario.
        Finalmente devuelve el resultado mediante onSuccess, o comunica el error mediante onError si falla la búsqueda inicial.
    */
    fun obtenerProximasCitasPorVeterinario(
        idVeterinario: String,
        onSuccess: (List<Pair<String, Cita>>) -> Unit,
        onError: (String?) -> Unit
    ) {
        obtenerCitasPorVeterinario(
            idVeterinario = idVeterinario,
            onSuccess = { lista ->
                val filtradas = lista.filter {
                    it.second.estadoCita.equals("programada", ignoreCase = true)
                }
                onSuccess(filtradas)
            },
            onError = onError
        )
    }
    */
}
