package com.example.vetycare.ui.fragment.mascota

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.vetycare.R
import com.example.vetycare.database.remote.MascotaRemote
import com.example.vetycare.database.repository.MascotaRepository
import com.example.vetycare.databinding.FragmentMascotaPerfilBinding
import com.example.vetycare.navigation.NavigatorRoot
import com.example.vetycare.ui.container.MascotaContainerFragment
import com.example.vetycare.ui.dialog.ConfirmacionDialog
import com.example.vetycare.utils.FirebaseUtils
import com.example.vetycare.utils.mostrarSnackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

/* EXPLICACIÓN DE LA CLASE <MascotaPerfilFragment()> : despliega para leer...
    Fragmento encargado de la gestión y visualización detallada del perfil de una mascota.
    Permite consultar datos técnicos, actualizar la fotografía mediante Firebase Storage
    y ejecutar la eliminación definitiva del registro del animal.
 */
class MascotaPerfilFragment : Fragment() {
    private lateinit var binding : FragmentMascotaPerfilBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var mascotaRepository: MascotaRepository
    private val keyConfirmacion = "confirmacion_eliminar_mascota"
    private var idMascotaSeleccionada: String? = null

    /* EXPLICACIÓN DE LA VARIABLE <galleryLauncher()> : despliega para leer...
        Registra el contrato para abrir el selector de contenido de la galería del dispositivo.
        Captura la URI de la imagen seleccionada por el usuario y dispara automáticamente
        el proceso de subida hacia el almacenamiento en la nube de Firebase.
     */
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            subirFotoMascota(it)
        }
    }

    /* EXPLICACIÓN DEL METODO <onAttach()> : despliega para leer...
        Inicializa las referencias de los servicios de Firebase y el repositorio de mascotas.
        Garantiza que la conexión con la base de datos y el sistema de autenticación esté
        establecida antes de que el fragmento comience a procesar información.
    */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        auth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance(FirebaseUtils.URL_RTDB)
        databaseReference = firebaseDatabase.reference
    }

    /* EXPLICACIÓN DEL METODO <onCreate()> : despliega para leer...
        Configura el sistema de escucha para recibir la confirmación desde el diálogo de borrado.
        Si el usuario valida la acción, activa la lógica de eliminación definitiva de la mascota
        y gestiona el retorno automático hacia la pantalla principal de usuario.
    */
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

    /* EXPLICACIÓN DEL METODO <onCreateView()> : despliega para leer...
        Infla la jerarquía de vistas del fragmento utilizando ViewBinding para establecer la interfaz.
        Prepara el contenedor visual y devuelve la vista raíz que permitirá mostrar los detalles
        del animal y los controles de gestión del perfil.
    */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMascotaPerfilBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    /* EXPLICACIÓN DEL METODO <onViewCreated()> : despliega para leer...
        Recupera el identificador de la mascota desde el contenedor padre y prepara el repositorio.
        Lanza la consulta inicial a la base de datos para recuperar y pintar los datos detallados
        del animal en los campos correspondientes de la interfaz.
    */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val container = parentFragment?.parentFragment as? MascotaContainerFragment
        idMascotaSeleccionada = container?.obtenerIdMascotaSeleccionada()

        val remoteMascota = MascotaRemote(databaseReference)
        mascotaRepository = MascotaRepository(remoteMascota)
        cargarDatosMascota()
    }

    /* EXPLICACIÓN DEL METODO <onResume()> : despliega para leer...
        Establece el estado de solo lectura en los campos del perfil y activa los listeners de los botones.
        Asegura que la configuración visual y la interactividad de la pantalla se mantengan
        consistentes cada vez que el fragmento vuelve al primer plano.
    */
    override fun onResume() {
        super.onResume()
        /* Acciones de los botones del fragment:
            Botón Volver => Navega al UsuarioMascotaFragment
            Botón Eliminar => Elimina la mascota del cliente
            Botón Foto => Navega a la galería de tú móvil
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

        binding.ivFotoAnimal.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        binding.btnVolver.setOnClickListener {
            navegacionFragment(1)
        }
        binding.btnEliminar.setOnClickListener {
            mensaje("confirmacion")
        }
    }

    /* EXPLICACIÓN DEL METODO <navegacionFragment()> : despliega para leer...
        Gestiona el flujo de salida desde la ficha de la mascota hacia la pantalla de origen del usuario.
        Utiliza el NavigatorRoot para cerrar el contenedor actual y asegurar un retorno fluido
        hacia el listado principal de mascotas registradas.
    */
    fun navegacionFragment(num: Int) {
        when (num) {
            1 -> NavigatorRoot.Mascota_to_Usuario(this)
        }
    }

    /* EXPLICACIÓN DEL METODO <mensaje()> : despliega para leer...
        Despliega el diálogo de confirmación personalizado para validar acciones críticas del usuario.
        Muestra una advertencia visual sobre las consecuencias de la eliminación antes de permitir
        que el proceso de borrado se ejecute en la base de datos.
    */
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

    /* EXPLICACIÓN DEL METODO <cargarDatosMascota()> : despliega para leer...
        Recupera la información técnica del animal desde el repositorio y la mapea en las vistas.
        Gestiona la carga de la imagen de perfil mediante la librería Glide, incluyendo la
        configuración de imágenes temporales durante la descarga.
    */
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
                    .placeholder(R.drawable.img_mascotas)
                    .into(binding.ivFotoAnimal)
            },
            { mensajeDeError ->
                mostrarSnackbar(mensajeDeError ?: "ERROR")
            }
        )
    }

    /* EXPLICACIÓN DEL METODO <eliminarMascota()> : despliega para leer...
        Ejecuta la orden de baja del registro de la mascota en Firebase Realtime Database.
        Tras recibir la confirmación de éxito del servidor, notifica al usuario mediante un
        aviso visual y actualiza el estado de navegación de la aplicación.
    */
    fun eliminarMascota(){
        val idParaEliminar = idMascotaSeleccionada ?: return

        mascotaRepository.eliminarMascota(
            idParaEliminar,
            {
                if (isAdded) {
                    mostrarSnackbar("La mascota ha sido eliminada correctamente")
                    navegacionFragment(1)
                }
            },
             { error ->
                 if (isAdded) {
                     mostrarSnackbar(error ?: "No se pudo eliminar la mascota")
                 }
            }
        )
    }

    /* EXPLICACIÓN DEL METODO <subirFotoMascota()> : despliega para leer...
        Procesa la transferencia del archivo de imagen seleccionado hacia una carpeta en Firebase Storage.
        Al finalizar la subida, solicita la URL de descarga pública para proceder a la
        actualización del enlace en el registro de la mascota en la base de datos.
    */
    private fun subirFotoMascota(imageUri: Uri) {
        val idMascota = idMascotaSeleccionada ?: return
        val storageRef = FirebaseStorage.getInstance().reference

        val fotoRef = storageRef.child("fotos_mascotas/$idMascota.jpg")
        mostrarSnackbar("Actualizando imagen...")

        fotoRef.putFile(imageUri)
            .addOnSuccessListener {
                fotoRef.downloadUrl.addOnSuccessListener { uri ->
                    actualizarUrlMascotaBBDD(uri.toString(), idMascota)
                }
            }
    }

    /* EXPLICACIÓN DEL METODO <actualizarUrlMascotaBBDD()> : despliega para leer...
        Sobrescribe el campo de la URL de la fotografía en el nodo de la mascota en Realtime Database.
        Sincroniza el cambio visual tanto en el fragmento de perfil actual como en la cabecera
        del contenedor principal para mantener la coherencia en toda la app.
    */
    private fun actualizarUrlMascotaBBDD(urlNueva: String, idMascota: String) {
        val rootRef = FirebaseDatabase.getInstance(FirebaseUtils.URL_RTDB).reference

        rootRef.child("mascotas").child(idMascota).child("urlFotoMasc").setValue(urlNueva)
            .addOnSuccessListener {
                if (isAdded) {
                    Glide.with(this)
                        .load(urlNueva)
                        .placeholder(R.drawable.img_mascotas)
                        .into(binding.ivFotoAnimal)

                    val contenedor = parentFragment?.parentFragment as? MascotaContainerFragment
                    contenedor?.actualizarFotoMascotaDesdePerfil(urlNueva)

                    mostrarSnackbar("¡Foto de mascota actualizada!")
                }
            }
            .addOnFailureListener {
                mostrarSnackbar("Error al actualizar la base de datos de la mascota")
            }
    }
}