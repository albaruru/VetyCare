package com.example.vetycare.database.repository

import com.example.vetycare.database.remote.VeterinarioRemote
import com.example.vetycare.model.entities.Veterinario

class VeterinarioRepository(private val remoteVeterinario: VeterinarioRemote) {

    /* EXPLICACIÓN DEL METODO <obtenerVeterinariosPorClinica()> : despliega para leer...
        El metodo obtenerVeterinariosPorClinica obtiene primero los ids de los veterinarios asociados a una clínica usando su idClinica.
        Si no encuentra ningún veterinario, devuelve una lista vacía mediante onSuccess.
        Si hay ids, recorre cada uno y obtiene los datos completos del veterinario llamando a obtenerVeterinarioPorId.
        Cada veterinario encontrado se guarda junto a su id en una lista de pares Pair<String, Veterinario>.
        Cuando termina de cargar todos los veterinarios, los ordena por nombre y apellido, y devuelve la lista; si ocurre un error, lo comunica mediante onError.
    */
    fun obtenerVeterinariosPorClinica(
        idClinica: String,
        onSuccess: (List<Pair<String, Veterinario>>) -> Unit,
        onError: (String?) -> Unit
    ) {
        remoteVeterinario.obtenerIdsVeterinariosPorClinica(
            idClinica = idClinica,
            onSuccess = { listaIds ->
                if (listaIds.isEmpty()) {
                    onSuccess(emptyList())
                    return@obtenerIdsVeterinariosPorClinica
                }

                val listaVeterinarios = mutableListOf<Pair<String, Veterinario>>()
                var restantes = listaIds.size
                var hayError = false

                listaIds.forEach { idVeterinario ->
                    remoteVeterinario.obtenerVeterinarioPorId(
                        idVeterinario = idVeterinario,
                        onSuccess = { veterinario ->
                            if (veterinario != null) {
                                listaVeterinarios.add(idVeterinario to veterinario)
                            }

                            restantes--
                            if (restantes == 0 && !hayError) {
                                onSuccess(
                                    listaVeterinarios.sortedBy {
                                        "${it.second.nombre ?: ""} ${it.second.apellido ?: ""}".trim()
                                    }
                                )
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

    /* EXPLICACIÓN DEL METODO <obtenerVeterinariosActivosPorClinica()> : despliega para leer...
        El metodo obtenerVeterinariosActivosPorClinica obtiene primero todos los veterinarios asociados a una clínica usando su idClinica.
        Para ello reutiliza el metodo obtenerVeterinariosPorClinica, que devuelve la lista completa de veterinarios de esa clínica.
        Después filtra la lista para quedarse únicamente con los veterinarios cuyo campo activa sea true.
        Finalmente devuelve los veterinarios activos mediante onSuccess, o comunica el error mediante onError si falla la búsqueda inicial.
    */
    fun obtenerVeterinariosActivosPorClinica(
        idClinica: String,
        onSuccess: (List<Pair<String, Veterinario>>) -> Unit,
        onError: (String?) -> Unit
    ) {
        obtenerVeterinariosPorClinica(
            idClinica = idClinica,
            onSuccess = { lista ->
                onSuccess(lista.filter { it.second.activa == true })
            },
            onError = onError
        )
    }

    /* EXPLICACIÓN DEL METODO <obtenerVeterinariosPorColegio()> : despliega para leer...
        El metodo obtenerVeterinariosPorColegio obtiene primero los ids de los veterinarios asociados a un colegio usando su idColegio.
        Si no encuentra ningún veterinario, devuelve una lista vacía mediante onSuccess.
        Si hay ids, recorre cada uno y obtiene los datos completos del veterinario llamando a obtenerVeterinarioPorId.
        Cada veterinario encontrado se guarda junto a su id en una lista de pares Pair<String, Veterinario>.
        Cuando termina de cargar todos los veterinarios, los ordena por nombre y apellido, y devuelve la lista; si ocurre un error, lo comunica mediante onError.
    */
    fun obtenerVeterinariosPorColegio(
        idColegio: String,
        onSuccess: (List<Pair<String, Veterinario>>) -> Unit,
        onError: (String?) -> Unit
    ) {
        remoteVeterinario.obtenerIdsVeterinariosPorColegio(
            idColegio = idColegio,
            onSuccess = { listaIds ->
                if (listaIds.isEmpty()) {
                    onSuccess(emptyList())
                    return@obtenerIdsVeterinariosPorColegio
                }

                val listaVeterinarios = mutableListOf<Pair<String, Veterinario>>()
                var restantes = listaIds.size
                var hayError = false

                listaIds.forEach { idVeterinario ->
                    remoteVeterinario.obtenerVeterinarioPorId(
                        idVeterinario = idVeterinario,
                        onSuccess = { veterinario ->
                            if (veterinario != null) {
                                listaVeterinarios.add(idVeterinario to veterinario)
                            }

                            restantes--
                            if (restantes == 0 && !hayError) {
                                onSuccess(
                                    listaVeterinarios.sortedBy {
                                        "${it.second.nombre ?: ""} ${it.second.apellido ?: ""}".trim()
                                    }
                                )
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
}