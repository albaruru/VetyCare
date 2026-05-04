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

}