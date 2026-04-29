package com.example.vetycare.database.repository

import com.example.vetycare.database.remote.DiagnosticoRemote
import com.example.vetycare.model.entities.Diagnostico

class DiagnosticoRepository (private val remoteDiagnostico: DiagnosticoRemote) {

    /* EXPLICACIÓN DEL METODO <obtenerTodasLasClinicasActivas()> : despliega para leer...
        El metodo obtenerDiagnosticosPorMascota obtiene primero los ids de los diagnósticos asociados a una mascota usando su idMascota.
        Si no encuentra ningún diagnóstico, devuelve una lista vacía mediante success.
        Si hay ids, recorre cada uno y obtiene el diagnóstico completo llamando a obtenerDiagnosticoPorId.
        Cada diagnóstico encontrado se guarda junto a su id en una lista de pares Pair<String, Diagnostico>.
        Cuando termina de cargar todos los diagnósticos, devuelve la lista mediante success; si ocurre un error, lo comunica mediante error.
     */
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

    /* FIXME: BORRAR => MÉTODO NO UTILIZADO
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
*/
    /* FIXME: BORRAR => MÉTODO NO UTILIZADO
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
    */
    /* FIXME: BORRAR => MÉTODO NO UTILIZADO
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
*/
    /* FIXME: BORRAR => MÉTODO NO UTILIZADO
    fun generarIdDiagnostico(
        success: (String) -> Unit,
        error: (String) -> Unit
        ) {
        remoteDiagnostico.generarIdDiagnostico(success, error)
    }
    */
}