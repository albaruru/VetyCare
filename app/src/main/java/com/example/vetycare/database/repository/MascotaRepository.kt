package com.example.vetycare.database.repository

import com.example.vetycare.database.remote.MascotaRemote
import com.example.vetycare.model.entities.Mascota
import com.google.firebase.database.DatabaseReference

class MascotaRepository (private val remoteMascota: MascotaRemote) {

    /* EXPLICACIÓN DEL METODO <obtenerMascotaPorId()> : despliega para leer...
        El metodo obtenerMascotaPorId busca una mascota concreta usando el identificador idMasc.
        Para ello llama al metodo obtenerMascotaPorId de remoteMascota, delegando en él la lectura real de la base de datos.
        Si la mascota existe, la devuelve mediante Success.
        Si no se encuentra ninguna mascota, devuelve un mensaje de error indicando "No se encontró mascota";
        si falla la lectura, comunica el error mediante Error.
    */
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

    /* EXPLICACIÓN DEL METODO <obtenerMascotasPorPropietario()> : despliega para leer...
        El metodo obtenerMascotasPorPropietario obtiene primero los ids de las mascotas asociadas a un propietario usando idProp.
        Si no encuentra ninguna mascota, devuelve una lista vacía mediante Success.
        Si hay ids, recorre cada uno y obtiene la mascota completa llamando a obtenerMascotaPorId.
        Cada mascota encontrada se guarda junto a su id en una lista de pares Pair<String, Mascota>.
        Cuando termina de cargar todas las mascotas, devuelve la lista mediante Success; si ocurre un error, lo comunica mediante Error.
    */
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

    /* EXPLICACIÓN DEL METODO <registrarMascota()> : despliega para leer...
        El metodo registrarMascota actúa como intermediario para guardar una nueva mascota desde la capa actual.
        Recibe el id de la mascota, el objeto Mascota y los callbacks para gestionar el resultado.
        Después llama directamente al metodo registrarMascota de remoteMascota, delegando en él el registro real en la base de datos.
        Finalmente ejecuta Success si la operación se completa correctamente, o Error si ocurre algún fallo.
    */
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

    /* EXPLICACIÓN DEL METODO <generarIdMascota()> : despliega para leer...
        El metodo generarIdMascota actúa como intermediario para generar un nuevo id de mascota desde la capa actual.
        Recibe los callbacks onSuccess y onError para gestionar el resultado de la operación.
        Después llama directamente al metodo generarIdMascota de remoteMascota, delegando en él la generación real del identificador.
        Finalmente devuelve el nuevo id mediante onSuccess, o comunica el error mediante onError si ocurre algún fallo.
    */
    fun generarIdMascota(
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
        ) {
        remoteMascota.generarIdMascota(onSuccess, onError)
    }

    /* EXPLICACIÓN DEL METODO <eliminarMascota()> : despliega para leer...
        El metodo eliminarMascota actúa como intermediario para eliminar una mascota desde la capa actual.
        Recibe el identificador idMasc y los callbacks necesarios para gestionar el resultado.
        Después llama directamente al metodo eliminarMascota de remoteMascota, delegando en él la eliminación real en la base de datos.
        Finalmente ejecuta onSuccess si la mascota se elimina correctamente, o comunica el error mediante onError si ocurre algún fallo.
    */
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