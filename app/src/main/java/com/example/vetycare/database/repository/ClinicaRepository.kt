package com.example.vetycare.database.repository

import com.example.vetycare.database.remote.ClinicaRemote
import com.example.vetycare.model.entities.Clinica

class ClinicaRepository(private val remoteClinica: ClinicaRemote) {

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