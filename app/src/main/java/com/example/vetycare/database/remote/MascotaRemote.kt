package com.example.vetycare.database.remote

import com.example.vetycare.model.entities.Mascota
import com.google.firebase.database.DatabaseReference

class MascotaRemote (private val databaseReference: DatabaseReference) {

    /* EXPLICACIÓN DEL METODO <obtenerMascotaPorId()> : despliega para leer...
        El metodo obtenerMascotaPorId busca una mascota concreta en la base de datos usando el idMascota recibido.
        Accede al nodo "mascotas" y selecciona el registro que coincide con ese id.
        Si la lectura se realiza correctamente, convierte los datos obtenidos en un objeto de tipo Mascota.
        Finalmente devuelve la mascota mediante onSuccess, o un mensaje de error mediante onError si falla la lectura.
    */
    fun obtenerMascotaPorId (
        idMascota: String,
        onSuccess: (Mascota?) -> Unit,
        onError: (String?) -> Unit
        ) {
        databaseReference.child("mascotas").child(idMascota).get()
            .addOnSuccessListener { snapshot ->
                val mascota = snapshot.getValue(Mascota::class.java)
                onSuccess(mascota)
            }
            .addOnFailureListener {
                onError("ERROR al leer la mascota")
            }
    }

    /* EXPLICACIÓN DEL METODO <obtenerIdsMascotasPorPropietario()> : despliega para leer...
        El metodo obtenerIdsMascotasPorPropietario busca las mascotas asociadas a un propietario concreto usando su idPropietario.
        Accede al nodo "mascotasPorPropietario" y obtiene los registros vinculados a ese propietario.
        Después recorre cada hijo y comprueba si su valor es true, indicando que la relación es válida.
        Si es válido, añade el id de la mascota a una lista.
        Finalmente devuelve la lista de ids mediante onSuccess, o un mensaje de error con onError si falla la lectura.
    */
    fun obtenerIdsMascotasPorPropietario (
        idPropietario: String,
        onSuccess: (List<String>) -> Unit,
        onError: (String?) -> Unit
        ) {
        databaseReference.child("mascotasPorPropietario").child(idPropietario).get()
            .addOnSuccessListener { snapshot ->
                val listaIds = mutableListOf<String>()
                for (child in snapshot.children) {
                    val esValido = child.getValue(Boolean::class.java) ?: false
                    if (esValido) {
                        child.key?.let {
                            listaIds.add(it)
                        }
                    }
                }
                onSuccess(listaIds)
            }
            .addOnFailureListener {
                onError("ERROR al buscar mascotas por propietario")
            }
    }

    /* EXPLICACIÓN DEL METODO <registrarMascota()> : despliega para leer...
        El metodo registrarMascota guarda una nueva mascota en la base de datos usando el idMascota recibido.
        Primero registra el objeto mascota dentro del nodo "mascotas".
        Si esa escritura se realiza correctamente, crea también una referencia en "mascotasPorPropietario" usando el id del propietario.
        Esta referencia permite localizar después las mascotas asociadas a un propietario concreto.
        Finalmente ejecuta onSuccess si se guarda bien, o devuelve un mensaje mediante onError si falla alguno de los pasos.
    */
    fun registrarMascota (
        idMascota: String,
        mascota: Mascota,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
        ){
        databaseReference.child("mascotas").child(idMascota).setValue(mascota)
            .addOnSuccessListener {
                databaseReference.child("mascotasPorPropietario").child(mascota.idPropietario.toString()).child(idMascota).setValue(true)
                    .addOnSuccessListener {
                        onSuccess() }
                    .addOnFailureListener {
                        onError("Mascota creada, pero error al guardar índice por propietario")
                    }
            }
            .addOnFailureListener {
                onError("ERROR al crear la mascota")
            }
    }

    /* EXPLICACIÓN DEL METODO <actualizarMascota()> : despliega para leer...
        El metodo actualizarMascota modifica los datos de una mascota concreta usando su idMascota.
        Accede al nodo "mascotas" y selecciona el registro correspondiente dentro de la base de datos.
        Después aplica los cambios recibidos en el mapa updates, actualizando solo los campos indicados.
        Si la actualización se realiza correctamente ejecuta onSuccess, y si ocurre algún error devuelve un mensaje mediante onError.
    */
    fun actualizarMascota (
        idMascota: String,
        updates: Map<String,Any?>,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
        ){
        databaseReference.child("mascotas").child(idMascota).updateChildren(updates)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError("EEROR al actualizar la mascota") }
    }

    /* EXPLICACIÓN DEL METODO <generarIdMascota()> : despliega para leer...
        El metodo generarIdMascota obtiene todas las mascotas existentes en la base de datos para calcular el siguiente id disponible.
        Recorre cada clave, la separa por "_" y extrae la parte numérica si tiene el formato esperado, por ejemplo "masc_001".
        Durante el recorrido guarda el número más alto encontrado y después le suma 1 para generar el nuevo identificador.
        Finalmente crea el id con formato de tres cifras, como "masc_002",
        y lo devuelve mediante onSuccess; si falla la lectura, devuelve un error con onError.
    */
    fun generarIdMascota(
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        databaseReference.child("mascotas").get()
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
                val nuevoId = "masc_" + String.format("%03d", nuevoNumero)

                onSuccess(nuevoId)
            }
            .addOnFailureListener {
                onError("ERROR al generar ID de mascota")
            }
    }

    /* EXPLICACIÓN DEL METODO <eliminarMascota()> : despliega para leer...
        El metodo eliminarMascota elimina una mascota concreta de la base de datos usando su idMascota.
        Accede al nodo "mascotas" y selecciona el registro correspondiente a esa mascota.
        Después utiliza removeValue() para borrar completamente ese nodo del servidor.
        Si la eliminación se realiza correctamente ejecuta onSuccess, y si ocurre algún error devuelve un mensaje mediante onError.
    */
    fun eliminarMascota(
        idMascota: String,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
    ) {
        // Referenciamos el nodo exacto de la mascota y lo eliminamos
        databaseReference.child("mascotas").child(idMascota).removeValue()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError("Error al eliminar la mascota en el servidor") }
    }
}