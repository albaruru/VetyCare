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
    private val keyConfirmacion = "confirmacion_registro" // Clave propia de la clase para ConfirmacionDialog
    private val keyCancelacion = "cancelacion_registro" // Clave propia de la clase para CancelacionDialog

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // FIXME CARLOS
        parentFragmentManager.setFragmentResultListener(keyConfirmacion,this) { _, bundle ->
            val confirmado = bundle.getBoolean(ConfirmacionDialog.KEY_CONFIRMADO)
            if (confirmado) {
                navegacionFragment(1)
            }
        }
        // FIXME CARLOS
        parentFragmentManager.setFragmentResultListener(keyCancelacion,this) { _, bundle ->
            val cancelado = bundle.getBoolean(CancelacionDialog.KEY_CANCELADO)
            if (cancelado) {
                navegacionFragment(2)
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
        binding.btnCerrarsesion.setOnClickListener {

            mensaje("cerrar_sesion")
        }
        // FIXME: binding.etDatosUsuario.isEnabled = false // Esto lo deshabilita por completo
        /* FIXME: <EditText -> HAY QUE PONERLO EN CADA UNO DE LOS ET
            android:id="@+id/et_datos_usuario"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:focusable="false"
            android:clickable="true"
            android:cursorVisible="false" /> */

    }

    fun navegacionFragment(num: Int) {
        when (num) {
            // FIXME CARLOS
            1 -> NavigatorUsuario.UsuarioPerfil_to_UsuarioInicio(this)
            2 -> NavigatorRoot.Usuario_to_Inicio(this)
        }
    }

    fun mensaje (tipo: String) {
        when (tipo) {
            //FIXME CARLOS
            "confirmacion" -> {
                ConfirmacionDialog.nuevoDialog(
                    "CONFIRMAR MODIFICACION PERFIL",
                    "¿Deseas confirmar la modificacion del perfil?",
                    keyConfirmacion
                ).show(parentFragmentManager,"ConfirmacionDialog")
            }
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