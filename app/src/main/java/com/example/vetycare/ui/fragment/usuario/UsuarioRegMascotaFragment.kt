package com.example.vetycare.ui.fragment.usuario

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.vetycare.database.remote.MascotaRemote
import com.example.vetycare.database.remote.PropietarioRemote
import com.example.vetycare.database.repository.MascotaRepository
import com.example.vetycare.database.repository.PropietarioRepository
import com.example.vetycare.databinding.FragmentUsuarioRegMascotaBinding
import com.example.vetycare.model.entities.Mascota
import com.example.vetycare.navigation.NavigatorUsuario
import com.example.vetycare.ui.dialog.CancelacionDialog
import com.example.vetycare.ui.dialog.ConfirmacionDialog
import com.example.vetycare.utils.FirebaseUtils
import com.example.vetycare.utils.mostrarSnackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

/* EXPLICACIÓN DE LA CLASE <UsuarioRegMascotaFragment()> : despliega para leer...
    Fragmento encargado de gestionar el registro de nuevas mascotas por parte del usuario.
    Controla la validación de los datos de entrada, la selección y subida de imágenes a Firebase
    Storage y la persistencia del nuevo objeto Mascota en la base de datos sincronizada.
 */
class UsuarioRegMascotaFragment : Fragment() {
    private lateinit var binding : FragmentUsuarioRegMascotaBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var propietarioRepository: PropietarioRepository
    private lateinit var mascotaRepository: MascotaRepository
    private val keyConfirmacion = "confirmacion_registro"
    private val keyCancelacion = "cancelacion_registro"

    // Variables para Storage y selección de imagen
    private lateinit var storageReference: StorageReference
    private var uriImagenSeleccionada: Uri? = null

    /* EXPLICACIÓN DE LA VARIABLE <pickImageLauncher()> : despliega para leer...
        Registra el contrato para obtener contenido de la galería de imágenes del dispositivo.
        Permite al usuario elegir una fotografía para su mascota y actualiza la vista previa
        en la interfaz antes de proceder con el guardado definitivo en el servidor.
     */
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            uriImagenSeleccionada = uri
            binding.ivFotoAnimal.setImageURI(uri)
        }
    }

    /* EXPLICACIÓN DEL METODO <onAttach()> : despliega para leer...
        Inicializa las instancias de Firebase y los repositorios de datos al vincular el fragmento.
        Establece las referencias necesarias para la autenticación, base de datos y almacenamiento,
        asegurando que la infraestructura de red esté lista para el proceso de registro.
    */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        auth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance(FirebaseUtils.URL_RTDB)
        databaseReference = firebaseDatabase.reference
        storageReference = FirebaseStorage.getInstance().reference

        val remotePropietario = PropietarioRemote(databaseReference)
        propietarioRepository = PropietarioRepository(remotePropietario)

        val remoteMascota = MascotaRemote(databaseReference)
        mascotaRepository = MascotaRepository(remoteMascota)
    }

    /* EXPLICACIÓN DEL METODO <onCreate()> : despliega para leer...
        Configura los escuchadores de resultados para procesar las respuestas de los diálogos de alerta.
        Activa la lógica de registro si el usuario confirma la acción o redirige al listado de
        mascotas si el usuario decide cancelar la operación y descartar los datos.
    */
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

    /* EXPLICACIÓN DEL METODO <onCreateView()> : despliega para leer...
        Infla la jerarquía de vistas utilizando la clase de vinculación generada para establecer la interfaz.
        Retorna la vista raíz que contiene el formulario de registro de mascota, permitiendo al
        sistema Android gestionar el ciclo de vida visual del componente.
    */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View? {
        binding = FragmentUsuarioRegMascotaBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    /* EXPLICACIÓN DEL METODO <onViewCreated()> : despliega para leer...
        Configura el callback para gestionar la pulsación del botón de retroceso nativo del dispositivo.
        Garantiza que el usuario regrese a la pantalla del listado de mascotas de forma controlada
        cuando intente salir del flujo de registro mediante el sistema de navegación de Android.
    */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navegacionFragment(2)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    /* EXPLICACIÓN DEL METODO <onResume()> : despliega para leer...
        Define los eventos de interacción para los botones de guardado, retorno y selección de imagen.
        Mantiene activa la escucha de eventos de la interfaz cada vez que el fragmento vuelve a
        estar en primer plano para asegurar una respuesta inmediata a las acciones del usuario.
    */
    override fun onResume() {
        super.onResume()

        /* Acciones de los botones del fragment:
            Boton Guardar =>
            Boton Volver => Navega a UsuarioMascotaFragment
            Boton Foto => Navega a la galería de tu teléfono móvil
        */
        binding.btnGuardar.setOnClickListener {

            if (comprobarCamposMascota()){
                mensaje("confirmacion")
            }
        }
        binding.btnVolver.setOnClickListener {
            mensaje("cancelacion")
        }
        binding.ivFotoAnimal.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }
    }

    /* EXPLICACIÓN DEL METODO <navegacionFragment()> : despliega para leer...
        Centraliza la lógica de flujo para retornar a la pantalla del listado de mascotas del usuario.
        Utiliza el NavigatorUsuario para ejecutar la transición de salida, asegurando que
        la navegación sea coherente tanto desde los botones de la interfaz como desde el sistema.
    */
    fun navegacionFragment (num: Int) {
        when (num) {
            1 -> NavigatorUsuario.UsuarioRegMascota_to_UsuarioMascota(this)
            2 -> NavigatorUsuario.UsuarioRegMascota_to_UsuarioMascota(this@UsuarioRegMascotaFragment)
        }
    }

    /* EXPLICACIÓN DEL METODO <mensaje()> : despliega para leer...
        Gestiona el despliegue de los diálogos personalizados de confirmación y cancelación.
        Informa al usuario sobre la importancia de completar el registro o avisa sobre la
        pérdida inminente de los datos introducidos si decide abandonar el proceso actual.
    */
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

    /* EXPLICACIÓN DEL METODO <comprobarCamposMascota()> : despliega para leer...
        Realiza una validación técnica básica de los campos de entrada del formulario.
        Verifica que el nombre de la mascota no esté vacío antes de permitir el avance
        hacia el diálogo de confirmación, evitando registros incompletos en la base de datos.
    */
    fun comprobarCamposMascota(): Boolean {
        val nombre = binding.etNombreAnimal.text.toString().trim()

        if (nombre.isEmpty()) {
            mostrarSnackbar( "Por favor, introduzca como mínimo el nombre de su mascota para el registro")
            return false
        }
        return true
    }

    /* EXPLICACIÓN DEL METODO <registrarMascota()> : despliega para leer...
        Coordina el proceso de alta de la mascota obteniendo el perfil del propietario y subiendo la imagen.
        Gestiona la lógica asíncrona para generar un ID único, cargar el archivo en Storage
        y finalmente persistir la entidad Mascota completa en Realtime Database.
    */
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
                val sexo = binding.spSexo.selectedItem.toString()
                val castracion = binding.spCastracion.selectedItem.toString().equals("Si", ignoreCase = true)
                val pesoActual = pesoTexto.toDoubleOrNull() ?: 0.0

                mascotaRepository.generarIdMascota(
                    { idMasc ->
                        // Logica de imagen
                        if (uriImagenSeleccionada != null) {
                            val folderRef = storageReference.child("fotos_mascotas/$idMasc.jpg")
                            folderRef.putFile(uriImagenSeleccionada!!)
                                .addOnSuccessListener {
                                    folderRef.downloadUrl.addOnSuccessListener { uri ->
                                        val urlDescarga = uri.toString()

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
                                            urlDescarga
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
                                    }
                                }
                                .addOnFailureListener {
                                    mostrarSnackbar("Error al subir la imagen")
                                }
                        } else {
                            // Flujo si no hay imagen (url vacia)
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
                        }
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

    /* EXPLICACIÓN DEL METODO <obtenerFechaActual()> : despliega para leer...
        Genera una cadena de texto representativa del día actual en formato yyyy-MM-dd.
        Utiliza el SimpleDateFormat para normalizar la fecha de registro de la mascota,
        asegurando una ordenación cronológica coherente en la base de datos de Firebase.
    */
    private fun obtenerFechaActual(): String {
        val formato = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        return formato.format(java.util.Date())
    }
}