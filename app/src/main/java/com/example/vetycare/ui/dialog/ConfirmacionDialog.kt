package com.example.vetycare.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.example.vetycare.R
import com.example.vetycare.navigation.NavigatorInicio

class ConfirmacionDialog : DialogFragment () {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val titulo = arguments?.getString(ARG_TITULO) ?: "CONFIRMACIÓN"
        val mensaje = arguments?.getString(ARG_MENSAJE) ?: "¿Estás seguro que deseas continuar?"
        val requestKey = arguments?.getString(ARG_REQUEST_KEY) ?: REQUEST_KEY_DEFAULT

        /* Explicación del metodo del return: despliega para leer...
        *
        * requiereContext() => Devuelve el Context del fragment. Se utiliza para poder crear la interfaz visual del diálogo.
        * setTittle(...) => Definimos el título del diálogo
        * setMessage(...) => Definimos el texto principal del diálogo
        * setPossitiveButton(...) => Botón en la parte derecha, se explica su interior en el siguiente comentario
        * setNegativeButton(...) => Botón en la parte izquierda. Al establecer null como listener, significa que no realiza ninguna acción y se cierra el dialog
        * create() => Construye el diálogo final y lo devuelve con nuestro return
        *
        * */
        return AlertDialog.Builder(requireContext())
            .setTitle(titulo) // Establecemos titulo
            .setMessage(mensaje) // Establecemos mensaje
            .setPositiveButton("Aceptar") { _, _ -> // Establecemos boton de aceptar
                /* Explicación metodo: despliega para leer...
                *
                * parentFragmentManager.setFragmentResult(...) => Esto manda un resultado usando el sistema de comunicación entre fragments
                * requestKey => Es la clave con la que el fragment y el diálogo se entienden. Tienen que coincidir en ambos lados
                * bundleOf(KEY_CONFIRMADO to true) => Aquí empaquetamos el dato que queremos devolver. En nuestro caso: "key_confirmado" = true
                *
                * */
                parentFragmentManager.setFragmentResult(
                    requestKey,
                    bundleOf(KEY_CONFIRMADO to true)
                )
            }
            .setNegativeButton("Cancelar", null)
            .create()
    }

    /* Explicación companion object: despliega para leer...
    *
    * <companion object> => es como una zona estática de la clase. Sirve para guardar:
    *       - constantes
    *       - funciones auxiliares
    *
    *  */
    companion object {
        /* Explicación de las variables constantes: despliega para leer..
        *
        * contantes <ARG> => Son las claves usadas dentro del Bundle arguments. Se hacen constantes para evitar errores por escribir textos distintos en varios sitios.
        * KEY_CONFIRMADO => Es la clave con la que el diálogo devuelve el valor <true> al fragment
        * REQUEST_KEY_DEFAULT => Es una clave por defecto por si no se le pasa otra.
        *
        *  */
        const val ARG_TITULO = "arg_titulo"
        const val ARG_MENSAJE = "arg_mensaje"
        const val ARG_REQUEST_KEY = "arg_request_key"
        const val KEY_CONFIRMADO = "key_confirmado"
        const val REQUEST_KEY_DEFAULT = "confirmacion_dialog"

        /* Explicacion metodo nuevoDialog(...): despliege para leer
        *
        * Esta funcion sirve para crear el dialogo ya preparado con sus datos base
        * */
        fun nuevoDialog(
            titulo: String = "CONFIRMACIÓN",
            mensaje: String = "¿Estás seguro que deseas continuar?",
            requestKey: String = REQUEST_KEY_DEFAULT
        ) : ConfirmacionDialog {
            return ConfirmacionDialog().apply {
                arguments = bundleOf(
                    ARG_TITULO to titulo,
                    ARG_MENSAJE to mensaje,
                    ARG_REQUEST_KEY to requestKey
                )
            }
        }
    }
}