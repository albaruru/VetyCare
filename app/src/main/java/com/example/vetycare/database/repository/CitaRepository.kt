package com.example.vetycare.database.repository

import com.example.vetycare.database.remote.CitaRemote
import com.example.vetycare.model.entities.Cita

class CitaRepository (private val remoteCita: CitaRemote) {

    fun generarIdCita(
        onSuccess: (String) -> Unit,
        onError: (String?) -> Unit
    ) {
        remoteCita.generarIdCita(
            onSuccess = onSuccess,
            onError = onError
        )
    }

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
