package com.example.vetycare.database.remote

import com.example.vetycare.model.entities.Medicamento
import com.google.firebase.database.DatabaseReference

class MedicamentoRemote (private val databaseReference: DatabaseReference) {

    /* EXPLICACIÓN DEL METODO <obtenerMedicamentoPorId()> : despliega para leer...
        El metodo obtenerMedicamentoPorId busca un medicamento concreto en la base de datos usando el idMedicamento recibido.
        Accede al nodo "medicamentos" y selecciona el registro que coincide con ese id.
        Si la lectura se realiza correctamente, convierte los datos obtenidos en un objeto de tipo Medicamento.
        Después asigna al medicamento su id usando la clave del nodo y lo devuelve mediante onSuccess;
        si falla la lectura, devuelve un mensaje con onError.
    */
    fun obtenerMedicamentoPorId(
        idMedicamento: String,
        onSuccess: (Medicamento?) -> Unit,
        onError: (String?) -> Unit
        ) {
        databaseReference.child("medicamentos").child(idMedicamento).get()
            .addOnSuccessListener { snapshot ->
                val medicamento = snapshot.getValue(Medicamento::class.java)

                if (medicamento != null) {
                    medicamento.id = snapshot.key
                }

                onSuccess(medicamento)
            }
            .addOnFailureListener {
                onError("ERROR al leer el medicamento")
            }
    }

    /* FIXME: BORRAR => MÉTODO NO UTILIZADO
    /* EXPLICACIÓN DEL METODO <obtenerTodosLosMedicamentos()> : despliega para leer...
        El metodo obtenerTodosLosMedicamentos obtiene todos los medicamentos guardados dentro del nodo "medicamentos" de la base de datos.
        Si la lectura se realiza correctamente, recorre cada registro y lo convierte en un objeto de tipo Medicamento.
        Después asigna a cada medicamento su id usando la clave del nodo correspondiente.
        Finalmente añade todos los medicamentos a una lista y la devuelve mediante onSuccess, o muestra un mensaje de error con onError si falla la lectura.
    */
    fun obtenerTodosLosMedicamentos(
        onSuccess: (List<Medicamento>) -> Unit,
        onError: (String?) -> Unit
        ) {
        databaseReference.child("medicamentos").get()
            .addOnSuccessListener { snapshot ->
                val listaMedicamentos = mutableListOf<Medicamento>()

                for (child in snapshot.children) {
                    val medicamento = child.getValue(Medicamento::class.java)
                    if (medicamento != null) {
                        medicamento.id = child.key
                        listaMedicamentos.add(medicamento)
                    }
                }

                onSuccess(listaMedicamentos)
            }
            .addOnFailureListener {
                onError("ERROR al obtener los medicamentos")
            }
    }
    */
    /* FIXME: BORRAR => MÉTODO NO UTILIZADO
    /* EXPLICACIÓN DEL METODO <generarIdMedicamento()> : despliega para leer...
        El metodo generarIdMedicamento obtiene todos los medicamentos existentes en la base de datos para calcular el siguiente id disponible.
        Recorre cada clave, la separa por "_" y extrae la parte numérica si tiene el formato esperado, por ejemplo "med_001".
        Durante el recorrido guarda el número más alto encontrado y después le suma 1 para crear el nuevo identificador.
        Finalmente genera el id con formato de tres cifras, como "med_002", y lo devuelve mediante onSuccess;
        si falla la lectura, devuelve un error con onError.
    */
    fun generarIdMedicamento(
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
        ) {
        databaseReference.child("medicamentos").get()
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
                val nuevoId = "med_" + String.format("%03d", nuevoNumero)

                onSuccess(nuevoId)
            }
            .addOnFailureListener {
                onError("ERROR al generar ID del medicamento")
            }
    }
    */
    /* FIXME: BORRAR => MÉTODO NO UTILIZADO
    /* EXPLICACIÓN DEL METODO <registrarMedicamento()> : despliega para leer...
        El metodo registrarMedicamento guarda un nuevo medicamento en la base de datos usando el idMedicamento recibido.
        Accede al nodo "medicamentos" y crea un registro hijo con ese identificador.
        Después almacena dentro de ese nodo el objeto medicamento mediante setValue.
        Si el registro se realiza correctamente ejecuta onSuccess, y si ocurre algún error devuelve un mensaje mediante onError.
    */
    fun registrarMedicamento(
        idMedicamento: String,
        medicamento: Medicamento,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
        ) {
        databaseReference.child("medicamentos").child(idMedicamento).setValue(medicamento)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onError("ERROR al registrar el medicamento")
            }
    }
    */
    /* FIXME: BORRAR => MÉTODO NO UTILIZADO
    /* EXPLICACIÓN DEL METODO <actualizarMedicamento()> : despliega para leer...
        El metodo actualizarMedicamento modifica los datos de un medicamento concreto usando su idMedicamento.
        Accede al nodo "medicamentos" y selecciona el registro correspondiente dentro de la base de datos.
        Después aplica los cambios recibidos en el mapa updates, actualizando únicamente los campos indicados.
        Si la actualización se realiza correctamente ejecuta onSuccess, y si ocurre algún error devuelve un mensaje mediante onError.
    */
    fun actualizarMedicamento(
        idMedicamento: String,
        updates: Map<String, Any?>,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
        ) {
        databaseReference.child("medicamentos").child(idMedicamento).updateChildren(updates)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onError("ERROR al actualizar el medicamento")
            }
    }
    */
}