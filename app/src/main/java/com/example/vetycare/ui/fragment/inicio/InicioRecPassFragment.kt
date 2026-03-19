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
import com.example.vetycare.utils.mostrarSnackbar
import com.google.android.material.snackbar.Snackbar

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
            // Solo si la validación es correcta, mostramos el diálogo de confirmación
            if(comprobarCampos()){
            mensaje("confirmacion")
            }
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

    // FUNCION PARA COMPROBAR RECUPERACIÓN DE CONTRASEÑA
    fun comprobarCampos(): Boolean{
        val correo = binding.etCorreo.text.toString().trim()
        val pass1 = binding.etNuevacontrasenha.text.toString().trim()
        val pass2 = binding.etRepetircontrasenha.text.toString().trim()

        // Verificar que no haya campos vacíos
        if(correo.isEmpty() || pass1.isEmpty() || pass2.isEmpty()){
            mostrarSnackbar("Por favor, rellena todos los campos.")
            return false
        }
        // Verificar que el correo sea correcto
        // TODO: EN ESTE CASO PONEMOS EL CORREO POR DEFECTO -> alba@uem.com
        if(correo != "alba@uem.com"){
            mostrarSnackbar("El correo introducido no existe.")
            return false
        }

        // Verificar que las contrasenas coincidan
        if(pass1 != pass2){
            mostrarSnackbar("Las contraseñas no coinciden.")
            return false
        }
        return true
    }
}