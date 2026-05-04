package com.example.vetycare.database.repository

import com.example.vetycare.database.remote.TratamientoRemote
import com.example.vetycare.model.entities.Tratamiento
import com.google.firebase.database.DataSnapshot

class TratamientoRepository (private val remoteTratamiento: TratamientoRemote) {

    /* EXPLICACIÓN DEL METODO <obtenerTratamientoPorId()> : despliega para leer...
        El metodo obtenerTratamientoPorId busca un tratamiento concreto usando el idTratamiento recibido.
        Para ello llama al metodo obtenerTratamientoPorId de remoteTratamiento, delegando en él la lectura real de la base de datos.
        Si el tratamiento existe, lo devuelve mediante success.
        Si no se encuentra ningún tratamiento, devuelve un mensaje de error; si falla la lectura, comunica el error mediante error.
    */
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

    /* EXPLICACIÓN DEL METODO <obtenerTratamientosPorMascota()> : despliega para leer...
        El metodo obtenerTratamientosPorMascota obtiene primero los ids de los tratamientos asociados a una mascota usando su idMascota.
        Si no encuentra ningún tratamiento, devuelve una lista vacía mediante success.
        Si hay ids, recorre cada uno y obtiene el tratamiento completo llamando a obtenerTratamientoPorId.
        Cada tratamiento encontrado se guarda junto a su id en una lista de pares Pair<String, Tratamiento>.
        Cuando termina de cargar todos los tratamientos, devuelve la lista mediante success; si ocurre un error, lo comunica mediante error.
    */
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

    /* EXPLICACIÓN DEL METODO <obtenerMedicamentosPorTratamiento()> : despliega para leer...
        El metodo obtenerMedicamentosPorTratamiento actúa como intermediario para obtener los medicamentos asociados a un tratamiento.
        Recibe el idTratamiento y los callbacks success y error para gestionar el resultado.
        Después llama directamente al metodo obtenerMedicamentosPorTratamiento de remoteTratamiento, delegando en él la lectura real de la base de datos.
        Finalmente devuelve el DataSnapshot mediante success, o comunica el error mediante error si ocurre algún fallo.
    */
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

}