package com.example.vetycare.ui.fragment.usuario

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.vetycare.database.remote.PropietarioRemote
import com.example.vetycare.database.repository.PropietarioRepository
import com.example.vetycare.databinding.FragmentUsuarioPerfilBinding
import com.example.vetycare.navigation.NavigatorRoot
import com.example.vetycare.ui.dialog.CancelacionDialog
import com.example.vetycare.utils.FirebaseUtils
import com.example.vetycare.utils.mostrarSnackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class UsuarioPerfilFragment : Fragment () {
    private lateinit var binding : FragmentUsuarioPerfilBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var propietarioRepository: PropietarioRepository
    private val keyCancelacion = "cancelacion_registro" // Clave propia de la clase para CancelacionDialog

    override fun onAttach(context: Context) {
        super.onAttach(context)
        auth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance(FirebaseUtils.URL_RTDB)
        databaseReference = firebaseDatabase.reference
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parentFragmentManager.setFragmentResultListener(keyCancelacion,this) { _, bundle ->
            val cancelado = bundle.getBoolean(CancelacionDialog.KEY_CANCELADO)
            if (cancelado) {
                // Cerramos la sesión del usuario
                auth.signOut()
                // Navegamos al fragment de inicio
                navegacionFragment(1)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View? {
        binding = FragmentUsuarioPerfilBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val remotePropietario = PropietarioRemote(databaseReference)
        propietarioRepository = PropietarioRepository(remotePropietario)
        cargarDatosUsuario()
    }

    override fun onResume() {
        super.onResume()

        /* Acciones de los botones del fragment:
        -
        */
        // Los EditTexts no tendrán funcion en este fragment ya que únicamente son para mostrar la informacion
        binding.etNombre.isEnabled = false
        binding.etApellido.isEnabled = false
        binding.etSexo.isEnabled = false
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

    private fun cargarDatosUsuario() {
        val auth = auth.currentUser?.uid

        if (auth.isNullOrEmpty()) {
            mostrarSnackbar("No se ha podido encontrar su identificador")
            return
        }

        propietarioRepository.obtenerPropietario(
            auth,
            { id, propietario ->
                binding.etNombre.setText(propietario.nombre)
                binding.etApellido.setText(propietario.apellido)
                binding.etSexo.setText(propietario.sexo)
                binding.etDni.setText(propietario.dni)
                binding.etFecha.setText(propietario.fechaNacimiento)
                binding.etCorreo.setText(propietario.email)
                binding.etTelefono.setText(propietario.telefono.toString())

                Glide.with(this)
                    .load(propietario.urlFotoProp)
                    .into(binding.ivFoto)
            },
            { mensajeDeError ->
                mostrarSnackbar(mensajeDeError?:"ERROR")
            }
        )
    }
}