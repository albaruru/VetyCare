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

    /* FIXME: BORRAR => MÉTODO NO UTILIZADO
/* EXPLICACIÓN DEL METODO <registrarTratamiento()> : despliega para leer...
    El metodo registrarTratamiento guarda un nuevo tratamiento en la base de datos usando el idTratamiento recibido.
    Primero registra el objeto tratamiento dentro del nodo "tratamientos".
    Si esa escritura se realiza correctamente, crea también una referencia en "tratamientosPorMascota" usando el id de la mascota.
    Esta referencia permite localizar después los tratamientos asociados a una mascota concreta.
    Finalmente ejecuta onSuccess si se guarda bien, o devuelve un mensaje mediante onError si falla alguno de los pasos.
*/
fun registrarTratamiento(
    idTratamiento: String,
    tratamiento: Tratamiento,
    onSuccess: () -> Unit,
    onError: (String?) -> Unit
    ) {
    databaseReference.child("tratamientos").child(idTratamiento).setValue(tratamiento)
        .addOnSuccessListener {
            databaseReference.child("tratamientosPorMascota")
                .child(tratamiento.idMascota.toString())
                .child(idTratamiento)
                .setValue(true)
                .addOnSuccessListener {
                    onSuccess()
                }
                .addOnFailureListener {
                    onError("Tratamiento creado, pero error al guardar índice por mascota")
                }
        }
        .addOnFailureListener {
            onError("ERROR al crear el tratamiento")
        }
}
*/
    /* FIXME: BORRAR => MÉTODO NO UTILIZADO
    /* EXPLICACIÓN DEL METODO <actualizarTratamiento()> : despliega para leer...
        El metodo actualizarTratamiento modifica los datos de un tratamiento concreto usando su idTratamiento.
        Accede al nodo "tratamientos" y selecciona el registro correspondiente dentro de la base de datos.
        Después aplica los cambios recibidos en el mapa updates, actualizando únicamente los campos indicados.
        Si la actualización se realiza correctamente ejecuta onSuccess, y si ocurre algún error devuelve un mensaje mediante onError.
    */
    fun actualizarTratamiento(
        idTratamiento: String,
        updates: Map<String, Any?>,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
        ) {
        databaseReference.child("tratamientos").child(idTratamiento).updateChildren(updates)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError("ERROR al actualizar el tratamiento") }
    }
    */
    /* FIXME: BORRAR => MÉTODO NO UTILIZADO
    /* EXPLICACIÓN DEL METODO <guardarMedicamentoEnTratamiento()> : despliega para leer...
        El metodo guardarMedicamentoEnTratamiento guarda la información de un medicamento asociado a un tratamiento concreto.
        Accede al nodo "medicamentosPorTratamiento" y entra en el tratamiento indicado mediante idTratamiento.
        Dentro de ese tratamiento, crea o actualiza el registro del medicamento usando idMedicamento.
        Finalmente guarda los datos recibidos en datosMedicamento y ejecuta onSuccess, o devuelve un mensaje mediante onError si ocurre algún fallo.
    */
    fun guardarMedicamentoEnTratamiento(
        idTratamiento: String,
        idMedicamento: String,
        datosMedicamento: Map<String, Any?>,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
        ) {
        databaseReference.child("medicamentosPorTratamiento")
            .child(idTratamiento)
            .child(idMedicamento)
            .setValue(datosMedicamento)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener {
                onError("ERROR al guardar medicamento en el tratamiento")
            }
    }
    */
    /* FIXME: BORRAR => MÉTODO NO UTILIZADO
    /* EXPLICACIÓN DEL METODO <generarIdTratamiento()> : despliega para leer...
        El metodo generarIdTratamiento obtiene todos los tratamientos existentes en la base de datos para calcular el siguiente id disponible.
        Recorre cada clave, la separa por "_" y extrae la parte numérica si tiene el formato esperado, por ejemplo "trat_001".
        Durante el recorrido guarda el número más alto encontrado y después le suma 1 para generar el nuevo identificador.
        Finalmente crea el id con formato de tres cifras, como "trat_002", y lo devuelve mediante onSuccess; si falla la lectura, devuelve un error con onError.
    */
    fun generarIdTratamiento(
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
        ) {
        databaseReference.child("tratamientos").get()
            .addOnSuccessListener { snapshot ->
                var maxNumero = 0

                for (child in snapshot.children) {
                    val id = child.key ?: continue
                    val partes = id.split("_")

                    if (partes.size == 2) {
                        val numero = partes[1].toIntOrNull()
                        if (numero != null && numero > maxNumero) {
                            maxNumero = numero
                        }
                    }
                }

                val nuevoNumero = maxNumero + 1
                val nuevoId = "trat_" + String.format("%03d", nuevoNumero)

                onSuccess(nuevoId)
            }
            .addOnFailureListener {
                onError("ERROR al generar ID del tratamiento")
            }
    }
    */
}