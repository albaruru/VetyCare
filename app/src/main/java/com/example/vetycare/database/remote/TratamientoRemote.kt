package com.example.vetycare.database.remote

import com.example.vetycare.model.entities.Tratamiento
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference

class TratamientoRemote (private val databaseReference: DatabaseReference) {

    /* EXPLICACIÓN DEL METODO <obtenerTratamientoPorId()> : despliega para leer...
        El metodo obtenerTratamientoPorId busca un tratamiento concreto en la base de datos usando el idTratamiento recibido.
        Accede al nodo "tratamientos" y selecciona el registro que coincide con ese id.
        Si la lectura se realiza correctamente, convierte los datos obtenidos en un objeto de tipo Tratamiento.
        Finalmente devuelve el tratamiento mediante onSuccess, o un mensaje de error mediante onError si falla la lectura.
    */
    fun obtenerTratamientoPorId(
        idTratamiento: String,
        onSuccess: (Tratamiento?) -> Unit,
        onError: (String?) -> Unit
        ) {
        databaseReference.child("tratamientos").child(idTratamiento).get()
            .addOnSuccessListener { snapshot ->
                val tratamiento = snapshot.getValue(Tratamiento::class.java)
                onSuccess(tratamiento)
            }
            .addOnFailureListener {
                onError("ERROR al leer el tratamiento")
            }
    }

    /* EXPLICACIÓN DEL METODO <obtenerIdsTratamientosPorMascota()> : despliega para leer...
        El metodo obtenerIdsTratamientosPorMascota busca los tratamientos asociados a una mascota concreta usando su idMascota.
        Accede al nodo "tratamientosPorMascota" y obtiene los registros vinculados a esa mascota.
        Después recorre cada hijo y comprueba si su valor es true, indicando que la relación es válida.
        Si es válido, añade el id del tratamiento a una lista.
        Finalmente devuelve la lista de ids mediante onSuccess, o un mensaje de error con onError si falla la lectura.
    */
    fun obtenerIdsTratamientosPorMascota(
        idMascota: String,
        onSuccess: (List<String>) -> Unit,
        onError: (String?) -> Unit
        ) {
        databaseReference.child("tratamientosPorMascota").child(idMascota).get()
            .addOnSuccessListener { snapshot ->
                val listaIds = mutableListOf<String>()

                for (child in snapshot.children) {
                    val esValido = child.getValue(Boolean::class.java) ?: false
                    if (esValido) {
                        child.key?.let { listaIds.add(it) }
                    }
                }

                onSuccess(listaIds)
            }
            .addOnFailureListener {
                onError("ERROR al buscar tratamientos por mascota")
            }
    }

    /* EXPLICACIÓN DEL METODO <obtenerMedicamentosPorTratamiento()> : despliega para leer...
        El metodo obtenerMedicamentosPorTratamiento busca los medicamentos asociados a un tratamiento concreto usando su idTratamiento.
        Accede al nodo "medicamentosPorTratamiento" y selecciona el registro correspondiente a ese tratamiento.
        Si la lectura se realiza correctamente, devuelve el DataSnapshot completo mediante onSuccess para poder recorrer sus medicamentos después.
        Si ocurre algún error durante la lectura, devuelve un mensaje mediante onError.
    */
    fun obtenerMedicamentosPorTratamiento(
        idTratamiento: String,
        onSuccess: (DataSnapshot) -> Unit,
        onError: (String?) -> Unit
        ) {
        databaseReference.child("medicamentosPorTratamiento").child(idTratamiento).get()
            .addOnSuccessListener { snapshot ->
                onSuccess(snapshot)
            }
            .addOnFailureListener {
                onError("ERROR al leer medicamentos por tratamiento")
            }
    }

}