package com.example.vetycare.database.repository

import com.example.vetycare.database.remote.PropietarioRemote
import com.example.vetycare.model.entities.Propietario

class PropietarioRepository (private val remotePropietario: PropietarioRemote) {

    fun obtenerPropietario (
        authUid: String,
        onSucces: (Propietario) -> Unit,
        onError: (String?) -> Unit) {

        remotePropietario.obtenerIdPropietarioPorAuthUid(
            authUid = authUid,
            onSuccess = { idPropietario ->
                if(idPropietario.isNullOrEmpty()) {
                    onError("No se ha encontrado el propietario")
                    return@obtenerIdPropietarioPorAuthUid
                }
                remotePropietario.obtenerPropietarioPorId(
                    idPropietario = idPropietario,
                    onSuccess = { propietario ->
                        if(propietario != null) {
                            onSucces(propietario)
                        }
                        else {
                            onError("No se pudieron cargar los datos del propietario")
                        }
                    },
                    onError = onError
                )
            },
            onError = onError
        )
    }
}