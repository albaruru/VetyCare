package com.example.vetycare.ui.fragment.mascota

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.vetycare.databinding.FragmentMascotaCitaBinding
import com.example.vetycare.ui.dialog.CancelacionDialog
import com.example.vetycare.ui.dialog.ConfirmacionDialog
import com.example.vetycare.utils.mostrarSnackbar

class MascotaCitaFragment : Fragment() {
    private lateinit var binding : FragmentMascotaCitaBinding
    private val keyConfirmacion = "confirmacion_cita"
    private val keyCancelacion = "cancelacion_limpiar_cita"

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Dialog Confirmación
        parentFragmentManager.setFragmentResultListener(keyConfirmacion, this) {_, bundle ->
            val confirmado = bundle.getBoolean(ConfirmacionDialog.KEY_CONFIRMADO)
            if (confirmado) {
                // TODO: RECOGER DATOS EN CLASES MOLDE
                ejecutarLimpieza()
                mostrarSnackbar("Cita solicitada con éxito.")
            }
        }

        // Dialog Cancelación
        parentFragmentManager.setFragmentResultListener(keyCancelacion, this) {_, bundle ->
            val cancelado = bundle.getBoolean(CancelacionDialog.KEY_CANCELADO)
            if (cancelado) {
                ejecutarLimpieza()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMascotaCitaBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        // TODO: RECOGER DATOS EN CLASES MOLDE
        /* Acciones de los botones del fragment:
       - Botón Pedir Cita => Recogeremos la información de los campos para pedir la cita
       - Botón Volver => Descarta cualquier información introducida en nuestros bloques de texto y volvemos a la pantalla login
       */
        binding.btnGuardar.setOnClickListener {
            // Solo si la validación es correcta, mostramos el diálogo de confirmación
                if (comprobarCampos()) {
                    mensaje("confirmacion")
                }
        }
        binding.btnLimpiar.setOnClickListener {
            mensaje("limpiar")
        }
    }

    fun mensaje(tipo: String) {
        when (tipo) {
            "confirmacion" -> {
                ConfirmacionDialog.nuevoDialog(
                    "CONFIRMAR CITA",
                    "¿Estás seguro de que quieres solicitar esta cita?",
                    keyConfirmacion
                ).show(parentFragmentManager, "ConfirmacionDialog")
            }
            "limpiar" -> {
                CancelacionDialog.nuevoDialog(
                    "LIMPIAR DATOS",
                    "¿Deseas vaciar todos los campos del formulario?",
                    keyCancelacion
                ).show(parentFragmentManager, "CancelacionDialog")
            }
        }
    }

    // FUNCION PARA COMPROBAR PEDIR CITA MASCOTA
    // TODO: ESPERAR A QUE ESTE CORREGIDO EL XML
    fun comprobarCampos(): Boolean {
        val motivo = binding.etMotivo.text.toString().trim()

        // Verificar que el motivo no este vacio
        if (motivo.isEmpty()) {
            mostrarSnackbar("Por favor, indica el motivo de la cita.")
            binding.etMotivo.requestFocus() // Pone el cursor en el error
            return false
        }
        return true
    }

    // FUNCION PARA LIMPIAR TODOS LOS CAMPOS
    private fun ejecutarLimpieza() {
        binding.etMotivo.text.clear()
        binding.spRecordatorio.setSelection(0)
        binding.tvClinica.text = ""
        binding.tvTipo.text = ""
        binding.tvFecha.text = ""
        binding.etMotivo.clearFocus()
    }
}