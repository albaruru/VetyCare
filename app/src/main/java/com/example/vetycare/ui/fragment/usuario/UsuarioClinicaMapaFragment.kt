package com.example.vetycare.ui.fragment.usuario

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.example.vetycare.R
import com.example.vetycare.database.remote.ClinicaRemote
import com.example.vetycare.database.repository.ClinicaRepository
import com.example.vetycare.databinding.FragmentUsuarioClinicaMapaBinding
import com.example.vetycare.model.entities.Clinica
import com.example.vetycare.navigation.NavigatorUsuario
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.generated.OnPointAnnotationClickListener
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import androidx.core.graphics.createBitmap

/* EXPLICACIÓN DE LA CLASE <UsuarioClinicaMapaFragment()> : despliega para leer...
    Fragmento encargado de la visualización geográfica de las clínicas veterinarias mediante Mapbox.
    Gestiona la representación de marcadores en el mapa, el enfoque dinámico de la cámara hacia
    puntos específicos y la interacción mediante hojas informativas inferiores (BottomSheets).
 */
class UsuarioClinicaMapaFragment : Fragment() {

    private lateinit var binding: FragmentUsuarioClinicaMapaBinding
    private lateinit var clinicaRepository: ClinicaRepository
    private lateinit var pointAnnotationManager: com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
    private val marcadoresClinica = mutableMapOf<String, Clinica>()
    private var clinicaFoco: Clinica? = null

    /* EXPLICACIÓN DEL METODO <onAttach()> : despliega para leer...
        Inicializa el repositorio de clínicas al vincular el fragmento con la actividad.
        Establece la conexión con la base de datos de Firebase para permitir la descarga
        de las coordenadas y datos de contacto de todos los centros veterinarios activos.
    */
    override fun onAttach(context: Context) {
        super.onAttach(context)

        val firebaseDatabase = com.google.firebase.database.FirebaseDatabase
            .getInstance(com.example.vetycare.utils.FirebaseUtils.URL_RTDB)
        val databaseReference = firebaseDatabase.reference

        val remoteClinica = ClinicaRemote(databaseReference)
        clinicaRepository = ClinicaRepository(remoteClinica)
    }

    /* EXPLICACIÓN DEL METODO <onCreate()> : despliega para leer...
        Recupera el objeto clínica desde los argumentos para determinar si existe un punto de enfoque inicial.
        Este proceso permite que el mapa se centre automáticamente en una clínica específica
        cuando el usuario navega desde el listado de búsqueda hacia la cartografía.
    */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        clinicaFoco = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable("clinica_foco", Clinica::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getSerializable("clinica_foco") as? Clinica
        }
    }

    /* EXPLICACIÓN DEL METODO <onCreateView()> : despliega para leer...
        Infla la jerarquía de vistas utilizando ViewBinding para establecer la interfaz de usuario.
        Retorna la vista raíz que contiene el componente de Mapbox y los elementos de control
        necesarios para la navegación y visualización de la información geográfica.
    */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
        ): View {
        binding = FragmentUsuarioClinicaMapaBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    /* EXPLICACIÓN DEL METODO <onViewCreated()> : despliega para leer...
        Configura el estado inicial del mapa y define el comportamiento del botón físico de retroceso.
        Asegura que el dispatcher de la actividad redirija al usuario al listado de clínicas
        de forma controlada cuando intente salir de la vista de mapa.
    */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iniciarMapa()

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navegacionFragment(2)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    /* EXPLICACIÓN DEL METODO <onResume()> : despliega para leer...
        Establece los escuchadores de eventos para los botones de control cuando el fragmento está activo.
        Configura el acceso directo al listado para permitir una transición fluida entre la
        vista de mapa y la representación tabular de los centros veterinarios.
    */
    override fun onResume() {
        super.onResume()

        /* Acciones de los botones del fragment:
            Botón Ver Listado => Navega a UsuarioClinicaFragment
        */
        binding.btnVerListado.setOnClickListener {
            navegacionFragment(1)
        }
    }

    /* EXPLICACIÓN DEL METODO <navegacionFragment()> : despliega para leer...
    Centraliza la lógica de navegación para retornar a la pantalla del listado de clínicas.
    Utiliza el NavigatorUsuario para ejecutar la transición de salida, asegurando
    que el flujo de pantallas se mantenga coherente dentro del módulo de usuario.
*/
    fun navegacionFragment(num: Int) {
        when (num) {
            1 -> NavigatorUsuario.UsuarioClinicaMapa_to_UsuarioClinica(this)
            2 -> NavigatorUsuario.UsuarioClinicaMapa_to_UsuarioClinica(this@UsuarioClinicaMapaFragment)
        }
    }

    /* EXPLICACIÓN DEL METODO <iniciarMapa()> : despliega para leer...
        Carga el estilo visual de Mapbox y establece la posición de la cámara según la entrada.
        Si existe una clínica en foco, centra la vista en ella con un zoom cercano; de lo contrario,
        muestra una vista general del territorio y prepara el gestor de anotaciones.
    */
    private fun iniciarMapa() {
        binding.mapView.mapboxMap.loadStyle(Style.MAPBOX_STREETS) {
            val (puntoInicial, zoomInicial) = if (clinicaFoco != null) {
                // Centrar en la clínica seleccionada
                val lat = clinicaFoco?.coordenadas?.latitud ?: 40.0
                val lng = clinicaFoco?.coordenadas?.longitud ?: -4.0
                Point.fromLngLat(lng, lat) to 15.0 // Zoom cercano
            } else {
                // Vista general de España
                Point.fromLngLat(-4.0, 40.0) to 4.5
            }

            binding.mapView.mapboxMap.setCamera(
                CameraOptions.Builder()
                    .center(puntoInicial)
                    .zoom(zoomInicial)
                    .build()
            )

            pointAnnotationManager = binding.mapView.annotations.createPointAnnotationManager()

            // Si hay una clínica enfocada, mostramos su BottomSheet automáticamente
            clinicaFoco?.let {
                mostrarBottomSheet(
                    it.nombre ?: "", it.provincia ?: "",
                    it.direccion ?: "", it.telefono?.toString() ?: ""
                )
            }

            cargarClinicasEnMapa()
        }
    }

    /* EXPLICACIÓN DEL METODO <cargarClinicasEnMapa()> : despliega para leer...
        Descarga todas las clínicas del repositorio y genera sus marcadores correspondientes sobre el mapa.
        Asocia cada marcador con los datos técnicos del centro y configura el listener de clics
        para disparar la visualización del detalle mediante una hoja de información inferior.
    */
    private fun cargarClinicasEnMapa() {
        clinicaRepository.obtenerTodasLasClinicasActivas(
            onSuccess = { lista ->
                marcadoresClinica.clear()
                pointAnnotationManager.deleteAll()

                val drawable = requireContext().getDrawable(R.drawable.chincheta_vetycare)
                if (drawable == null) {
                    Toast.makeText(requireContext(), "No se pudo cargar el icono del marcador", Toast.LENGTH_SHORT).show()
                    return@obtenerTodasLasClinicasActivas
                }

                val bitmap = drawableToBitmap(drawable)

                lista.forEach { clinica ->
                    val latitud = clinica.coordenadas?.latitud ?: return@forEach
                    val longitud = clinica.coordenadas?.longitud ?: return@forEach

                    val annotation = pointAnnotationManager.create(
                        PointAnnotationOptions()
                            .withPoint(Point.fromLngLat(longitud, latitud))
                            .withIconImage(bitmap)
                            .withIconSize(0.10)
                    )

                    marcadoresClinica[annotation.id.toString()] = clinica
                }

                pointAnnotationManager.addClickListener(OnPointAnnotationClickListener { annotation ->
                    val clinica = marcadoresClinica[annotation.id.toString()]
                    clinica?.let {
                        mostrarBottomSheet(
                            nombre = it.nombre ?: "",
                            provincia = it.provincia ?: "",
                            direccion = it.direccion ?: "",
                            telefono = it.telefono?.toString() ?: ""
                        )
                    }
                    true
                })
            },
            { mensajeDeError ->
                Toast.makeText(requireContext(), mensajeDeError ?: "ERROR al cargar clínicas", Toast.LENGTH_SHORT).show()
            }
        )
    }

    /* EXPLICACIÓN DEL METODO <mostrarBottomSheet()> : despliega para leer...
        Despliega un diálogo emergente inferior con los datos de contacto de la clínica seleccionada.
        Permite al usuario realizar llamadas telefónicas directamente al pulsar sobre el número,
        utilizando un Intent específico para el marcador de llamadas del sistema.
    */
    private fun mostrarBottomSheet(
        nombre: String, provincia: String,
        direccion: String, telefono: String
        ) {
        val bottomSheet = BottomSheetDialog(requireContext())
        val sheetView = layoutInflater.inflate(R.layout.recycler_clinica, null)

        sheetView.findViewById<TextView>(R.id.tv_nombre_clinica).text = nombre
        sheetView.findViewById<TextView>(R.id.tv_provincia).text = provincia
        sheetView.findViewById<TextView>(R.id.tv_direccion).text = direccion
        val tvTelefono = sheetView.findViewById<TextView>(R.id.tv_telefono)

        tvTelefono.text = telefono

        // Logica para la llamada
        tvTelefono.setOnClickListener {
            val numeroLimpio = telefono.replace(" ", "")

            if (numeroLimpio.isNotEmpty()) {
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:$numeroLimpio")
                }
                startActivity(intent)
            }
        }

        bottomSheet.setContentView(sheetView)
        bottomSheet.show()
    }

    /* EXPLICACIÓN DEL METODO <drawableToBitmap()> : despliega para leer...
        Realiza la conversión técnica de un recurso Drawable a un objeto Bitmap para Mapbox.
        Permite utilizar iconos personalizados como marcadores geográficos, dibujando el recurso
        sobre un lienzo (Canvas) para su correcta integración en el motor del mapa.
    */
    private fun drawableToBitmap(drawable: android.graphics.drawable.Drawable): android.graphics.Bitmap {
        val bitmap = createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight)
        val canvas = android.graphics.Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    /* EXPLICACIÓN DE LOS CICLOS DE VIDA <onStart/onStop/onLowMemory/onDestroyView> : despliega para leer...
        Vincula los eventos del ciclo de vida del fragmento con el componente de MapView.
        Garantiza una gestión eficiente de los recursos del mapa, pausando o liberando la
        memoria necesaria según el estado actual de la vista para evitar fugas y bloqueos.
    */
    override fun onStart() { super.onStart(); binding.mapView.onStart() }
    override fun onStop() { super.onStop(); binding.mapView.onStop() }
    override fun onLowMemory() { super.onLowMemory(); binding.mapView.onLowMemory() }
    override fun onDestroyView() {
        super.onDestroyView()
        binding.mapView.onDestroy()
    }
}