package com.example.vetycare.database.repository

import com.example.vetycare.database.remote.MascotaRemote
import com.example.vetycare.model.entities.Mascota
import com.google.firebase.database.DatabaseReference

class MascotaRepository (private val remoteMascota: MascotaRemote) {

    fun obtenerMascotaPorId (
        idMasc: String,
        Success: (Mascota) -> Unit,
        Error: (String?) -> Unit
        ) {
        remoteMascota.obtenerMascotaPorId(
            idMasc,
            { mascota ->
                if (mascota != null) {
                    Success(mascota)
                }
                else {
                    Error("No se encontró mascota")
                }
            },
            Error
        )
    }

    fun obtenerMascotasPorPropietario (
        idProp: String,
        Success: (List<Pair<String, Mascota>>) -> Unit,
        Error: (String?) -> Unit
        ) {
        remoteMascota.obtenerIdsMascotasPorPropietario(
            idProp,
            { listaIds ->
                if (listaIds.isEmpty()) {
                    Success(emptyList())
                    return@obtenerIdsMascotasPorPropietario
                }
                val listaMascotas = mutableListOf<Pair<String,Mascota>>()
                var restantes = listaIds.size
                var hayError = false

                listaIds.forEach { idMasc ->
                    remoteMascota.obtenerMascotaPorId(
                        idMasc,
                        { mascota ->
                            if (mascota != null) {
                                listaMascotas.add(idMasc to mascota)
                            }
                            restantes--
                            if (restantes == 0 && !hayError) {
                                Success(listaMascotas)
                            }
                        },
                        { error ->
                            if (!hayError) {
                                hayError = true
                                Error(error)
                            }
                        }
                    )
                }
            },
            Error
        )
    }

    fun registrarMascota (
        idMasc: String,
        masc: Mascota,
        Success: () -> Unit,
        Error: (String?) -> Unit
        ) {
        remoteMascota.registrarMascota(
            idMasc,
            masc,
            Success,
            Error
        )
    }

    fun actualizarMascota (
        idMasc: String,
        cambios: Map <String,Any?>,
        Success: () -> Unit,
        Error: (String?) -> Unit
        ) {
        remoteMascota.actualizarMascota(
            idMasc,
            cambios,
            Success,
            Error
        )
    }

    fun generarIdMascota(
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
        ) {
        remoteMascota.generarIdMascota(onSuccess, onError)
    }

    fun eliminarMascota(
        idMasc: String,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
    ) {
        remoteMascota.eliminarMascota(
            idMasc,
            onSuccess,
            onError
        )
    }
}