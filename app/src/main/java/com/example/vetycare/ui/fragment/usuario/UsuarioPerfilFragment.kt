package com.example.vetycare.ui.fragment.usuario

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.vetycare.databinding.FragmentUsuarioPerfilBinding
import com.example.vetycare.navigation.NavigatorRoot
import com.example.vetycare.navigation.NavigatorUsuario
import com.example.vetycare.ui.dialog.CancelacionDialog
import com.example.vetycare.ui.dialog.ConfirmacionDialog

class UsuarioPerfilFragment : Fragment () {
    private lateinit var binding : FragmentUsuarioPerfilBinding
    private val keyCancelacion = "cancelacion_registro" // Clave propia de la clase para CancelacionDialog

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parentFragmentManager.setFragmentResultListener(keyCancelacion,this) { _, bundle ->
            val cancelado = bundle.getBoolean(CancelacionDialog.KEY_CANCELADO)
            if (cancelado) {
                navegacionFragment(1)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View? {
        binding = FragmentUsuarioPerfilBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        /* Acciones de los botones del fragment:
        -
        */
        // Los EditTexts no tendrán funcion en este fragment ya que únicamente son para mostrar la informacion
        binding.etNombre.isEnabled = false
        binding.etApellido.isEnabled = false
        binding.spSexo.isEnabled = false
        binding.etDni.isEnabled = false
        binding.etFecha.isEnabled = false
        binding.etCorreo.isEnabled = false
        binding.etTelefono.isEnabled = false

        binding.btnCerrarsesion.setOnClickListener {

            mensaje("cerrar_sesion")
        }
    }

    fun navegacionFragment(num: Int) {
        when (num) {
            1 -> NavigatorRoot.Usuario_to_Inicio(this)
        }
    }

    fun mensaje (tipo: String) {
        when (tipo) {
            "cerrar_sesion" -> {
                CancelacionDialog.nuevoDialog(
                    "CERRAR SESIÓN",
                    "¿Deseas cerrar la sesión? \nTendrás que volver a introducir tus credenciales.",
                    keyCancelacion
                ).show(parentFragmentManager,"CancelacionDialog")
            }
        }
    }

}