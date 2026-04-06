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
import com.example.vetycare.databinding.FragmentUsuarioInicioBinding
import com.example.vetycare.model.entities.Propietario
import com.example.vetycare.utils.FirebaseUtils
import com.example.vetycare.utils.mostrarSnackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class UsuarioInicioFragment : Fragment() {
    private lateinit var binding : FragmentUsuarioInicioBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var propietarioRepository: PropietarioRepository

    override fun onAttach(context: Context) {
        super.onAttach(context)
        auth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance(FirebaseUtils.URL_RTDB)
        databaseReference = firebaseDatabase.reference
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View? {
        binding = FragmentUsuarioInicioBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val remotePropietario = PropietarioRemote(databaseReference)
        propietarioRepository = PropietarioRepository(remotePropietario)
        cargarDatosUsuario()
    }

    /* Este fragment no tiene acciones, únicamente es la pantalla de bienvenida.
    */
    override fun onResume() {
        super.onResume()
        cargarDatosUsuario()
    }

    private fun cargarDatosUsuario() {
        val auth = auth.currentUser?.uid
        if (auth.isNullOrEmpty()) {
            mostrarSnackbar("No se ha podido encontrar el autentificador")
            return
        }

        propietarioRepository.obtenerPropietario(
            auth,
            { id, propietario ->
                binding.tvNombreUsuario.setText(propietario.nombre + " " + propietario.apellido)
                if (propietario.sexo.equals("Femenino")) {
                    binding.tvTitulo.setText("BIENVENIDA")
                }
            },
            { mensajeDeError ->
                mostrarSnackbar(mensajeDeError?:"ERROR")
            }
        )
    }
}