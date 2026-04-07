package com.example.vetycare.ui.fragment.usuario

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.vetycare.database.remote.MascotaRemote
import com.example.vetycare.database.remote.PropietarioRemote
import com.example.vetycare.database.repository.MascotaRepository
import com.example.vetycare.database.repository.PropietarioRepository
import com.example.vetycare.databinding.FragmentUsuarioRegMascotaBinding
import com.example.vetycare.model.entities.Mascota
import com.example.vetycare.navigation.NavigatorInicio
import com.example.vetycare.navigation.NavigatorUsuario
import com.example.vetycare.ui.dialog.CancelacionDialog
import com.example.vetycare.ui.dialog.ConfirmacionDialog
import com.example.vetycare.utils.FirebaseUtils
import com.example.vetycare.utils.mostrarSnackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlin.toString

class UsuarioRegMascotaFragment : Fragment() {
    private lateinit var binding : FragmentUsuarioRegMascotaBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var propietarioRepository: PropietarioRepository
    private lateinit var mascotaRepository: MascotaRepository
    private val keyConfirmacion = "confirmacion_registro" // Clave propia de la clase para ConfirmacionDialog
    private val keyCancelacion = "cancelacion_registro" // Clave propia de la clase para CancelacionDialog

    override fun onAttach(context: Context) {
        super.onAttach(context)
        auth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance(FirebaseUtils.URL_RTDB)
        databaseReference = firebaseDatabase.reference

        val remotePropietario = PropietarioRemote(databaseReference)
        propietarioRepository = PropietarioRepository(remotePropietario)

        val remoteMascota = MascotaRemote(databaseReference)
        mascotaRepository = MascotaRepository(remoteMascota)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        parentFragmentManager.setFragmentResultListener(keyConfirmacion,this) { _, bundle ->
            val confirmado = bundle.getBoolean(ConfirmacionDialog.KEY_CONFIRMADO)
            if (confirmado) {
                registrarMascota()
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

        //Verificar que no haya campos vacíos
        if (nombre.isEmpty() || especie.isEmpty() ||
            raza.isEmpty() || fecha.isEmpty() || peso.isEmpty()) {

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

    private fun registrarMascota() {
        val auth = auth.currentUser?.uid

        if(auth.isNullOrEmpty()) {
            mostrarSnackbar("No se ha podido identificar al usuario")
            return
        }

        propietarioRepository.obtenerPropietario(
            auth,
            { idProp, propietario ->
                val nombre = binding.etNombreAnimal.text.toString().trim()
                val microchip = binding.etMicrochip.text.toString().trim()
                val especie = binding.etEspecie.text.toString().trim()
                val raza = binding.etRaza.text.toString().trim()
                val fechaNacimiento = binding.etFechaAnimal.text.toString().trim()
                val pesoTexto = binding.etPeso.text.toString().trim()
                val sexo = binding.spSexo.isSelected.toString()

                val castracionTexto = binding.spCastracion.toString()
                val castracion = false
                if (castracionTexto.equals("Si")) {
                    val castracion = true
                }

                val pesoActual = pesoTexto.toDoubleOrNull()
                if (pesoActual == null) {
                    mostrarSnackbar("El peso debe ser un número válido.")
                    return@obtenerPropietario
                }

                mascotaRepository.generarIdMascota(
                    { idMasc ->
                        val mascota = Mascota (
                            idMasc,
                            true,
                            castracion,
                            especie,
                            obtenerFechaActual(),
                            fechaNacimiento,
                            idProp,
                            microchip,
                            nombre,
                            pesoActual,
                            raza,
                            sexo,
                            ""
                        )
                        mascotaRepository.registrarMascota(
                            idMasc,
                            mascota,
                            {
                                mostrarSnackbar("Mascota registrada correctamente.")
                                navegacionFragment(1)
                            },
                            { mensajeDeError ->
                                mostrarSnackbar(mensajeDeError ?: "ERROR al registrar la mascota")
                            }
                        )
                    },
                    { mensajeDeError ->
                        mostrarSnackbar(mensajeDeError ?: "ERROR al obtener id de la mascota")
                    }
                )

            },
            { mensajeDeError ->
                mostrarSnackbar(mensajeDeError ?: "Error al obtener el propietario")
            }
        )
    }

    private fun obtenerFechaActual(): String {
        val formato = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        return formato.format(java.util.Date())
    }

}