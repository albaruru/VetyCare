package com.example.vetycare.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment

/* EXPLICACIÓN DE LA CLASE <CancelacionDialog()> : despliega para leer...
    Fragmento de diálogo especializado en mostrar advertencias de cancelación al usuario.
    Permite informar sobre la pérdida de datos y devolver la confirmación al fragmento
    de origen mediante el sistema de comunicación FragmentResult de Android.
 */
class CancelacionDialog : DialogFragment () {

    /* EXPLICACIÓN DEL METODO <onCreateDialog()> : despliega para leer...
        Configura y construye la ventana de alerta utilizando el Builder de AlertDialog.
        Recupera los textos de los argumentos, define los botones de acción y gestiona
        el envío del resultado positivo al fragmento que invocó el diálogo.
    */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val titulo = arguments?.getString(ARG_TITULO) ?: "CANCELAR"
        val mensaje = arguments?.getString(ARG_MENSAJE) ?: "¿Estás seguro de querer cancelar? Se perderán todos los datos introducidos."
        val requestKey = arguments?.getString(ARG_REQUEST_KEY) ?: REQUEST_KEY_DEFAULT

        return AlertDialog.Builder(requireContext())
            .setTitle(titulo) // Establecemos titulo
            .setMessage(mensaje) // Establecemos mensaje
            .setPositiveButton("Sí") { _, _ -> // Establecemos boton de aceptar

                parentFragmentManager.setFragmentResult(
                    requestKey,
                    bundleOf(KEY_CANCELADO to true)
                )
            }
            .setNegativeButton("No", null)
            .create()
    }

    /* EXPLICACIÓN DEL <companion object> : despliega para leer...
        Espacio estático que centraliza las constantes de claves para los argumentos y resultados.
        Incluye también el metodo de factoría necesario para instanciar el diálogo con una
        configuración personalizada de forma segura y centralizada.
    */
    companion object {
        const val ARG_TITULO = "arg_titulo"
        const val ARG_MENSAJE = "arg_mensaje"
        const val ARG_REQUEST_KEY = "arg_request_key"
        const val KEY_CANCELADO = "key_confirmado"
        const val REQUEST_KEY_DEFAULT = "confirmacion_dialog"

        /* EXPLICACIÓN DEL METODO <nuevoDialog()> : despliega para leer...
            Función de factoría que crea una nueva instancia del fragmento y adjunta sus argumentos.
            Facilita la reutilización del componente permitiendo definir títulos, mensajes y claves
            de respuesta específicas para cada flujo de la aplicación.
        */
        fun nuevoDialog(
            titulo: String = "CANCELAR",
            mensaje: String = "¿Estás seguro de querer cancelar? Se perderán todos los datos introducidos.",
            requestKey: String = REQUEST_KEY_DEFAULT
        ) : CancelacionDialog {
            return CancelacionDialog().apply {
                arguments = bundleOf(
                    ARG_TITULO to titulo,
                    ARG_MENSAJE to mensaje,
                    ARG_REQUEST_KEY to requestKey
                )
            }
        }
    }
}
