package com.example.vetycare.database.repository

import com.example.vetycare.database.remote.TratamientoRemote
import com.example.vetycare.model.entities.Tratamiento
import com.google.firebase.database.DataSnapshot

class TratamientoRepository (private val remoteTratamiento: TratamientoRemote) {
    fun obtenerTratamientoPorId(
        idTratamiento: String,
        success: (Tratamiento) -> Unit,
        error: (String?) -> Unit
        ) {
        remoteTratamiento.obtenerTratamientoPorId(
            idTratamiento,
            { tratamiento ->
                if (tratamiento != null) {
                    success(tratamiento)
                } else {
                    error("No se encontró el tratamiento")
                }
            },
            error
        )
    }

    fun obtenerTratamientosPorMascota(
        idMascota: String,
        success: (List<Pair<String, Tratamiento>>) -> Unit,
        error: (String?) -> Unit
        ) {
        remoteTratamiento.obtenerIdsTratamientosPorMascota(
            idMascota,
            { listaIds ->
                if (listaIds.isEmpty()) {
                    success(emptyList())

                    return@obtenerIdsTratamientosPorMascota
                }

                val listaTratamientos = mutableListOf<Pair<String, Tratamiento>>()
                var restantes = listaIds.size
                var hayError = false

                listaIds.forEach { idTratamiento ->
                    remoteTratamiento.obtenerTratamientoPorId(
                        idTratamiento,
                        { tratamiento ->
                            if (tratamiento != null) {
                                listaTratamientos.add(idTratamiento to tratamiento)
                            }

                            restantes--
                            if (restantes == 0 && !hayError) {
                                success(listaTratamientos)
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

    fun registrarTratamiento(
        idTratamiento: String,
        tratamiento: Tratamiento,
        success: () -> Unit,
        error: (String?) -> Unit
        ) {
        remoteTratamiento.registrarTratamiento(
            idTratamiento,
            tratamiento,
            success,
            error
        )
    }

    fun actualizarTratamiento(
        idTratamiento: String,
        cambios: Map<String, Any?>,
        success: () -> Unit,
        error: (String?) -> Unit
        ) {
        remoteTratamiento.actualizarTratamiento(
            idTratamiento,
            cambios,
            success,
            error
        )
    }

    fun obtenerMedicamentosPorTratamiento(
        idTratamiento: String,
        success: (DataSnapshot) -> Unit,
        error: (String?) -> Unit
        ) {
        remoteTratamiento.obtenerMedicamentosPorTratamiento(
            idTratamiento,
            success,
            error
        )
    }

    fun guardarMedicamentoEnTratamiento(
        idTratamiento: String,
        idMedicamento: String,
        datosMedicamento: Map<String, Any?>,
        success: () -> Unit,
        error: (String?) -> Unit
        ) {
        remoteTratamiento.guardarMedicamentoEnTratamiento(
            idTratamiento,
            idMedicamento,
            datosMedicamento,
            success,
            error
        )
    }

    fun generarIdTratamiento(
        success: (String) -> Unit,
        error: (String) -> Unit
        ) {
        remoteTratamiento.generarIdTratamiento(success, error)
    }
}