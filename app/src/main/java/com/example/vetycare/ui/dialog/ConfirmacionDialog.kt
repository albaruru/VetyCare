package com.example.vetycare.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment

/* EXPLICACIÓN DE LA CLASE <ConfirmacionDialog()> : despliega para leer...
    Fragmento de diálogo reutilizable diseñado para solicitar la validación del usuario ante acciones críticas.
    Envía un resultado booleano al fragmento solicitante a través del sistema de comunicación FragmentResult
    para confirmar si una operación debe proceder o detenerse.
 */
class ConfirmacionDialog : DialogFragment () {

    /* EXPLICACIÓN DEL METODO <onCreateDialog()> : despliega para leer...
        Construye y configura la interfaz del diálogo de alerta utilizando el Builder de AlertDialog.
        Establece los textos de título y cuerpo, y define la lógica del botón positivo para notificar
        la aceptación del usuario mediante una clave de respuesta única.
    */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val titulo = arguments?.getString(ARG_TITULO) ?: "CONFIRMACIÓN"
        val mensaje = arguments?.getString(ARG_MENSAJE) ?: "¿Estás seguro que deseas continuar?"
        val requestKey = arguments?.getString(ARG_REQUEST_KEY) ?: REQUEST_KEY_DEFAULT

        return AlertDialog.Builder(requireContext())
            .setTitle(titulo) // Establecemos titulo
            .setMessage(mensaje) // Establecemos mensaje
            .setPositiveButton("Aceptar") { _, _ -> // Establecemos boton de aceptar

                parentFragmentManager.setFragmentResult(
                    requestKey,
                    bundleOf(KEY_CONFIRMADO to true)
                )
            }
            .setNegativeButton("Cancelar", null)
            .create()
    }

    /* EXPLICACIÓN DEL <companion object> : despliega para leer...
        Centraliza las constantes de configuración y las claves de comunicación del diálogo.
        Contiene los identificadores necesarios para pasar argumentos y recuperar los resultados,
        además de la función de factoría para crear nuevas instancias.
    */
    companion object {
        const val ARG_TITULO = "arg_titulo"
        const val ARG_MENSAJE = "arg_mensaje"
        const val ARG_REQUEST_KEY = "arg_request_key"
        const val KEY_CONFIRMADO = "key_confirmado"
        const val REQUEST_KEY_DEFAULT = "confirmacion_dialog"

        /* EXPLICACIÓN DEL METODO <nuevoDialog()> : despliega para leer...
            Función de factoría que instancia el fragmento y le adjunta los parámetros personalizados.
            Simplifica la creación del diálogo permitiendo definir el título, el mensaje y la clave
            de escucha de forma centralizada y segura mediante un Bundle de argumentos.
        */
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