package com.example.vetycare.ui.fragment.inicio

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.vetycare.R
import com.example.vetycare.databinding.FragmentInicioRecPassBinding
import com.example.vetycare.navigation.NavigatorInicio
import com.example.vetycare.ui.dialog.ConfirmacionDialog

class InicioRecPassFragment : Fragment() {
    private lateinit var binding : FragmentInicioRecPassBinding
    private val keyConfirmacion = "confirmacion_recuperacion"

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parentFragmentManager.setFragmentResultListener(keyConfirmacion, this) {_, bundle ->
            val confirmado = bundle.getBoolean(ConfirmacionDialog.KEY_CONFIRMADO)
            if (confirmado) {
                navegacionFragment(1)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View? {
        binding = FragmentInicioRecPassBinding.inflate(layoutInflater, container,false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        /* Acciones de los botones del fragment:
        - Botón Guardar => Recogeremos el correo y la contraseña para cambiar las credenciales del usuario en FireBase
        */
        binding.btnGuardar.setOnClickListener {

            mensaje("confirmacion")
        }
    }

    /* NAVEGACION ENTRE FRAGMENTS
    * */
    fun navegacionFragment(num: Int) {
        when (num) {
            1 -> NavigatorInicio.InicioRecPass_to_InicioPrincipal(this)
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
                    "CONFIRMAR CAMBIO DE CONTRASEÑA",
                    "¿Deseas completar el cambio de contraseña?",
                    keyConfirmacion
                ).show(parentFragmentManager, "ConfirmacionDialog")
            }
        }
    }
}