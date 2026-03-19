package com.example.vetycare.ui.fragment.usuario

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.vetycare.databinding.FragmentUsuarioRegMascotaBinding
import com.example.vetycare.navigation.NavigatorInicio
import com.example.vetycare.navigation.NavigatorUsuario
import com.example.vetycare.ui.dialog.CancelacionDialog
import com.example.vetycare.ui.dialog.ConfirmacionDialog
import com.example.vetycare.utils.mostrarSnackbar

class UsuarioRegMascotaFragment : Fragment() {
    private lateinit var binding : FragmentUsuarioRegMascotaBinding
    private val keyConfirmacion = "confirmacion_registro" // Clave propia de la clase para ConfirmacionDialog
    private val keyCancelacion = "cancelacion_registro" // Clave propia de la clase para CancelacionDialog

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        parentFragmentManager.setFragmentResultListener(keyConfirmacion,this) { _, bundle ->
            val confirmado = bundle.getBoolean(ConfirmacionDialog.KEY_CONFIRMADO)
            if (confirmado) {
                navegacionFragment(1)
            }
        }
        parentFragmentManager.setFragmentResultListener(keyCancelacion,this) { _, bundle ->
            val cancelado = bundle.getBoolean(CancelacionDialog.KEY_CANCELADO)
            if (cancelado) {
                navegacionFragment(1)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View? {
        binding = FragmentUsuarioRegMascotaBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        /* Acciones de los botones del fragment:
        -
        */
        binding.btnGuardar.setOnClickListener {
            //Solo si los campos son válidos, mostramos el diálogo
            if (comprobarCamposMascota()){
            mensaje("confirmacion")
            }
        }

        binding.btnVolver.setOnClickListener {
            mensaje("cancelacion")
        }
    }

    /* NAVEGACION ENTRE FRAGMENTS
    1.- Mostramos el mensaje de confirmación y según la respuesta, entrará en el parentFragmentManager de nuestro metodo onCreate
    2.- Mostramos el mensaje de cancelacion y según la respuesta, entrará en el parentFragmentManager de nuestro metodo onCreate
    * */
    fun navegacionFragment (num: Int) {
        when (num) {
            1 -> NavigatorUsuario.UsuarioRegMascota_to_UsuarioMascota(this)
        }
    }
    fun mensaje (tipo: String) {
        when (tipo) {
            "confirmacion" -> {
                ConfirmacionDialog.nuevoDialog(
                    "CONFIRMAR REGISTRO DE MASCOTA",
                    "¿Deseas completar el registro de tu mascota?",
                    keyConfirmacion
                ).show(parentFragmentManager,"ConfirmacionDialog")
            }
            "cancelacion" -> {
                CancelacionDialog.nuevoDialog(
                    "CANCELACION REGISTRO DE MASCOTA",
                    "¿Deseas cancelar el registro de tu mascota? \nTodos los datos introducidos se perderán.",
                    keyCancelacion
                ).show(parentFragmentManager,"CancelacionDialog")
            }
        }
    }

    // FUNCIÓN PARA COMPROBAR REGISTRO DE MASCOTA
    fun comprobarCamposMascota(): Boolean {
        val nombre = binding.etNombreAnimal.text.toString().trim()
        val chip = binding.etMicrochip.text.toString().trim()
        val especie = binding.etEspecie.text.toString().trim()
        val raza = binding.etRaza.text.toString().trim()
        val fecha = binding.etFechaAnimal.text.toString().trim()
        val peso = binding.etPeso.text.toString().trim()
        val castracion = binding.etCastracion.text.toString().trim()

        //Verificar que no haya campos vacíos
        if (nombre.isEmpty() || chip.isEmpty() || especie.isEmpty() ||
            raza.isEmpty() || fecha.isEmpty() || peso.isEmpty() || castracion.isEmpty()) {

            mostrarSnackbar( "Por favor, completa todos los datos de la mascota")
            return false
        }

        // Validación específica del microchip
        if (chip.length != 15) {
            mostrarSnackbar("El microchip debe tener exactamente 15 dígitos")
            return false
        }
        return true
    }
}