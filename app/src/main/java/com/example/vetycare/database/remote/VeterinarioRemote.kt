package com.example.vetycare.database.remote

import com.example.vetycare.model.entities.Veterinario
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference

class VeterinarioRemote(private val databaseReference: DatabaseReference) {

    /* EXPLICACIÓN DEL METODO <extraerIdsBoolean()> : despliega para leer...
        El metodo extraerIdsBoolean recibe un DataSnapshot y extrae los ids de los registros que estén marcados como válidos.
        Recorre cada hijo del snapshot y obtiene su valor como Boolean.
        Si el valor es true, añade la clave de ese hijo a la lista de ids.
        Finalmente devuelve la lista con todos los ids válidos encontrados.
    */
    private fun extraerIdsBoolean(snapshot: DataSnapshot): List<String> {
        val listaIds = mutableListOf<String>()

        for (child in snapshot.children) {
            val esValido = child.getValue(Boolean::class.java) ?: false
            if (esValido) {
                child.key?.let { listaIds.add(it) }
            }
        }

        return listaIds
    }

    /* EXPLICACIÓN DEL METODO <obtenerVeterinarioPorId()> : despliega para leer...
        El metodo obtenerVeterinarioPorId busca un veterinario concreto en la base de datos usando el idVeterinario recibido.
        Accede al nodo "veterinarios" y selecciona el registro que coincide con ese id.
        Si la lectura se realiza correctamente, convierte los datos obtenidos en un objeto de tipo Veterinario.
        Después asigna manualmente el id al objeto y lo devuelve mediante onSuccess; si falla la lectura, devuelve un mensaje con onError.
    */
    fun obtenerVeterinarioPorId(
        idVeterinario: String,
        onSuccess: (Veterinario?) -> Unit,
        onError: (String?) -> Unit
    ) {
        databaseReference.child("veterinarios").child(idVeterinario).get()
            .addOnSuccessListener { snapshot ->
                val veterinario = snapshot.getValue(Veterinario::class.java)
                if (veterinario != null) {
                    veterinario.id = idVeterinario
                }
                onSuccess(veterinario)
            }
            .addOnFailureListener {
                onError("ERROR al leer el veterinario")
            }
    }

    /* EXPLICACIÓN DEL METODO <obtenerIdsVeterinariosPorClinica()> : despliega para leer...
        El metodo obtenerIdsVeterinariosPorClinica busca los veterinarios asociados a una clínica concreta usando su idClinica.
        Accede al nodo "veterinariosPorClinica" y obtiene los registros vinculados a esa clínica.
        Si la lectura se realiza correctamente, llama a extraerIdsBoolean(snapshot) para extraer solo los ids marcados como válidos.
        Finalmente devuelve la lista de ids mediante onSuccess, o un mensaje de error con onError si falla la búsqueda.
    */
    fun obtenerIdsVeterinariosPorClinica(
        idClinica: String,
        onSuccess: (List<String>) -> Unit,
        onError: (String?) -> Unit
    ) {
        databaseReference.child("veterinariosPorClinica").child(idClinica).get()
            .addOnSuccessListener { snapshot ->
                onSuccess(extraerIdsBoolean(snapshot))
            }
            .addOnFailureListener {
                onError("ERROR al buscar veterinarios por clínica")
            }
    }

    /* EXPLICACIÓN DEL METODO <obtenerIdsVeterinariosPorColegio()> : despliega para leer...
        El metodo obtenerIdsVeterinariosPorColegio busca los veterinarios asociados a un colegio concreto usando su idColegio.
        Accede al nodo "veterinariosPorColegio" y obtiene los registros vinculados a ese colegio.
        Si la lectura se realiza correctamente, llama a extraerIdsBoolean(snapshot) para extraer solo los ids marcados como válidos.
        Finalmente devuelve la lista de ids mediante onSuccess, o un mensaje de error con onError si falla la búsqueda.
    */
    fun obtenerIdsVeterinariosPorColegio(
        idColegio: String,
        onSuccess: (List<String>) -> Unit,
        onError: (String?) -> Unit
    ) {
        databaseReference.child("veterinariosPorColegio").child(idColegio).get()
            .addOnSuccessListener { snapshot ->
                onSuccess(extraerIdsBoolean(snapshot))
            }
            .addOnFailureListener {
                onError("ERROR al buscar veterinarios por colegio")
            }
    }
}