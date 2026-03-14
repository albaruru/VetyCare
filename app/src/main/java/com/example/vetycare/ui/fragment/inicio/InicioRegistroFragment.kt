package com.example.vetycare.ui.fragment.inicio

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.vetycare.R
import com.example.vetycare.databinding.FragmentInicioRegistroBinding
import com.example.vetycare.navigation.NavigatorInicio
import com.example.vetycare.ui.dialog.CancelacionDialog
import com.example.vetycare.ui.dialog.ConfirmacionDialog

class InicioRegistroFragment : Fragment() {
    private lateinit var binding : FragmentInicioRegistroBinding

    private val keyConfirmacion = "confirmacion_registro" // Clave propia de la clase para ConfirmacionDialog
    private val keyCancelacion = "cancelacion_registro" // Clave propia de la clase para CancelacionDialog

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /* DIALOG CONFIRMACION: Explicacion...

        Registramos el listener, le estamos diciendo al fragment: "Si llega un resultado con esta <requestKey>, recibelo aquí"
        Y recibimos un <Bundle>. Si el usuario pulsa "Aceptar" será true, por lo que entonces realizamos la navegación programada.

        */
        parentFragmentManager.setFragmentResultListener(keyConfirmacion,this) { _, bundle ->
            val confirmado = bundle.getBoolean(ConfirmacionDialog.KEY_CONFIRMADO)
            if (confirmado) {
                navegacionFragment(1)
            }
        }
        /* DIALOG CANCELACION: Explicacion...

        Registramos el listener, le estamos diciendo al fragment: "Si llega un resultado con esta <requestKey>, recibelo aquí"
        Y recibimos un <Bundle>. Si el usuario pulsa "Aceptar" será true, por lo que entonces realizamos la navegación programada.

        */
        parentFragmentManager.setFragmentResultListener(keyCancelacion,this) { _, bundle ->
            val cancelado = bundle.getBoolean(CancelacionDialog.KEY_CANCELADO)
            if (cancelado) {
                navegacionFragment(1)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View? {
        binding = FragmentInicioRegistroBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        /* Acciones de los botones del fragment:
        * - Botón Guardar => Recogeremos el correo y la contraseña para cambiar las credenciales del usuario en FireBase
        * - Botón Volver => Descarta cualquier información introducida en nuestros bloques de texto y volvemos a la pantalla login
        * */
        binding.btnGuardar.setOnClickListener {

            mensaje("confirmacion")
        }
        binding.btnVolver.setOnClickListener {

            mensaje("cancelacion")
        }
    }

    /* NAVEGACION ENTRE FRAGMENTS
    1.- Mostramos el mensaje de confirmación y según la respuesta, entrará en el parentFragmentManager de nuestro metodo onCreate
    2.- Mostramos el mensaje de cancelacion y según la respuesta, entrará en el parentFragmentManager de nuestro metodo onCreate
    * */
    fun navegacionFragment(num : Int) {
        when (num) {
            1 -> NavigatorInicio.InicioRegistro_to_InicioPrincipal(this)
        }
    }

    fun mensaje (tipo: String) {
        when (tipo) {
            "confirmacion" -> {
                /* Explicación del metodo ConfirmacionDialog.nuevoDialog(...)

                Aquí hacemos lo siguiente:
                1. Creamos una instancia del diálogo
                2. Le pasamos título, mensaje y clave. (Si no se rellenan, se pondrán los valores por defecto del Dialog)
                3. Mostramos en pantalla nuestra alerta.

                */
                ConfirmacionDialog.nuevoDialog(
                    "CONFIRMAR REGISTRO DE USUARIO",
                    "¿Deseas completar el registro?",
                    keyConfirmacion
                ).show(parentFragmentManager,"ConfirmacionDialog")
            }
            "cancelacion" -> {
                /* Explicación del metodo CancelacionDialog.nuevoDialog(...)

                Aquí hacemos lo siguiente:
                1. Creamos una instancia del diálogo
                2. Le pasamos título, mensaje y clave. (Si no se rellenan, se pondrán los valores por defecto del Dialog)
                3. Mostramos en pantalla nuestra alerta.

                */
                CancelacionDialog.nuevoDialog(
                    "CANCELACION REGISTRO DE USUARIO",
                    "¿Deseas cancelar el registro? \nTodos los datos introducidos se perderán.",
                    keyCancelacion
                ).show(parentFragmentManager,"CancelacionDialog")
            }
        }
    }
}