package com.example.vetycare.ui.container

import android.content.Context
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.example.vetycare.R
import com.example.vetycare.database.remote.PropietarioRemote
import com.example.vetycare.database.repository.PropietarioRepository
import com.example.vetycare.utils.FirebaseUtils
import com.example.vetycare.utils.mostrarSnackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class UsuarioContainerFragment : Fragment (R.layout.fragment_container_usuario) {

    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var propietarioRepository: PropietarioRepository
    private var ivFotoUsuarioHeader: ImageView? = null

    /* EXPLICACIÓN: despliega para leer...
        El metodo onAttach se ejecuta cuando el fragment se asocia a un Context.
        Primero llama a super.onAttach(context) para mantener el comportamiento original del fragment.
        Después inicializa Firebase Auth para gestionar la autenticación del usuario.
        También obtiene la instancia de Firebase Realtime Database usando la URL definida en FirebaseUtils.URL_RTDB.
        Finalmente guarda la referencia principal de la base de datos en databaseReference para poder usarla después en otras operaciones.
    */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        auth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance(FirebaseUtils.URL_RTDB)
        databaseReference = firebaseDatabase.reference
    }

    /* EXPLICACIÓN: despliega para leer...
        El metodo onViewStateRestored se ejecuta cuando la vista del fragment ya ha sido restaurada y está lista para usarse.
        Primero inicializa el repositorio de propietarios y obtiene el NavController del NavHostFragment hijo para gestionar la navegación interna.
        Después enlaza los botones, el nombre y la imagen del usuario con sus elementos visuales del layout, y carga los datos del propietario en el header.
        A continuación asigna a cada botón su navegación correspondiente, permitiendo moverse entre mascotas, calendario, perfil, clínicas e inicio.
        Finalmente añade un listener al NavController para cambiar los iconos del menú según el fragment visible, marcando en negro la sección activa.
    */
    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        val remotePropietario = PropietarioRemote(databaseReference)
        propietarioRepository = PropietarioRepository(remotePropietario)

        val navHostFragment = childFragmentManager
            .findFragmentById(R.id.container_host_usuario) as NavHostFragment

        val navController = navHostFragment.navController

        // Bindemos nuestras variables con los botones existentes
        val botonPerfilMascotas = view?.findViewById<ImageButton> (R.id.btnIniMasc)
        val botonCalendario = view?.findViewById<ImageButton> (R.id.btnCalendarUsu)
        val botonPerfilUsuario = view?.findViewById<ImageButton> (R.id.btnUsuario)
        val botonClinicas = view?.findViewById<ImageButton> (R.id.btnClinicas)
        val botonVetyCare = view?.findViewById<ImageButton>(R.id.iv_logo)
        val tvNombreUsuario = view?.findViewById<TextView>(R.id.tv_nombre_dueno)
        val ivFotoUsuario = view?.findViewById<ImageView>(R.id.iv_foto_dueno)

        this.ivFotoUsuarioHeader = ivFotoUsuario
        cargarDatosPropietario(tvNombreUsuario,ivFotoUsuario)

        // Asignamos la navegación en las variables de los botones creados
        botonPerfilMascotas?.setOnClickListener {
            navController.navigate(R.id.UsuarioMascotaFragment)
        }
        botonCalendario?.setOnClickListener {
            navController.navigate(R.id.UsuarioCalendario)
        }
        botonPerfilUsuario?.setOnClickListener {
            navController.navigate(R.id.UsuarioPerfilFragment)
        }
        tvNombreUsuario?.setOnClickListener {
            navController.navigate(R.id.UsuarioPerfilFragment)
        }
        ivFotoUsuario?.setOnClickListener {
            navController.navigate(R.id.UsuarioPerfilFragment)
        }
        botonClinicas?.setOnClickListener {
            navController.navigate(R.id.UsuarioClinicaFragment)
        }
        botonVetyCare?.setOnClickListener {
            navController.navigate(R.id.UsuarioInicioFragment)
        }
        navController.addOnDestinationChangedListener { _, destination, _ ->
            // Ponemos los iconos por defecto, para que cada vez que se cambie de fragment, se limpie los iconos.
            botonPerfilMascotas?.setImageResource(R.drawable.btn_huella)
            botonCalendario?.setImageResource(R.drawable.btn_calendar)
            botonPerfilUsuario?.setImageResource(R.drawable.btn_user)
            botonClinicas?.setImageResource(R.drawable.btn_clinicas)

            // Cambiamos el icono de color según el fragment que se encuentra visible
            when (destination.id) {
                R.id.UsuarioMascotaFragment -> {
                    botonPerfilMascotas?.setImageResource(R.drawable.btn_huella_black)
                }
                R.id.UsuarioRegMascota -> {
                    botonPerfilMascotas?.setImageResource(R.drawable.btn_huella_black)
                }
                R.id.UsuarioCalendario -> {
                    botonCalendario?.setImageResource(R.drawable.btn_calendar_black)
                }
                R.id.UsuarioPerfilFragment -> {
                    botonPerfilUsuario?.setImageResource(R.drawable.btn_user_black)
                }
                R.id.UsuarioClinicaFragment -> {
                    botonClinicas?.setImageResource(R.drawable.btn_clinicas_black)
                }
                R.id.usuarioClinicaMapaFragment -> {
                    botonClinicas?.setImageResource(R.drawable.btn_clinicas_black)
                }
            }
        }
    }

    /* EXPLICACIÓN: despliega para leer ...
        El metodo actualizarFotoDesdePerfil actualiza la imagen del usuario que aparece en el header.
        Primero comprueba que ivFotoUsuarioHeader no sea nulo usando let.
        Después utiliza Glide para cargar la nueva imagen desde la URL recibida en urlNueva.
        Mientras se carga la imagen, muestra como placeholder R.drawable.img_usser.
        Finalmente coloca la imagen cargada dentro del ImageView correspondiente.
    */
    fun actualizarFotoDesdePerfil(urlNueva: String) {
        ivFotoUsuarioHeader?.let {
            Glide.with(this)
                .load(urlNueva)
                .placeholder(R.drawable.img_usser)
                .into(it)
        }
    }

    /* EXPLICACIÓN: despliega para leer...
        El metodo cargarDatosPropietario obtiene el uid del usuario autenticado actualmente.
        Si no hay ningún usuario autenticado, finaliza el metodo sin realizar más acciones.
        Después llama a propietarioRepository.obtenerPropietario para recuperar los datos del propietario asociado a ese uid.
        Si la carga es correcta, muestra el nombre y apellido en el TextView y carga la foto con Glide.
        Si ocurre algún error, muestra un Snackbar con el mensaje recibido o uno por defecto.
    */
    private fun cargarDatosPropietario(
        tvNombreUsuario: TextView?,
        ivFotoUsuario: ImageView?
    ) {
        val auth = auth.currentUser?.uid
        if (auth.isNullOrEmpty()) { return }
        propietarioRepository.obtenerPropietario(
            auth,
            { idProp, propietario ->
                tvNombreUsuario?.text = propietario.nombre + " " + propietario.apellido
                val url = propietario.urlFotoProp
                ivFotoUsuario?.let {
                    Glide.with(requireContext())
                        .load(url)
                        .placeholder(R.drawable.img_usser)
                        .error(R.drawable.img_usser)
                        .into(it)
                }
            },
            { mensajeDeError ->
                mostrarSnackbar(mensajeDeError?:"ERROR al cargar datos en el container")
            }
        )
    }
}