package com.example.vetycare.ui.container

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.example.vetycare.R
import com.example.vetycare.database.remote.MascotaRemote
import com.example.vetycare.database.repository.MascotaRepository
import com.example.vetycare.model.entities.Mascota
import com.example.vetycare.navigation.NavigatorRoot
import com.example.vetycare.utils.FirebaseUtils
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import androidx.navigation.fragment.findNavController

class MascotaContainerFragment : Fragment (R.layout.fragment_container_mascota) {
    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var mascotaRepository: MascotaRepository
    private var mascotaSeleccionada: Mascota? = null
    private var idMascotaSeleccionada: String? = null
    private var ivFotoMascotaHeader: ShapeableImageView? = null

    /* EXPLICACIÓN: despliega para leer ...
        El metodo onAttach se ejecuta cuando el fragment se conecta a un Context.
        Primero llama a super.onAttach(context) para mantener el comportamiento original del ciclo de vida del fragment.
        Después inicializa Firebase Auth, la base de datos en tiempo real y la referencia principal de la base de datos.
        Finalmente crea MascotaRemote con esa referencia e inicializa mascotaRepository para gestionar las operaciones relacionadas con mascotas.
    */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        auth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance(FirebaseUtils.URL_RTDB)
        databaseReference = firebaseDatabase.reference

        val remoteMascota = MascotaRemote(databaseReference)
        mascotaRepository = MascotaRepository(remoteMascota)
    }

    /* EXPLICACIÓN: despliega para leer...
        El metodo onCreate se ejecuta al crear el fragment y se encarga de recuperar los datos recibidos por argumentos.
        Primero obtiene el idMascotaSeleccionada usando la clave ARG_ID_MASCOTA.
        Después recupera el objeto Mascota enviado en los argumentos mediante getSerializable.
        Además, comprueba la versión de Android para usar la forma recomendada en TIRAMISU o mantener la compatibilidad con versiones anteriores.
    */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        idMascotaSeleccionada = arguments?.getString(ARG_ID_MASCOTA)

        mascotaSeleccionada =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                arguments?.getSerializable(ARG_MASCOTA, Mascota::class.java)
            } else {
                @Suppress("DEPRECATION")
                arguments?.getSerializable(ARG_MASCOTA) as? Mascota
            }
    }

    /* EXPLICACIÓN: despliega para leer...
        El companion object define constantes que pertenecen a la clase y pueden usarse sin crear una instancia del fragment.
        En este caso, ARG_MASCOTA se utiliza como clave para enviar o recuperar el objeto Mascota desde los argumentos.
        La constante ARG_ID_MASCOTA se usa como clave para enviar o recuperar el id de la mascota seleccionada.
        De esta forma se evita escribir textos repetidos directamente en el código y se reducen posibles errores.
    */
    companion object {
        const val ARG_MASCOTA = "arg_mascota"
        const val ARG_ID_MASCOTA = "arg_id_mascota"
    }

    /* EXPLICACIÓN: despliega para leer...
        El metodo onViewCreated se ejecuta cuando la vista del fragment ya ha sido creada y permite configurar sus elementos visuales.
        Primero obtiene el NavController del NavHostFragment interno y carga los datos de la mascota en el header, como su foto y nombre.
        Después enlaza los botones del menú y les asigna la navegación hacia citas, tratamientos, informes, perfil de mascota, regreso o pantalla principal.
        También permite acceder al perfil pulsando sobre el nombre o la foto de la mascota.
        Finalmente añade un listener para cambiar los iconos del menú según el fragment visible, marcando en negro la sección activa.
    */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navHostFragment = childFragmentManager
            .findFragmentById(R.id.container_host_mascota) as NavHostFragment

        val navController = navHostFragment.navController

        val ivFotoMascota = view.findViewById<ShapeableImageView>(R.id.iv_foto_mascota)
        val tvNombreMascota = view.findViewById<TextView>(R.id.tv_nombre_mascota)

        this.ivFotoMascotaHeader = ivFotoMascota
        cargarDatosMascota(ivFotoMascota, tvNombreMascota)

        val botonCita = view?.findViewById<ImageButton>(R.id.btnCita)
        val botonTratamiento = view?.findViewById<ImageButton>(R.id.btnTratamiento)
        val botonInforme = view?.findViewById<ImageButton>(R.id.btnInforme)
        val botonMascota = view?.findViewById<ImageButton>(R.id.btnMascotas)
        val botonRegresar = view?.findViewById<ImageButton>(R.id.btnRegresar)
        val botonVetyCare = view?.findViewById<ImageButton>(R.id.iv_logo)

        botonCita?.setOnClickListener {
            navController.navigate(R.id.MascotaCitaFragment)
        }
        botonTratamiento?.setOnClickListener {
            navController.navigate(R.id.MascotaTratamientoFragment)
        }
        botonInforme?.setOnClickListener {
            navController.navigate(R.id.MascotaInformeFragment)
        }
        botonMascota?.setOnClickListener {
            navController.navigate(R.id.MascotaPerfilFragment)
        }
        tvNombreMascota?.setOnClickListener {
            navController.navigate(R.id.MascotaPerfilFragment)
        }
        ivFotoMascota?.setOnClickListener {
            navController.navigate(R.id.MascotaPerfilFragment)
        }
        botonRegresar?.setOnClickListener {
            findNavController().popBackStack()
        }
        botonVetyCare?.setOnClickListener {
            NavigatorRoot.Mascota_to_Usuario(this)
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            botonMascota?.setImageResource(R.drawable.btn_huella)
            botonCita?.setImageResource(R.drawable.btn_calendar)
            botonTratamiento?.setImageResource(R.drawable.btn_tratamiento)
            botonInforme?.setImageResource(R.drawable.btn_informes)
            botonRegresar?.setImageResource(R.drawable.btn_regresa)

            when (destination.id) {
                R.id.MascotaPerfilFragment -> {
                    botonMascota?.setImageResource(R.drawable.btn_huella_black)
                }
                R.id.MascotaCitaFragment -> {
                    botonCita?.setImageResource(R.drawable.btn_calendar_black)
                }
                R.id.MascotaTratamientoFragment -> {
                    botonTratamiento?.setImageResource(R.drawable.btn_tratamiento_black)
                }
                R.id.MascotaTratamientoInfoFragment -> {
                    botonTratamiento?.setImageResource(R.drawable.btn_tratamiento_black)
                }
                R.id.MascotaInformeFragment -> {
                    botonInforme?.setImageResource(R.drawable.btn_informes_black)
                }
                R.id.MascotaInformeInfoFragment -> {
                    botonInforme?.setImageResource(R.drawable.btn_informes_black)
                }
            }
        }
    }

    /* EXPLICACIÓN: despliega para leer...
        El metodo actualizarFotoMascotaDesdePerfil actualiza la imagen de la mascota que aparece en el header.
        Primero comprueba que ivFotoMascotaHeader no sea nulo usando let.
        Después utiliza Glide para cargar la nueva imagen desde la URL recibida en urlNueva.
        Mientras se carga la imagen, muestra como placeholder R.drawable.img_mascotas.
        Finalmente coloca la imagen cargada dentro del ImageView correspondiente.
    */
    fun actualizarFotoMascotaDesdePerfil(urlNueva: String) {
        ivFotoMascotaHeader?.let {
            Glide.with(this)
                .load(urlNueva)
                .placeholder(R.drawable.img_mascotas)
                .into(it)
        }
    }

    /* EXPLICACIÓN: despliega para leer...
        El metodo cargarDatosMascota carga la información de la mascota seleccionada en el header del fragment.
        Primero comprueba si existe una mascotaSeleccionada; si no existe, finaliza el metodo.
        Después muestra el nombre de la mascota en el TextView, usando "Mascota" como valor por defecto si el nombre está vacío.
        Finalmente carga la foto de la mascota con Glide, mostrando una imagen por defecto si no hay foto o si ocurre un error al cargarla.
    */
    private fun cargarDatosMascota(
        ivFotoMascota: ImageView?,
        tvNombreMascota: TextView?
    ) {
        val mascota = mascotaSeleccionada ?: return

        tvNombreMascota?.text = mascota.nombre ?: "Mascota"
        val url = mascota.urlFotoMasc
        ivFotoMascota?.let {
            Glide.with(requireContext())
                .load(url)
                .placeholder(R.drawable.img_mascotas)
                .error(R.drawable.img_mascotas)
                .into(it)

        }
    }

    /* EXPLICACIÓN: despliega para leer...
        El metodo obtenerMascotaSeleccionada devuelve el objeto Mascota que está guardado como mascota seleccionada en el fragment.
        Si no hay ninguna mascota seleccionada, devuelve null.
        El metodo obtenerIdMascotaSeleccionada devuelve el id asociado a esa mascota seleccionada.
        Si no se ha recibido o guardado ningún id de mascota, también devuelve null.
    */
    fun obtenerMascotaSeleccionada(): Mascota? = mascotaSeleccionada
    fun obtenerIdMascotaSeleccionada(): String? = idMascotaSeleccionada
}