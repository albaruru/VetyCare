package com.example.vetycare.database.repository

import com.example.vetycare.database.remote.PropietarioRemote
import com.example.vetycare.model.entities.Propietario

class PropietarioRepository (private val remotePropietario: PropietarioRemote) {

    fun obtenerPropietario (
        authUid: String,
        Success: (Propietario) -> Unit,
        Error: (String?) -> Unit
    ) {
        remotePropietario.obtenerIdPropietarioPorAuthUid (
            authUid = authUid,
            { idProp ->
                if(idProp.isNullOrEmpty()) {
                    Error("No se ha encontrado el propietario")
                    return@obtenerIdPropietarioPorAuthUid
                }
                remotePropietario.obtenerPropietarioPorId (
                    idProp,
                    { propietario ->
                        if(propietario != null) {
                            Success(propietario)
                        }
                        else {
                            Error("No se pudieron cargar los datos del propietario")
                        }
                    },
                    Error
                )
            },
            Error
        )
    }

    fun crearPropietario (
        idProp: String,
        prop: Propietario,
        success: () -> Unit,
        error: (String?) -> Unit
    ) {
        remotePropietario.crearPropietario(
            idProp,
            prop,
            success,
            error
        )
    }

    fun actualizarPropietario (
        idProp: String,
        cambios: Map<String, Any?>,
        success: () -> Unit,
        error: (String?) -> Unit
    ) {
        remotePropietario.actualizarPropietario(
            idProp,
            cambios,
            success,
            error
        )
    }
}