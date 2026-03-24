package com.example.vetycare.ui.fragment.usuario

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.vetycare.databinding.FragmentUsuarioCalendarioCitaBinding
import com.example.vetycare.navigation.NavigatorInicio
import com.example.vetycare.navigation.NavigatorUsuario
import com.example.vetycare.ui.dialog.CancelacionDialog
import com.example.vetycare.ui.dialog.ConfirmacionDialog

class UsuarioCalendarioCitaFragment : Fragment() {
    private lateinit var binding : FragmentUsuarioCalendarioCitaBinding
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
        binding = FragmentUsuarioCalendarioCitaBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        /* Acciones de los botones del fragment:
        -
        */
        binding.btnGuardar.setOnClickListener {

            mensaje("confirmacion")
        }
        binding.btnVolver.setOnClickListener {

            mensaje("cancelacion")
        }
        // TODO: CREAR CLASE MOLDE PARA RECOGER DATOS
    }

    fun navegacionFragment(num : Int) {
        when (num) {
            1 -> NavigatorUsuario.UsuarioCalendarioCita_to_UsuarioCalendario(this)
        }
    }
    fun mensaje (tipo: String) {
        when (tipo) {
            "confirmacion" -> {
                ConfirmacionDialog.nuevoDialog(
                    "CONFIRMAR CITA MÉDICA",
                    "¿Deseas confirmar su cita con el veterinario?",
                    keyConfirmacion
                ).show(parentFragmentManager,"ConfirmacionDialog")
            }
            "cancelacion" -> {
                CancelacionDialog.nuevoDialog(
                    "CANCELACION CITA MEDICA",
                    "¿Deseas cancelar la cita? \nTodos los datos introducidos se perderán.",
                    keyCancelacion
                ).show(parentFragmentManager,"CancelacionDialog")
            }
        }
    }
}