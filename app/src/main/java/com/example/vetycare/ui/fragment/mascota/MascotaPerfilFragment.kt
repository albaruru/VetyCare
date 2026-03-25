package com.example.vetycare.ui.fragment.mascota

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.vetycare.databinding.FragmentMascotaPerfilBinding
import com.example.vetycare.navigation.NavigatorMascota
import com.example.vetycare.navigation.NavigatorRoot
import com.example.vetycare.ui.dialog.CancelacionDialog
import com.example.vetycare.ui.dialog.ConfirmacionDialog
import com.example.vetycare.utils.mostrarSnackbar

class MascotaPerfilFragment : Fragment() {
    private lateinit var binding : FragmentMascotaPerfilBinding
    private val keyConfirmacion = "confirmacion_eliminar_mascota"

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        parentFragmentManager.setFragmentResultListener(keyConfirmacion,this) { _, bundle ->
            val confirmado = bundle.getBoolean(ConfirmacionDialog.KEY_CONFIRMADO)
            if (confirmado) {
                eliminarMascota()
                navegacionFragment(1)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMascotaPerfilBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        /* Acciones de los botones del fragment:
        - Botón Volver => Navega al UsuarioMascotaFragment
        - Botón Eliminar => Elimina la mascota del cliente
        */
        binding.btnVolver.setOnClickListener {
            navegacionFragment(1)
        }
        binding.btnEliminar.setOnClickListener {
            mensaje("confirmacion")
        }
    }

    // NAVEGACION ENTRE FRAGMENTS
    fun navegacionFragment(num: Int) {
        when (num) {
            1 -> NavigatorRoot.Mascota_to_Usuario(this)
        }
    }

    fun mensaje (tipo: String) {
        when (tipo) {
            "confirmacion" -> {
                ConfirmacionDialog.nuevoDialog(
                    "ELIMINAR MASCOTA",
                    "¿Estás seguro de que deseas eliminar esta mascota?\nEsta acción no se podrá deshacer.",
                    keyConfirmacion
                ).show(parentFragmentManager,"ConfirmacionDialog")
            }
        }
    }

    // FUNCIÓN PARA ELIMINAR MASCOTA DEL CLIENTE
    fun eliminarMascota(){
        // TODO: CREAR LA LÓGICA DE FIREBASE PARA ELIMINAR MASCOTA DEL USUARIO
        mostrarSnackbar("Mascota eliminada correctamente.")
    }
}