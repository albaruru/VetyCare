package com.example.vetycare.ui.fragment.mascota

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.vetycare.database.remote.MascotaRemote
import com.example.vetycare.database.repository.MascotaRepository
import com.example.vetycare.databinding.FragmentMascotaPerfilBinding
import com.example.vetycare.navigation.NavigatorMascota
import com.example.vetycare.navigation.NavigatorRoot
import com.example.vetycare.ui.container.MascotaContainerFragment
import com.example.vetycare.ui.dialog.CancelacionDialog
import com.example.vetycare.ui.dialog.ConfirmacionDialog
import com.example.vetycare.utils.FirebaseUtils
import com.example.vetycare.utils.mostrarSnackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MascotaPerfilFragment : Fragment() {
    private lateinit var binding : FragmentMascotaPerfilBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var mascotaRepository: MascotaRepository
    private val keyConfirmacion = "confirmacion_eliminar_mascota"
    private var idMascotaSeleccionada: String? = null // Variable para guardar el ID recibido

    override fun onAttach(context: Context) {
        super.onAttach(context)
        auth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance(FirebaseUtils.URL_RTDB)
        databaseReference = firebaseDatabase.reference
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val container = parentFragment?.parentFragment as? MascotaContainerFragment
        idMascotaSeleccionada = container?.obtenerIdMascotaSeleccionada()

        val remoteMascota = MascotaRemote(databaseReference)
        mascotaRepository = MascotaRepository(remoteMascota)
        cargarDatosMascota()
    }

    override fun onResume() {
        super.onResume()
        /* Acciones de los botones del fragment:
        - Botón Volver => Navega al UsuarioMascotaFragment
        - Botón Eliminar => Elimina la mascota del cliente
        */

        // Bloqueamos los campos para que solo sean de lectura
        binding.etNombreAnimal.isEnabled = false
        binding.etMicrochip.isEnabled = false
        binding.etEspecie.isEnabled = false
        binding.etRaza.isEnabled = false
        binding.etFechaAnimal.isEnabled = false
        binding.etPeso.isEnabled = false
        binding.etSexo.isEnabled = false
        binding.etCastracion.isEnabled = false

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

    // Funcion para cargar los datos de la mascota
    private fun cargarDatosMascota() {
        if (idMascotaSeleccionada.isNullOrEmpty()) {
            mostrarSnackbar("No se ha podido encontrar el identificador de la mascota")
            return
        }

        mascotaRepository.obtenerMascotaPorId(
            idMascotaSeleccionada!!,
            { mascota ->
                binding.etNombreAnimal.setText(mascota.nombre)
                binding.etMicrochip.setText(mascota.microchip)
                binding.etEspecie.setText(mascota.especie)
                binding.etRaza.setText(mascota.raza)
                binding.etFechaAnimal.setText(mascota.fechaNacimiento)
                binding.etSexo.setText(mascota.sexo)
                binding.etPeso.setText(mascota.pesoActual.toString())
                // Convierto el booleano de castración a texto para cargarlo en el perfil
                val castradaTexto = if (mascota.castracion == true) "Sí" else "No"
                binding.etCastracion.setText(castradaTexto)

                // Carga de imagen con Glide
                Glide.with(this)
                    .load(mascota.urlFotoMasc)
                    .into(binding.ivFotoAnimal)
            },
            { mensajeDeError ->
                mostrarSnackbar(mensajeDeError ?: "ERROR")
            }
        )
    }

    // FUNCIÓN PARA ELIMINAR MASCOTA DEL CLIENTE
    fun eliminarMascota(){
        // TODO: CREAR LA LÓGICA DE FIREBASE PARA ELIMINAR MASCOTA DEL USUARIO
        mostrarSnackbar("Mascota eliminada correctamente.")
    }
}