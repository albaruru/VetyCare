package com.example.vetycare.database.repository

import com.example.vetycare.database.remote.DiagnosticoRemote
import com.example.vetycare.model.entities.Diagnostico

class DiagnosticoRepository (private val remoteDiagnostico: DiagnosticoRemote) {

    fun obtenerDiagnosticoPorId(
        idDiagnostico: String,
        success: (Diagnostico) -> Unit,
        error: (String?) -> Unit
        ) {
        remoteDiagnostico.obtenerDiagnosticoPorId(
            idDiagnostico,
            { diagnostico ->
                if (diagnostico != null) {
                    success(diagnostico)
                } else {
                    error("No se encontró el diagnóstico")
                }
            },
            error
        )
    }

    fun obtenerDiagnosticosPorMascota(
        idMascota: String,
        success: (List<Pair<String, Diagnostico>>) -> Unit,
        error: (String?) -> Unit
        ) {
        remoteDiagnostico.obtenerIdsDiagnosticosPorMascota(
            idMascota,
            { listaIds ->
                if (listaIds.isEmpty()) {
                    success(emptyList())
                    return@obtenerIdsDiagnosticosPorMascota
                }

                val listaDiagnosticos = mutableListOf<Pair<String, Diagnostico>>()
                var restantes = listaIds.size
                var hayError = false

                listaIds.forEach { idDiagnostico ->
                    remoteDiagnostico.obtenerDiagnosticoPorId(
                        idDiagnostico,
                        { diagnostico ->
                            if (diagnostico != null) {
                                listaDiagnosticos.add(idDiagnostico to diagnostico)
                            }

                            restantes--
                            if (restantes == 0 && !hayError) {
                                success(listaDiagnosticos)
                            }
                        },
                        { mensajeError ->
                            if (!hayError) {
                                hayError = true
                                error(mensajeError)
                            }
                        }
                    )
                }
            },
            error
        )
    }

    fun registrarDiagnostico(
        idDiagnostico: String,
        diagnostico: Diagnostico,
        success: () -> Unit,
        error: (String?) -> Unit
        ) {
        remoteDiagnostico.registrarDiagnostico(
            idDiagnostico,
            diagnostico,
            success,
            error
        )
    }

    fun actualizarDiagnostico(
        idDiagnostico: String,
        cambios: Map<String, Any?>,
        success: () -> Unit,
        error: (String?) -> Unit
        ) {
        remoteDiagnostico.actualizarDiagnostico(
            idDiagnostico,
            cambios,
            success,
            error
        )
    }

    fun generarIdDiagnostico(
        success: (String) -> Unit,
        error: (String) -> Unit
        ) {
        remoteDiagnostico.generarIdDiagnostico(success, error)
    }
}