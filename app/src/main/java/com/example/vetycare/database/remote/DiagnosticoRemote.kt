package com.example.vetycare.database.remote

import com.example.vetycare.model.entities.Diagnostico
import com.google.firebase.database.DatabaseReference

class DiagnosticoRemote (private val databaseReference: DatabaseReference){

    /* EXPLICACIÓN DEL METODO <obtenerDiagnosticoPorId()> : despliega para leer...
        El metodo obtenerDiagnosticoPorId busca un diagnóstico concreto en la base de datos usando el idDiagnostico recibido.
        Accede al nodo "diagnosticos" y selecciona el registro que coincide con ese id.
        Si la lectura es correcta, convierte los datos obtenidos en un objeto de tipo Diagnostico.
        Después asigna al objeto su id usando la clave del nodo y lo devuelve mediante onSuccess.
        Si ocurre algún error durante la lectura, devuelve un mensaje mediante onError.
    */
    fun obtenerDiagnosticoPorId(
        idDiagnostico: String,
        onSuccess: (Diagnostico?) -> Unit,
        onError: (String?) -> Unit
        ) {
        databaseReference.child("diagnosticos").child(idDiagnostico).get()
            .addOnSuccessListener { snapshot ->
                val diagnostico = snapshot.getValue(Diagnostico::class.java)

                if (diagnostico != null) {
                    diagnostico.id = snapshot.key
                }

                onSuccess(diagnostico)
            }
            .addOnFailureListener {
                onError("ERROR al leer el diagnóstico")
            }
    }

    /* EXPLICACIÓN DEL METODO <obtenerIdsDiagnosticosPorMascota()> : despliega para leer...
        El metodo obtenerIdsDiagnosticosPorMascota busca los diagnósticos asociados a una mascota concreta usando su idMascota.
        Accede al nodo "diagnosticosPorMascota" y obtiene los registros vinculados a esa mascota.
        Después recorre cada hijo y comprueba si su valor es true, indicando que la relación es válida.
        Si es válido, añade el id del diagnóstico a una lista.
        Finalmente devuelve la lista de ids mediante onSuccess, o un mensaje de error con onError si falla la lectura.
    */
    fun obtenerIdsDiagnosticosPorMascota(
        idMascota: String,
        onSuccess: (List<String>) -> Unit,
        onError: (String?) -> Unit
        ) {
        databaseReference.child("diagnosticosPorMascota").child(idMascota).get()
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
                onError("ERROR al buscar diagnósticos por mascota")
            }
    }

    /* FIXME: BORRAR => MÉTODO NO UTILIZADO
    /* EXPLICACIÓN DEL METODO <registrarDiagnostico()> : despliega para leer...
        El metodo registrarDiagnostico guarda un nuevo diagnóstico en la base de datos usando el idDiagnostico recibido.
        Primero registra el objeto diagnostico dentro del nodo "diagnosticos".
        Si esa escritura se realiza correctamente, crea también una referencia en "diagnosticosPorMascota" usando el id de la mascota.
        Esa referencia permite localizar después los diagnósticos asociados a una mascota concreta.
        Finalmente ejecuta onSuccess si se guarda bien, o devuelve un mensaje mediante onError si falla alguno de los pasos.
    */
    fun registrarDiagnostico(
        idDiagnostico: String,
        diagnostico: Diagnostico,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
        ) {
        databaseReference.child("diagnosticos").child(idDiagnostico).setValue(diagnostico)
            .addOnSuccessListener {
                databaseReference.child("diagnosticosPorMascota")
                    .child(diagnostico.idMascota.toString())
                    .child(idDiagnostico)
                    .setValue(true)
                    .addOnSuccessListener {
                        onSuccess()
                    }
                    .addOnFailureListener {
                        onError("Diagnóstico creado, pero error al guardar índice por mascota")
                    }
            }
            .addOnFailureListener {
                onError("ERROR al crear el diagnóstico")
            }
    }
    */
    /* FIXME: BORRAR => MÉTODO NO UTILIZADO
    /* EXPLICACIÓN DEL METODO <actualizarDiagnostico()> : despliega para leer...
        El metodo actualizarDiagnostico modifica los datos de un diagnóstico concreto usando su idDiagnostico.
        Accede al nodo "diagnosticos" y selecciona el diagnóstico correspondiente dentro de la base de datos.
        Después aplica los cambios recibidos en el mapa updates, actualizando únicamente los campos indicados.
        Si la actualización se realiza correctamente ejecuta onSuccess, y si ocurre algún error devuelve un mensaje mediante onError.
    */
    fun actualizarDiagnostico(
        idDiagnostico: String,
        updates: Map<String, Any?>,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
        ) {
        databaseReference.child("diagnosticos").child(idDiagnostico).updateChildren(updates)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError("ERROR al actualizar el diagnóstico") }
    }
    */
    /* FIXME: BORRAR => MÉTODO NO UTILIZADO
    /* EXPLICACIÓN DEL METODO <generarIdDiagnostico()> : despliega para leer...
        El metodo generarIdDiagnostico obtiene todos los diagnósticos existentes en la base de datos para calcular el siguiente id disponible.
        Recorre cada clave, la separa por "_" y extrae la parte numérica si tiene el formato esperado, por ejemplo "diag_001".
        Durante el recorrido guarda el número más alto encontrado y después le suma 1 para crear el nuevo id.
        Finalmente genera el id con formato de tres cifras, como "diag_002", y lo devuelve con onSuccess; si falla la lectura, devuelve un error con onError.
    */
    fun generarIdDiagnostico(
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
        ) {
        databaseReference.child("diagnosticos").get()
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
                val nuevoId = "diag_" + String.format("%03d", nuevoNumero)

                onSuccess(nuevoId)
            }
            .addOnFailureListener {
                onError("ERROR al generar ID del diagnóstico")
            }
    }
    */
}