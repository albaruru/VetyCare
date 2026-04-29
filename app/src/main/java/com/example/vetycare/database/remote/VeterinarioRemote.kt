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

    /* FIXME: BORRAR => MÉTODO NO UTILIZADO
/* EXPLICACIÓN DEL METODO <obtenerTodosLosVeterinarios()> : despliega para leer...
    El metodo obtenerTodosLosVeterinarios obtiene todos los veterinarios guardados dentro del nodo "veterinarios" de la base de datos.
    Si la lectura se realiza correctamente, recorre cada registro y lo convierte en un objeto de tipo Veterinario.
    Después asigna a cada veterinario su id usando la clave del nodo correspondiente.
    Finalmente añade todos los veterinarios a una lista y la devuelve mediante onSuccess, o muestra un mensaje de error con onError si falla la lectura.
*/
fun obtenerTodosLosVeterinarios(
    onSuccess: (List<Veterinario>) -> Unit,
    onError: (String?) -> Unit
) {
    databaseReference.child("veterinarios").get()
        .addOnSuccessListener { snapshot ->
            val lista = mutableListOf<Veterinario>()

            for (child in snapshot.children) {
                val veterinario = child.getValue(Veterinario::class.java)
                if (veterinario != null) {
                    veterinario.id = child.key ?: ""
                    lista.add(veterinario)
                }
            }

            onSuccess(lista)
        }
        .addOnFailureListener {
            onError("ERROR al leer todos los veterinarios")
        }
}
*/
    /* FIXME: BORRAR => MÉTODO NO UTILIZADO
    /* EXPLICACIÓN DEL METODO <registrarVeterinario()> : despliega para leer...
        El metodo registrarVeterinario guarda un nuevo veterinario en la base de datos usando el idVeterinario recibido.
        Primero comprueba que el veterinario tenga idClinica e idColegio, ya que son necesarios para crear sus relaciones.
        Si falta alguno de esos datos, detiene el proceso y devuelve un mensaje mediante onError.
        Después crea un mapa updates para registrar el veterinario y vincularlo tanto con su clínica como con su colegio.
        Finalmente aplica todos los cambios a la vez con updateChildren, ejecutando onSuccess si todo va bien o onError si falla el registro.
    */
    fun registrarVeterinario(
        idVeterinario: String,
        veterinario: Veterinario,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
    ) {
        val idClinica = veterinario.idClinica
        val idColegio = veterinario.idColegio

        if (idClinica.isNullOrEmpty()) {
            onError("El veterinario no tiene idClinica")
            return
        }

        if (idColegio.isNullOrEmpty()) {
            onError("El veterinario no tiene idColegio")
            return
        }

        val updates = hashMapOf<String, Any?>(
            "/veterinarios/$idVeterinario" to veterinario,
            "/veterinariosPorClinica/$idClinica/$idVeterinario" to true,
            "/veterinariosPorColegio/$idColegio/$idVeterinario" to true
        )

        databaseReference.updateChildren(updates)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onError("ERROR al registrar el veterinario")
            }
    }
    */
    /* FIXME: BORRAR => MÉTODO NO UTILIZADO
    /* EXPLICACIÓN DEL METODO <actualizarVeterinario()> : despliega para leer...
        El metodo actualizarVeterinario modifica los datos de un veterinario concreto usando su idVeterinario.
        Accede al nodo "veterinarios" y selecciona el registro correspondiente dentro de la base de datos.
        Después aplica los cambios recibidos en el mapa cambios, actualizando únicamente los campos indicados.
        Si la actualización se realiza correctamente ejecuta onSuccess, y si ocurre algún error devuelve un mensaje mediante onError.
    */
    fun actualizarVeterinario(
        idVeterinario: String,
        cambios: Map<String, Any?>,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
    ) {
        databaseReference.child("veterinarios").child(idVeterinario).updateChildren(cambios)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onError("ERROR al actualizar el veterinario")
            }
    }
    */
    /* FIXME: BORRAR => MÉTODO NO UTILIZADO
    /* EXPLICACIÓN DEL METODO <activarVeterinario()> : despliega para leer...
        El metodo activarVeterinario cambia el estado de un veterinario concreto para marcarlo como activo.
        Accede al nodo "veterinarios", selecciona el registro mediante su idVeterinario y entra en el campo "activa".
        Después asigna el valor true, indicando que el veterinario queda activado.
        Si el cambio se realiza correctamente ejecuta onSuccess, y si ocurre un error devuelve un mensaje mediante onError.
    */
    fun activarVeterinario(
        idVeterinario: String,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
    ) {
        databaseReference.child("veterinarios").child(idVeterinario).child("activa").setValue(true)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onError("ERROR al activar el veterinario")
            }
    }
    */
    /* FIXME: BORRAR => MÉTODO NO UTILIZADO
    /* EXPLICACIÓN DEL METODO <desactivarVeterinario()> : despliega para leer...
        El metodo desactivarVeterinario cambia el estado de un veterinario concreto para marcarlo como inactivo.
        Accede al nodo "veterinarios", selecciona el registro mediante su idVeterinario y entra en el campo "activa".
        Después asigna el valor false, indicando que el veterinario queda desactivado.
        Si el cambio se realiza correctamente ejecuta onSuccess, y si ocurre un error devuelve un mensaje mediante onError.
    */
    fun desactivarVeterinario(
        idVeterinario: String,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
    ) {
        databaseReference.child("veterinarios").child(idVeterinario).child("activa").setValue(false)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onError("ERROR al desactivar el veterinario")
            }
    }
    */
    /* FIXME: BORRAR => MÉTODO NO UTILIZADO
    /* EXPLICACIÓN DEL METODO <actualizarClinicaVeterinario()> : despliega para leer...
        El metodo actualizarClinicaVeterinario cambia la clínica asociada a un veterinario concreto usando su idVeterinario.
        Primero actualiza el campo "idClinica" del veterinario con la nueva clínica recibida.
        Después elimina la relación anterior dentro de "veterinariosPorClinica" asignándole null.
        A continuación crea la nueva relación entre la clínica nueva y el veterinario, guardándola con valor true.
        Finalmente aplica todos los cambios a la vez con updateChildren, ejecutando onSuccess si va bien o onError si ocurre un fallo.
    */
    fun actualizarClinicaVeterinario(
        idVeterinario: String,
        idClinicaAnterior: String,
        idClinicaNueva: String,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
    ) {
        val updates = hashMapOf<String, Any?>(
            "/veterinarios/$idVeterinario/idClinica" to idClinicaNueva,
            "/veterinariosPorClinica/$idClinicaAnterior/$idVeterinario" to null,
            "/veterinariosPorClinica/$idClinicaNueva/$idVeterinario" to true
        )

        databaseReference.updateChildren(updates)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onError("ERROR al actualizar la clínica del veterinario")
            }
    }
    */
    /* FIXME: BORRAR => MÉTODO NO UTILIZADO
    /* EXPLICACIÓN DEL METODO <actualizarColegioVeterinario()> : despliega para leer...
        El metodo actualizarColegioVeterinario cambia el colegio asociado a un veterinario concreto usando su idVeterinario.
        Primero actualiza el campo "idColegio" del veterinario con el nuevo colegio recibido.
        Después elimina la relación anterior dentro de "veterinariosPorColegio" asignándole null.
        A continuación crea la nueva relación entre el colegio nuevo y el veterinario, guardándola con valor true.
        Finalmente aplica todos los cambios a la vez con updateChildren, ejecutando onSuccess si va bien o onError si ocurre un fallo.
    */
    fun actualizarColegioVeterinario(
        idVeterinario: String,
        idColegioAnterior: String,
        idColegioNuevo: String,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
    ) {
        val updates = hashMapOf<String, Any?>(
            "/veterinarios/$idVeterinario/idColegio" to idColegioNuevo,
            "/veterinariosPorColegio/$idColegioAnterior/$idVeterinario" to null,
            "/veterinariosPorColegio/$idColegioNuevo/$idVeterinario" to true
        )

        databaseReference.updateChildren(updates)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onError("ERROR al actualizar el colegio del veterinario")
            }
    }
     */

}