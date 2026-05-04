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
import com.bumptech.glide.Glide
import com.example.vetycare.R
import com.example.vetycare.database.remote.PropietarioRemote
import com.example.vetycare.database.repository.PropietarioRepository
import com.example.vetycare.databinding.FragmentUsuarioPerfilBinding
import com.example.vetycare.navigation.NavigatorRoot
import com.example.vetycare.navigation.NavigatorUsuario
import com.example.vetycare.ui.container.UsuarioContainerFragment
import com.example.vetycare.ui.dialog.CancelacionDialog
import com.example.vetycare.utils.FirebaseUtils
import com.example.vetycare.utils.mostrarSnackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

/* EXPLICACIÓN DE LA CLASE <UsuarioPerfilFragment()> : despliega para leer...
    Fragmento encargado de gestionar la visualización de los datos personales del propietario y su fotografía de perfil.
    Controla el flujo de cierre de sesión mediante diálogos de advertencia y permite la actualización de la imagen
    de usuario en tiempo real mediante la sincronización con Firebase Storage.
 */
class UsuarioPerfilFragment : Fragment () {
    private lateinit var binding : FragmentUsuarioPerfilBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var propietarioRepository: PropietarioRepository
    private val keyCancelacion = "cancelacion_registro"
    private var idPropietarioFicha: String? = null

    /* EXPLICACIÓN DE LA VARIABLE <galleryLauncher()> : despliega para leer...
        Registra el contrato para abrir la galería del dispositivo y obtener la URI de la imagen seleccionada por el usuario.
        Activa automáticamente el proceso de carga hacia el servidor de Firebase Storage para actualizar de forma
        permanente la fotografía de identidad del propietario registrado.
     */
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            subirFotoAFirebase(it)
        }
    }

    /* EXPLICACIÓN DEL METODO <onAttach()> : despliega para leer...
        Inicializa los servicios fundamentales de Firebase y el acceso a la base de datos sincronizada al vincularse con la actividad.
        Garantiza que las referencias de autenticación y red estén configuradas correctamente antes de cualquier
        interacción del usuario con los datos de su perfil personal.
    */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        auth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance(FirebaseUtils.URL_RTDB)
        databaseReference = firebaseDatabase.reference
    }

    /* EXPLICACIÓN DEL METODO <onCreate()> : despliega para leer...
        Establece la escucha de resultados para el diálogo de cancelación, gestionando el cierre de sesión definitivo en Firebase.
        Al confirmar la acción, invalida las credenciales activas del usuario y ejecuta la navegación hacia el fragmento
        de inicio del sistema para devolver la aplicación a su estado de acceso.
    */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parentFragmentManager.setFragmentResultListener(keyCancelacion,this) { _, bundle ->
            val cancelado = bundle.getBoolean(CancelacionDialog.KEY_CANCELADO)
            if (cancelado) {
                auth.signOut()
                navegacionFragment(1)
            }
        }
    }

    /* EXPLICACIÓN DEL METODO <onCreateView()> : despliega para leer...
        Infla la jerarquía de vistas del fragmento utilizando la clase de vinculación generada para establecer la interfaz de perfil.
        Retorna la vista raíz necesaria para que el sistema Android gestione el ciclo de vida visual del componente
        y permita la interacción con los campos de información personal.
    */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View? {
        binding = FragmentUsuarioPerfilBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    /* EXPLICACIÓN DEL METODO <onViewCreated()> : despliega para leer...
        Inicializa el repositorio de propietarios, lanza la carga de información del perfil y configura el botón físico de retroceso.
        Implementa un callback personalizado para asegurar que el usuario retorne al menú principal de usuario de forma
        controlada al utilizar el sistema de navegación nativo del dispositivo.
    */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val remotePropietario = PropietarioRemote(databaseReference)
        propietarioRepository = PropietarioRepository(remotePropietario)
        cargarDatosUsuario()

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navegacionFragment(2)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    /* EXPLICACIÓN DEL METODO <onResume()> : despliega para leer...
        Establece el modo de solo lectura en los campos de texto del formulario y activa los escuchadores de eventos para los botones.
        Mantiene la interactividad del perfil activa cada vez que el fragmento vuelve al primer plano, asegurando
        que las funciones de cierre de sesión y cambio de foto estén siempre disponibles.
    */
    override fun onResume() {
        super.onResume()

        /* Acciones de los botones del fragment:
            Botón Cerrar Sesión => Navega al InicioPrincipalFragment
            Botón Foto => Navega a la galería de tu teléfono móvil
        */
        binding.btnCerrarsesion.setOnClickListener {
            mensaje("cerrar_sesion")
        }
        binding.ivFoto.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        // Los EditTexts no tendrán funcion en este fragment ya que únicamente son para mostrar la informacion
        binding.etNombre.isEnabled = false
        binding.etApellido.isEnabled = false
        binding.etSexo.isEnabled = false
        binding.etDni.isEnabled = false
        binding.etFecha.isEnabled = false
        binding.etCorreo.isEnabled = false
        binding.etTelefono.isEnabled = false

    }

    /* EXPLICACIÓN DEL METODO <navegacionFragment()> : despliega para leer...
        Centraliza el flujo de salida hacia la pantalla de login principal o el retorno a la vista de inicio del usuario.
        Utiliza los navegadores de raíz y de usuario para ejecutar transiciones coherentes entre los diferentes
        contenedores, garantizando una navegación sin errores dentro de la arquitectura de la app.
    */
    fun navegacionFragment(num: Int) {
        when (num) {
            1 -> NavigatorRoot.Usuario_to_Inicio(this)
            2 -> NavigatorUsuario.UsuarioPerfil_to_UsuarioInicio(this@UsuarioPerfilFragment)
        }
    }

    /* EXPLICACIÓN DEL METODO <mensaje()> : despliega para leer...
        Despliega el diálogo de advertencia para confirmar la intención del usuario de abandonar la sesión actual de la cuenta.
        Permite informar sobre la necesidad de volver a introducir credenciales en el futuro, garantizando
        una acción de salida segura, verificada y consciente por parte del propietario.
    */
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

    /* EXPLICACIÓN DEL METODO <cargarDatosUsuario()> : despliega para leer...
        Recupera el perfil del propietario autenticado mediante su identificador único de Firebase y mapea los datos en la interfaz.
        Utiliza la librería Glide para procesar la fotografía de perfil desde la URL almacenada, gestionando
        automáticamente los estados de carga y las imágenes de sustitución en caso de error.
    */
    private fun cargarDatosUsuario() {
        val auth = auth.currentUser?.uid

        if (auth.isNullOrEmpty()) {
            mostrarSnackbar("No se ha podido encontrar su identificador")
            return
        }

        propietarioRepository.obtenerPropietario(
            auth,
            { idRecuperado, propietario ->
                idPropietarioFicha = idRecuperado
                binding.etNombre.setText(propietario.nombre)
                binding.etApellido.setText(propietario.apellido)
                binding.etSexo.setText(propietario.sexo)
                binding.etDni.setText(propietario.dni)
                binding.etFecha.setText(propietario.fechaNacimiento)
                binding.etCorreo.setText(propietario.email)
                binding.etTelefono.setText(propietario.telefono.toString())

                Glide.with(this)
                    .load(propietario.urlFotoProp)
                    .placeholder(R.drawable.img_usser)
                    .into(binding.ivFoto)
            },
            { mensajeDeError ->
                mostrarSnackbar(mensajeDeError?:"ERROR")
            }
        )
    }

    /* EXPLICACIÓN DEL METODO <subirFotoAFirebase()> : despliega para leer...
        Gestiona la transferencia del archivo de imagen seleccionado hacia el repositorio de almacenamiento de Firebase Storage.
        Al completar la transferencia exitosa, recupera la URL de descarga pública generada para proceder
        a la actualización del enlace permanente en el nodo de la base de datos del propietario.
    */
    private fun subirFotoAFirebase(imageUri: Uri) {
        val idReal = idPropietarioFicha ?: return

        val storageRef = FirebaseStorage.getInstance().reference
        val fotoRef = storageRef.child("fotos_propietarios/${auth.currentUser?.uid}.jpg")

        mostrarSnackbar("Actualizando imagen...")

        fotoRef.putFile(imageUri)
            .addOnSuccessListener {
                fotoRef.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    actualizarUrlEnBaseDeDatos(downloadUrl, idReal)
                }
            }
            .addOnFailureListener {
                mostrarSnackbar("Error al subir la imagen")
            }
    }

    /* EXPLICACIÓN DEL METODO <actualizarUrlEnBaseDeDatos()> : despliega para leer...
        Sobrescribe el campo de la URL de la fotografía en el nodo del propietario y sincroniza el cambio visual en toda la aplicación.
        Notifica al fragmento contenedor para que actualice la cabecera de navegación, manteniendo la
        coherencia de la imagen de identidad en todos los menús y vistas del sistema.
    */
    private fun actualizarUrlEnBaseDeDatos(urlNueva: String, idPropietario: String) {

        val rootRef = FirebaseDatabase.getInstance(FirebaseUtils.URL_RTDB).reference

        rootRef.child("propietarios").child(idPropietario).child("urlFotoProp").setValue(urlNueva)
            .addOnSuccessListener {

                if (isAdded) {
                    // Se actualiza la imagen en la pantalla
                    Glide.with(this)
                        .load(urlNueva)
                        .placeholder(R.drawable.img_usser)
                        .into(binding.ivFoto)

                    val contenedor = parentFragment?.parentFragment as? UsuarioContainerFragment
                    contenedor?.actualizarFotoDesdePerfil(urlNueva)

                    mostrarSnackbar("Foto de perfil actualizada")
                }
            }
            .addOnFailureListener {
                mostrarSnackbar("Error al conectar con la base de datos")
            }
    }
}