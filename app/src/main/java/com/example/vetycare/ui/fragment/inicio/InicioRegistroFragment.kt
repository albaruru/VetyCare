package com.example.vetycare.ui.fragment.inicio

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.example.vetycare.database.remote.PropietarioRemote
import com.example.vetycare.database.repository.MascotaRepository
import com.example.vetycare.database.repository.PropietarioRepository
import com.example.vetycare.databinding.FragmentInicioRegistroBinding
import com.example.vetycare.model.entities.Propietario
import com.example.vetycare.navigation.NavigatorInicio
import com.example.vetycare.ui.dialog.CancelacionDialog
import com.example.vetycare.ui.dialog.ConfirmacionDialog
import com.example.vetycare.utils.FirebaseUtils
import com.example.vetycare.utils.mostrarSnackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import androidx.activity.result.contract.ActivityResultContracts

/* EXPLICACIÓN DE LA CLASE <InicioRegistroFragment()> : despliega para leer...
    Fragmento encargado de gestionar el registro completo de nuevos propietarios en el sistema.
    Controla la validación de los datos de entrada, la carga de fotografías a Firebase Storage
    y la creación de perfiles tanto en Authentication como en Realtime Database.
 */
class InicioRegistroFragment : Fragment() {
    private lateinit var binding : FragmentInicioRegistroBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var propietarioRepository: PropietarioRepository
    private lateinit var mascotaRepository: MascotaRepository
    private val keyConfirmacion = "confirmacion_registro"
    private val keyCancelacion = "cancelacion_registro"
    private lateinit var storageReference: StorageReference
    private var uriImagenSeleccionada: Uri? = null

    /* EXPLICACIÓN DE LA VARIABLE <pickImageLauncher()> : despliega para leer...
        Registra el contrato para abrir la galería de imágenes del dispositivo móvil.
        Permite al usuario seleccionar una fotografía de perfil y muestra una previsualización
        en la interfaz antes de proceder con el proceso de subida a la nube.
     */
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            uriImagenSeleccionada = uri
            binding.ivFoto.setImageURI(uri)
        }
    }

    /* EXPLICACIÓN DEL METODO <onAttach()> : despliega para leer...
        Inicializa las referencias de Firebase y el repositorio de propietarios al vincular el fragmento.
        Asegura que los servicios de base de datos, autenticación y almacenamiento de archivos estén
        listos para ser utilizados antes de que el usuario interactúe con el formulario.
    */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        auth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance(FirebaseUtils.URL_RTDB)
        databaseReference = firebaseDatabase.reference
        storageReference = FirebaseStorage.getInstance().reference

        val remotePropietario = PropietarioRemote(databaseReference)
        propietarioRepository = PropietarioRepository(remotePropietario)
    }

    /* EXPLICACIÓN DEL METODO <onCreate()> : despliega para leer...
        Configura los escuchadores de resultados para gestionar las respuestas de los diálogos emergentes.
        Permite ejecutar la lógica de registro si el usuario confirma la acción o redirigir al inicio
        si decide cancelar y descartar los datos introducidos en el formulario.
    */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        parentFragmentManager.setFragmentResultListener(keyConfirmacion,this) { _, bundle ->
            val confirmado = bundle.getBoolean(ConfirmacionDialog.KEY_CONFIRMADO)
            if (confirmado) {
                registrarUsuario()
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
        Infla la jerarquía de vistas del fragmento utilizando la clase de vinculación generada.
        Establece el diseño visual del formulario de registro y prepara el binding para
        acceder a todos los componentes de la interfaz de forma segura.
    */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View? {
        binding = FragmentInicioRegistroBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    /* EXPLICACIÓN DEL METODO <onViewCreated()> : despliega para leer...
        Implementa un callback para capturar y gestionar la pulsación del botón físico de retroceso.
        Garantiza que el usuario regrese a la pantalla de acceso principal de forma controlada
        cuando intente salir del flujo de registro mediante el sistema nativo de Android.
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
        Configura los listeners de los botones de guardado, retorno y selección de fotografía.
        Mantiene la interactividad de la pantalla activa cada vez que el fragmento vuelve a
        estar en primer plano para el usuario durante el ciclo de vida.
    */
    override fun onResume() {
        super.onResume()

        /* Acciones de los botones del fragment:
            Botón Guardar => Recogeremos el correo y la contraseña para cambiar las credenciales del usuario en FireBase
            Botón Volver => Descarta cualquier información introducida en nuestros bloques de texto y volvemos a la pantalla login
            Botón Foto => Te lleva a la galería del móvil
        */
        binding.btnGuardar.setOnClickListener {
            if(comprobarCampos()){
            mensaje("confirmacion")
            }
        }
        binding.btnVolver.setOnClickListener {

            mensaje("cancelacion")
        }
        binding.ivFoto.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }
    }

    /* EXPLICACIÓN DEL METODO <navegacionFragment()> : despliega para leer...
        Centraliza el flujo de salida hacia la pantalla de inicio principal de la aplicación.
        Utiliza el navegador del módulo de inicio para realizar la transición, asegurando
        que el usuario regrese correctamente a la vista de login tras el registro o cancelación.
    */
    fun navegacionFragment(num : Int) {
        when (num) {
            1 -> NavigatorInicio.InicioRegistro_to_InicioPrincipal(this)
            2 -> NavigatorInicio.InicioRegistro_to_InicioPrincipal(this@InicioRegistroFragment)
        }
    }

    /* EXPLICACIÓN DEL METODO <mensaje()> : despliega para leer...
        Gestiona el despliegue de los diálogos de confirmación y cancelación personalizados.
        Permite informar al usuario sobre la importancia de completar el registro o avisar
        sobre la pérdida inminente de los datos si decide abandonar el formulario.
    */
    fun mensaje (tipo: String) {
        when (tipo) {
            "confirmacion" -> {
                ConfirmacionDialog.nuevoDialog(
                    "CONFIRMAR REGISTRO DE USUARIO",
                    "¿Deseas completar el registro?",
                    keyConfirmacion
                ).show(parentFragmentManager,"ConfirmacionDialog")
            }
            "cancelacion" -> {
                CancelacionDialog.nuevoDialog(
                    "CANCELACION REGISTRO DE USUARIO",
                    "¿Deseas cancelar el registro? \nTodos los datos introducidos se perderán.",
                    keyCancelacion
                ).show(parentFragmentManager,"CancelacionDialog")
            }
        }
    }

    /* EXPLICACIÓN DEL METODO <comprobarCampos()> : despliega para leer...
        Realiza una validación lógica y técnica de todos los campos del formulario de registro.
        Verifica la obligatoriedad de los datos, el formato del correo electrónico y la
        longitud del teléfono antes de permitir el envío de información a Firebase.
    */
    fun comprobarCampos(): Boolean{
        val nombre = binding.etNombre.text.toString().trim()
        val apellido = binding.etApellido.text.toString().trim()
        val sexo = binding.spSexo.selectedItem.toString().trim()
        val dni = binding.etDni.text.toString().trim()
        val fecha = binding.etFecha.text.toString().trim()
        val correo = binding.etCorreo.text.toString().trim()
        val telefono = binding.etTelefono.text.toString().trim()
        val pass = binding.etPass.text.toString().trim()

        if (nombre.isEmpty() || apellido.isEmpty() || dni.isEmpty() ||
            fecha.isEmpty() || correo.isEmpty() || telefono.isEmpty() || pass.isEmpty()) {
            mostrarSnackbar("Por favor, rellena todos los campos")
            return false
        }
        if (sexo.equals("Selecciona una opción", ignoreCase = true)) {
            mostrarSnackbar("Selecciona el sexo")
            return false
        }
        if (telefono.length != 9) {
            mostrarSnackbar("El teléfono debe tener 9 dígitos")
            return false
        }
        if (telefono.toLongOrNull() == null) {
            mostrarSnackbar("El teléfono solo debe contener números")
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            mostrarSnackbar("Introduce un correo válido")
            return false
        }
        if (pass.length < 6) {
            mostrarSnackbar("La contraseña debe tener al menos 6 caracteres")
            return false
        }

        return true
    }

    /* EXPLICACIÓN DEL METODO <registrarUsuario()> : despliega para leer...
        Ejecuta la creación del nuevo usuario en Firebase Auth y la subida de la imagen a Storage.
        Tras una respuesta exitosa, utiliza el repositorio para guardar el objeto Propietario
        completo en la base de datos sincronizada en tiempo real.
    */
    private fun registrarUsuario () {
        val nombre = binding.etNombre.text.toString().trim()
        val apellido = binding.etApellido.text.toString()
        val sexo = binding.spSexo.selectedItem.toString().trim()
        val dni = binding.etDni.text.toString().trim()
        val fechaNacimiento = binding.etFecha.text.toString().trim()
        val correo = binding.etCorreo.text.toString().trim()
        val telefonoTexto = binding.etTelefono.text.toString().trim()
        val pass = binding.etPass.text.toString().trim()

        val telefono = telefonoTexto.toLongOrNull()
        if (telefono == null) {
            mostrarSnackbar("El teléfono debe ser numérico.")
            return
        }

        auth.createUserWithEmailAndPassword(correo, pass)
            .addOnSuccessListener { authResult ->
                val auth = authResult.user?.uid
                if (auth.isNullOrEmpty()) {
                    mostrarSnackbar("No se ha podido obtener el UID del usuario")
                    return@addOnSuccessListener
                }
                // Logica de imagen
                if (uriImagenSeleccionada != null) {
                    val folderRef = storageReference.child("fotos_propietarios/$auth.jpg")
                    folderRef.putFile(uriImagenSeleccionada!!)
                        .addOnSuccessListener {
                            folderRef.downloadUrl.addOnSuccessListener { uri ->
                                val urlDescarga = uri.toString()
                propietarioRepository.generarIdPropietario(
                    { idProp ->
                        val propietarioNuevo = Propietario(
                            id = idProp,
                            activa = true,
                            apellido = apellido,
                            authUid = auth,
                            dni = dni,
                            email = correo,
                            fechaRegistro = obtenerFechaActual(),
                            fechaNacimiento = fechaNacimiento,
                            nombre = nombre,
                            telefono = telefono,
                            sexo = sexo,
                            urlFotoProp = urlDescarga
                        )
                        propietarioRepository.crearPropietario(
                            idProp,
                            propietarioNuevo,
                            {
                                mostrarSnackbar("Usuario registrado correctamente")
                                navegacionFragment(1)
                            },
                            { mensajeDeError ->
                                mostrarSnackbar(mensajeDeError ?: "ERROR al guardar el propietario en la base de datos")
                            }
                        )
                    },
                    { mensajeDeError ->
                        mostrarSnackbar(mensajeDeError ?: "ERROR")
                    }
                )
                            }
                        }
                        .addOnFailureListener {
                            mostrarSnackbar("Error al subir la imagen")
                        }
                } else {
                    // Flujo original si no hay imagen (url vacía)
                    propietarioRepository.generarIdPropietario(
                        { idProp ->
                            val propietarioNuevo = Propietario(
                                id = idProp,
                                activa = true,
                                apellido = apellido,
                                authUid = auth,
                                dni = dni,
                                email = correo,
                                fechaRegistro = obtenerFechaActual(),
                                fechaNacimiento = fechaNacimiento,
                                nombre = nombre,
                                telefono = telefono,
                                sexo = sexo,
                                urlFotoProp = ""
                            )
                            propietarioRepository.crearPropietario(
                                idProp,
                                propietarioNuevo,
                                {
                                    mostrarSnackbar("Usuario registrado correctamente")
                                    navegacionFragment(1)
                                },
                                { mensajeDeError ->
                                    mostrarSnackbar(mensajeDeError ?: "ERROR al guardar el propietario en la base de datos")
                                }
                            )
                        },
                        { mensajeDeError ->
                            mostrarSnackbar(mensajeDeError ?: "ERROR")
                        }
                    )
                }
            }
            .addOnFailureListener { exception ->
                mostrarSnackbar(exception.message ?: "ERROR al crear el usuaruo en Firebase Auth")
            }
    }

    /* EXPLICACIÓN DEL METODO <obtenerFechaActual()> : despliega para leer...
        Genera una cadena de texto con la fecha del sistema en formato yyyy-MM-dd.
        Este valor se utiliza para registrar cronológicamente el momento exacto en el que
        un nuevo propietario completa su alta en la plataforma VetyCare.
    */
    private fun obtenerFechaActual() : String {
        val formato = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        return formato.format(java.util.Date())
    }
}