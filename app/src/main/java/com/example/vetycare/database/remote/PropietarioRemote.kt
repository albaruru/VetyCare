package com.example.vetycare.database.remote

import com.example.vetycare.model.entities.Propietario
import com.google.firebase.database.DatabaseReference

class PropietarioRemote (private val databaseReference : DatabaseReference) {

    /* EXPLICACIÓN DEL METODO <obtenerIdPropietarioPorAuthUid()> : despliega para leer...
        El metodo obtenerIdPropietarioPorAuthUid busca el id de un propietario usando el authUid del usuario autenticado.
        Accede al nodo "propietariosPorAuthUid" y selecciona el registro asociado a ese authUid.
        Si la lectura se realiza correctamente, obtiene el valor guardado como String, que corresponde al idPropietario.
        Finalmente devuelve ese id mediante onSuccess, o un mensaje de error mediante onError si falla la búsqueda.
    */
    fun obtenerIdPropietarioPorAuthUid (
        authUid: String,
        onSuccess: (String?) -> Unit,
        onError: (String?) -> Unit
        ) {
        databaseReference.child("propietariosPorAuthUid").child(authUid).get()
            .addOnSuccessListener { snapshot ->
                val idPropietario = snapshot.getValue(String::class.java)
                onSuccess(idPropietario)
            }
            .addOnFailureListener {
                onError("ERROR al buscar el propietario por authUid")
            }
    }

    /* EXPLICACIÓN DEL METODO <obtenerPropietarioPorId()> : despliega para leer...
        El metodo obtenerPropietarioPorId busca un propietario concreto en la base de datos usando el idPropietario recibido.
        Accede al nodo "propietarios" y selecciona el registro que coincide con ese id.
        Si la lectura se realiza correctamente, convierte los datos obtenidos en un objeto de tipo Propietario.
        Finalmente devuelve el propietario mediante onSuccess, o un mensaje de error mediante onError si falla la lectura.
    */
    fun obtenerPropietarioPorId (
        idPropietario: String,
        onSuccess: (Propietario?) -> Unit,
        onError: (String?) -> Unit
        ) {
        databaseReference.child("propietarios").child(idPropietario).get()
            .addOnSuccessListener { snapshot ->
                val propietario = snapshot.getValue(Propietario::class.java)
                onSuccess(propietario)
            }
            .addOnFailureListener {
                onError("ERROR al leer el propietario")
            }
    }

    /* EXPLICACIÓN DEL METODO <crearPropietario()> : despliega para leer...
        El metodo crearPropietario guarda un nuevo propietario en la base de datos usando el idPropietario recibido.
        Primero registra el objeto propietario dentro del nodo "propietarios".
        Si esa escritura se realiza correctamente, crea también una referencia en "propietariosPorAuthUid" usando el authUid del propietario.
        Esta referencia permite localizar después al propietario asociado a un usuario autenticado.
        Finalmente ejecuta onSuccess si se guarda bien, o devuelve un mensaje mediante onError si falla alguno de los pasos.
    */
    fun crearPropietario (
        idPropietario: String,
        propietario: Propietario,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
        ) {
        databaseReference.child("propietarios").child(idPropietario).setValue(propietario)
            .addOnSuccessListener {
                databaseReference.child("propietariosPorAuthUid").child(propietario.authUid.toString()).setValue(idPropietario)
                    .addOnSuccessListener { onSuccess()
                    }
                    .addOnFailureListener { onError("Propietario creado, pero error al guardar índice por authUid")
                    }
            }
            .addOnFailureListener {
                onError("ERROR al crear el propietario")
            }
    }

    /* EXPLICACIÓN DEL METODO <actualizarPropietario()> : despliega para leer...
        El metodo actualizarPropietario modifica los datos de un propietario concreto usando su idPropietario.
        Accede al nodo "propietarios" y selecciona el registro correspondiente dentro de la base de datos.
        Después aplica los cambios recibidos en el mapa updates, actualizando únicamente los campos indicados.
        Si la actualización se realiza correctamente ejecuta onSuccess, y si ocurre algún error devuelve un mensaje mediante onError.
    */
    fun actualizarPropietario (
        idPropietario: String,
        updates: Map <String, Any?>,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
        ) {
        databaseReference.child("propietarios").child(idPropietario).updateChildren(updates)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError("ERROR al utilizar el propietario") }
    }

    /* EXPLICACIÓN DEL METODO <generarIdPropietario()> : despliega para leer...
        El metodo generarIdPropietario obtiene todos los propietarios existentes en la base de datos para calcular el siguiente id disponible.
        Recorre cada clave, la separa por "_" y extrae la parte numérica si tiene el formato esperado, por ejemplo "prop_001".
        Durante el recorrido guarda el número más alto encontrado y después le suma 1 para crear el nuevo identificador.
        Finalmente genera el id con formato de tres cifras, como "prop_002", y lo devuelve mediante onSuccess;
        si falla la lectura, devuelve un error con onError.
    */
    fun generarIdPropietario(
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        databaseReference.child("propietarios").get()
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
                val nuevoId = "prop_" + String.format("%03d", nuevoNumero)

                onSuccess(nuevoId)
            }
            .addOnFailureListener {
                onError("ERROR al generar ID del propietario")
            }
    }
}