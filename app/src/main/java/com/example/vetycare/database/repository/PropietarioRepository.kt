package com.example.vetycare.database.repository

import com.example.vetycare.database.remote.PropietarioRemote
import com.example.vetycare.model.entities.Propietario

class PropietarioRepository (private val remotePropietario: PropietarioRemote) {

    /* EXPLICACIÓN DEL METODO <obtenerPropietario()> : despliega para leer...
        El metodo obtenerPropietario busca primero el id del propietario asociado al authUid del usuario autenticado.
        Si no encuentra ningún id, detiene el proceso y devuelve un mensaje de error mediante Error.
        Si obtiene un id válido, llama a obtenerPropietarioPorId para cargar los datos completos del propietario.
        Cuando el propietario existe, devuelve tanto su id como el objeto Propietario mediante Success.
        Si no se pueden cargar los datos o falla alguna lectura, comunica el problema mediante Error.
    */
    fun obtenerPropietario (
        authUid: String,
        Success: (String, Propietario) -> Unit,
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
                            Success(idProp,propietario)
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

    /* EXPLICACIÓN DEL METODO <crearPropietario()> : despliega para leer...
        El metodo crearPropietario actúa como intermediario para registrar un nuevo propietario desde la capa actual.
        Recibe el id del propietario, el objeto Propietario y los callbacks para gestionar el resultado.
        Después llama directamente al metodo crearPropietario de remotePropietario, delegando en él el guardado real en la base de datos.
        Finalmente ejecuta success si el propietario se crea correctamente, o comunica el error mediante error si ocurre algún fallo.
    */
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

    /* EXPLICACIÓN DEL METODO <generarIdPropietario()> : despliega para leer...
        El metodo generarIdPropietario actúa como intermediario para generar un nuevo id de propietario desde la capa actual.
        Recibe los callbacks success y error para gestionar el resultado de la operación.
        Después llama directamente al metodo generarIdPropietario de remotePropietario, delegando en él la generación real del identificador.
        Finalmente devuelve el nuevo id mediante success, o comunica el error mediante error si ocurre algún fallo.
    */
    fun generarIdPropietario(
        success: (String) -> Unit,
        error: (String) -> Unit
        ) {
        remotePropietario.generarIdPropietario(success, error)
    }

    /* FIXME: BORRAR => MÉTODO NO UTILIZADO
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
*/
}