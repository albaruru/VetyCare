package com.example.vetycare.ui.fragment.usuario

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
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

    /* Este fragment no tiene acciones, únicamente es la pantalla de bienvenida.
    */
    override fun onResume() {
        super.onResume()
        cargarDatosUsuario()
    }

    private fun cargarDatosUsuario() {
        val authUid = auth.currentUser?.uid
        if (authUid.isNullOrEmpty()) {
            mostrarSnackbar("No se ha podido encontrar el autentificador")
            return
        }

        databaseReference.child("propietariosPorAuthUid").child(authUid).get()
            .addOnSuccessListener { snapshotId ->
                val idPropietario = snapshotId.getValue(String::class.java)
                if (idPropietario.isNullOrEmpty()) {
                    mostrarSnackbar("No se ha encontrado propietario")
                    return@addOnSuccessListener
                }
                databaseReference.child("propietarios").child(idPropietario).get()
                    .addOnSuccessListener { snapshotProp ->
                        val propietario = snapshotProp.getValue(Propietario::class.java)
                        if (propietario != null) {
                            binding.tvNombreUsuario.setText(propietario?.nombre + " " + propietario?.apellido)
                            if (propietario?.sexo.equals("Femenino")) {
                                binding.tvTitulo.setText("BIENVENIDA")
                            }
                        }
                        else {
                            mostrarSnackbar("No se pudieron cargar los datos del propietario")
                        }
                    }
                    .addOnFailureListener {
                        mostrarSnackbar("ERROR al leer el propietario")
                    }
            }
            .addOnFailureListener {
                mostrarSnackbar("ERROR al buscar el propietario por authUid")
            }
    }
}