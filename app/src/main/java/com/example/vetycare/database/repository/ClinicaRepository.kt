package com.example.vetycare.database.repository

import com.example.vetycare.database.remote.ClinicaRemote
import com.example.vetycare.model.entities.Clinica

class ClinicaRepository(private val remoteClinica: ClinicaRemote) {

    /* EXPLICACIÓN DEL METODO <obtenerTodasLasClinicasActivas()> : despliega para leer...
        El metodo obtenerTodasLasClinicasActivas obtiene primero todas las clínicas llamando a obtenerTodasLasClinicas de remoteClinica.
        Cuando recibe la lista completa, filtra únicamente aquellas clínicas cuyo campo activa sea true.
        De esta forma devuelve solo las clínicas que están disponibles o habilitadas en la aplicación.
        Finalmente envía la lista filtrada mediante onSuccess, o comunica el error mediante onError si falla la lectura inicial.
    */
    fun obtenerTodasLasClinicasActivas(
        onSuccess: (List<Clinica>) -> Unit,
        onError: (String?) -> Unit
        ) {
        remoteClinica.obtenerTodasLasClinicas(
            onSuccess = { lista ->
                onSuccess(lista.filter { it.activa == true })
            },
            onError = onError
        )
    }

    /* EXPLICACIÓN DEL METODO <obtenerClinicasPorComunidad()> : despliega para leer...
        El metodo obtenerClinicasPorComunidad obtiene primero los ids de las clínicas asociadas a una comunidad autónoma usando claveComunidad.
        Si no encuentra ninguna clínica, devuelve una lista vacía mediante onSuccess.
        Si hay ids, recorre cada uno y obtiene la clínica completa llamando a obtenerClinicaPorId.
        Cada clínica encontrada se guarda junto a su id en una lista de pares Pair<String, Clinica>.
        Cuando termina de cargar todas las clínicas, las ordena por nombre y las devuelve; si ocurre un error, lo comunica mediante onError.
    */
    fun obtenerClinicasPorComunidad(
        claveComunidad: String,
        onSuccess: (List<Pair<String, Clinica>>) -> Unit,
        onError: (String?) -> Unit
    ) {
        remoteClinica.obtenerIdsClinicasPorComunidad(
            claveComunidad = claveComunidad,
            onSuccess = { listaIds ->
                if (listaIds.isEmpty()) {
                    onSuccess(emptyList())
                    return@obtenerIdsClinicasPorComunidad
                }

                val listaClinicas = mutableListOf<Pair<String, Clinica>>()
                var restantes = listaIds.size
                var hayError = false

                listaIds.forEach { idClinica ->
                    remoteClinica.obtenerClinicaPorId(
                        idClinica = idClinica,
                        onSuccess = { clinica ->
                            if (clinica != null) {
                                listaClinicas.add(idClinica to clinica)
                            }

                            restantes--
                            if (restantes == 0 && !hayError) {
                                onSuccess(listaClinicas.sortedBy { it.second.nombre ?: "" })
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

    /* EXPLICACIÓN DEL METODO <obtenerClinicasActivasPorComunidad()> : despliega para leer...
        El metodo obtenerClinicasActivasPorComunidad obtiene primero las clínicas asociadas a una comunidad usando claveComunidad.
        Para ello reutiliza el metodo obtenerClinicasPorComunidad, que devuelve la lista completa de clínicas de esa comunidad.
        Después filtra el resultado para quedarse solo con las clínicas cuyo campo activa sea true.
        Finalmente devuelve la lista de clínicas activas mediante onSuccess, o comunica el error mediante onError si falla la búsqueda inicial.
    */
    fun obtenerClinicasActivasPorComunidad(
        claveComunidad: String,
        onSuccess: (List<Pair<String, Clinica>>) -> Unit,
        onError: (String?) -> Unit
    ) {
        obtenerClinicasPorComunidad(
            claveComunidad = claveComunidad,
            onSuccess = { lista ->
                val activas = lista.filter { it.second.activa == true }
                onSuccess(activas)
            },
            onError = onError
        )
    }

    /* FIXME: BORRAR => METODO NO UTILIZADO
    fun obtenerClinicaPorId(
        idClinica: String,
        onSuccess: (Clinica) -> Unit,
        onError: (String?) -> Unit
    ) {
        remoteClinica.obtenerClinicaPorId(
            idClinica = idClinica,
            onSuccess = { clinica ->
                if (clinica != null) {
                    onSuccess(clinica)
                } else {
                    onError("No se encontró la clínica")
                }
            },
            onError = onError
        )
    }
    */
    /* FIXME: BORRAR => MÉTODO NO UTILIZADO
fun registrarClinica(
    idClinica: String,
    clinica: Clinica,
    claveComunidad: String,
    onSuccess: () -> Unit,
    onError: (String?) -> Unit
) {
    remoteClinica.registrarClinica(
        idClinica = idClinica,
        clinica = clinica,
        claveComunidad = claveComunidad,
        onSuccess = onSuccess,
        onError = onError
    )
}
*/
    /* FIXME: BORRAR => MÉTODO NO UTILIZADO
    fun actualizarClinica(
        idClinica: String,
        cambios: Map<String, Any?>,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
    ) {
        remoteClinica.actualizarClinica(
            idClinica = idClinica,
            cambios = cambios,
            onSuccess = onSuccess,
            onError = onError
        )
    }
    */
    /* FIXME: BORRAR => MÉTODO NO UTILIZADO
    fun activarClinica(
        idClinica: String,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
    ) {
        remoteClinica.activarClinica(
            idClinica = idClinica,
            onSuccess = onSuccess,
            onError = onError
        )
    }
    */
    /* FIXME: BORRAR => MÉTODO NO UTILIZADO
    fun desactivarClinica(
        idClinica: String,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
    ) {
        remoteClinica.desactivarClinica(
            idClinica = idClinica,
            onSuccess = onSuccess,
            onError = onError
        )
    }
    */
    /* FIXME: BORRAR => MÉTODO NO UTILIZADO
    fun actualizarCoordenadasClinica(
        idClinica: String,
        latitud: Double,
        longitud: Double,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
    ) {
        remoteClinica.actualizarCoordenadasClinica(
            idClinica = idClinica,
            latitud = latitud,
            longitud = longitud,
            onSuccess = onSuccess,
            onError = onError
        )
    }
    */
    /* FIXME: BORRAR => MÉTODO NO UTILIZADO
    fun actualizarComunidadClinica(
        idClinica: String,
        claveComunidadAnterior: String,
        claveComunidadNueva: String,
        comunidadAutonomaVisible: String,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
    ) {
        remoteClinica.actualizarComunidadClinica(
            idClinica = idClinica,
            claveComunidadAnterior = claveComunidadAnterior,
            claveComunidadNueva = claveComunidadNueva,
            comunidadAutonomaVisible = comunidadAutonomaVisible,
            onSuccess = onSuccess,
            onError = onError
        )
    }
    */
}