package com.example.vetycare.database.repository

import com.example.vetycare.database.remote.VeterinarioRemote
import com.example.vetycare.model.entities.Veterinario

class VeterinarioRepository(private val remoteVeterinario: VeterinarioRemote) {

    /* EXPLICACIÓN DEL METODO <obtenerVeterinariosPorClinica()> : despliega para leer...

    */
    fun obtenerVeterinariosPorClinica(
        idClinica: String,
        onSuccess: (List<Pair<String, Veterinario>>) -> Unit,
        onError: (String?) -> Unit
    ) {
        remoteVeterinario.obtenerIdsVeterinariosPorClinica(
            idClinica = idClinica,
            onSuccess = { listaIds ->
                if (listaIds.isEmpty()) {
                    onSuccess(emptyList())
                    return@obtenerIdsVeterinariosPorClinica
                }

                val listaVeterinarios = mutableListOf<Pair<String, Veterinario>>()
                var restantes = listaIds.size
                var hayError = false

                listaIds.forEach { idVeterinario ->
                    remoteVeterinario.obtenerVeterinarioPorId(
                        idVeterinario = idVeterinario,
                        onSuccess = { veterinario ->
                            if (veterinario != null) {
                                listaVeterinarios.add(idVeterinario to veterinario)
                            }

                            restantes--
                            if (restantes == 0 && !hayError) {
                                onSuccess(
                                    listaVeterinarios.sortedBy {
                                        "${it.second.nombre ?: ""} ${it.second.apellido ?: ""}".trim()
                                    }
                                )
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

    /* EXPLICACIÓN DEL METODO <obtenerVeterinariosActivosPorClinica()> : despliega para leer...

    */
    fun obtenerVeterinariosActivosPorClinica(
        idClinica: String,
        onSuccess: (List<Pair<String, Veterinario>>) -> Unit,
        onError: (String?) -> Unit
    ) {
        obtenerVeterinariosPorClinica(
            idClinica = idClinica,
            onSuccess = { lista ->
                onSuccess(lista.filter { it.second.activa == true })
            },
            onError = onError
        )
    }

    /* EXPLICACIÓN DEL METODO <obtenerVeterinariosPorColegio()> : despliega para leer...

    */
    fun obtenerVeterinariosPorColegio(
        idColegio: String,
        onSuccess: (List<Pair<String, Veterinario>>) -> Unit,
        onError: (String?) -> Unit
    ) {
        remoteVeterinario.obtenerIdsVeterinariosPorColegio(
            idColegio = idColegio,
            onSuccess = { listaIds ->
                if (listaIds.isEmpty()) {
                    onSuccess(emptyList())
                    return@obtenerIdsVeterinariosPorColegio
                }

                val listaVeterinarios = mutableListOf<Pair<String, Veterinario>>()
                var restantes = listaIds.size
                var hayError = false

                listaIds.forEach { idVeterinario ->
                    remoteVeterinario.obtenerVeterinarioPorId(
                        idVeterinario = idVeterinario,
                        onSuccess = { veterinario ->
                            if (veterinario != null) {
                                listaVeterinarios.add(idVeterinario to veterinario)
                            }

                            restantes--
                            if (restantes == 0 && !hayError) {
                                onSuccess(
                                    listaVeterinarios.sortedBy {
                                        "${it.second.nombre ?: ""} ${it.second.apellido ?: ""}".trim()
                                    }
                                )
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

    /* FIXME: BORRAR => MÉTODO NO UTILIZADO
fun obtenerTodosLosVeterinariosActivos(
    onSuccess: (List<Veterinario>) -> Unit,
    onError: (String?) -> Unit
) {
    remoteVeterinario.obtenerTodosLosVeterinarios(
        onSuccess = { lista ->
            onSuccess(lista.filter { it.activa == true })
        },
        onError = onError
    )
}
*/
    /* FIXME: BORRAR => MÉTODO NO UTILIZADO
    fun obtenerVeterinarioPorId(
        idVeterinario: String,
        onSuccess: (Veterinario) -> Unit,
        onError: (String?) -> Unit
    ) {
        remoteVeterinario.obtenerVeterinarioPorId(
            idVeterinario = idVeterinario,
            onSuccess = { veterinario ->
                if (veterinario != null) {
                    onSuccess(veterinario)
                } else {
                    onError("No se encontró el veterinario")
                }
            },
            onError = onError
        )
    }
    */
    /* FIXME: BORRAR => MÉTODO NO UTILIZADO
fun obtenerVeterinariosActivosPorColegio(
    idColegio: String,
    onSuccess: (List<Pair<String, Veterinario>>) -> Unit,
    onError: (String?) -> Unit
) {
    obtenerVeterinariosPorColegio(
        idColegio = idColegio,
        onSuccess = { lista ->
            onSuccess(lista.filter { it.second.activa == true })
        },
        onError = onError
    )
}
*/
    /* FIXME: BORRAR => MÉTODO NO UTILIZADO
    fun registrarVeterinario(
        idVeterinario: String,
        veterinario: Veterinario,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
    ) {
        remoteVeterinario.registrarVeterinario(
            idVeterinario = idVeterinario,
            veterinario = veterinario,
            onSuccess = onSuccess,
            onError = onError
        )
    }
    */
    /* FIXME: BORRAR => MÉTODO NO UTILIZADO
    fun actualizarVeterinario(
        idVeterinario: String,
        cambios: Map<String, Any?>,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
    ) {
        remoteVeterinario.actualizarVeterinario(
            idVeterinario = idVeterinario,
            cambios = cambios,
            onSuccess = onSuccess,
            onError = onError
        )
    }
    */
    /* FIXME: BORRAR => MÉTODO NO UTILIZADO
    fun activarVeterinario(
        idVeterinario: String,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
    ) {
        remoteVeterinario.activarVeterinario(
            idVeterinario = idVeterinario,
            onSuccess = onSuccess,
            onError = onError
        )
    }
    */
    /* FIXME: BORRAR => MÉTODO NO UTILIZADO
    fun desactivarVeterinario(
        idVeterinario: String,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
    ) {
        remoteVeterinario.desactivarVeterinario(
            idVeterinario = idVeterinario,
            onSuccess = onSuccess,
            onError = onError
        )
    }
    */
    /* FIXME: BORRAR => MÉTODO NO UTILIZADO
    fun actualizarClinicaVeterinario(
        idVeterinario: String,
        idClinicaAnterior: String,
        idClinicaNueva: String,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
    ) {
        remoteVeterinario.actualizarClinicaVeterinario(
            idVeterinario = idVeterinario,
            idClinicaAnterior = idClinicaAnterior,
            idClinicaNueva = idClinicaNueva,
            onSuccess = onSuccess,
            onError = onError
        )
    }
    */
    /* FIXME: BORRAR => MÉTODO NO UTILIZADO
    fun actualizarColegioVeterinario(
        idVeterinario: String,
        idColegioAnterior: String,
        idColegioNuevo: String,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
    ) {
        remoteVeterinario.actualizarColegioVeterinario(
            idVeterinario = idVeterinario,
            idColegioAnterior = idColegioAnterior,
            idColegioNuevo = idColegioNuevo,
            onSuccess = onSuccess,
            onError = onError
        )
    }
    */
}