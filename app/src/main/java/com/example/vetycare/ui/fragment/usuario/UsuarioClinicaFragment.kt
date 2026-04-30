package com.example.vetycare.ui.fragment.usuario

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vetycare.R
import com.example.vetycare.adapter.ClinicaAdapter
import com.example.vetycare.database.remote.ClinicaRemote
import com.example.vetycare.database.repository.ClinicaRepository
import com.example.vetycare.databinding.FragmentUsuarioClinicaBinding
import com.example.vetycare.model.entities.Clinica
import com.example.vetycare.navigation.NavigatorUsuario
import com.example.vetycare.utils.FirebaseUtils
import com.example.vetycare.utils.mostrarSnackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

/* EXPLICACIÓN DE LA CLASE <UsuarioClinicaFragment()> : despliega para leer...
    Fragmento encargado de gestionar la búsqueda y visualización de clínicas veterinarias asociadas.
    Permite filtrar los centros por comunidad autónoma mediante un selector y navegar
    hacia una vista de mapa para localizar geográficamente la clínica seleccionada.
 */
class UsuarioClinicaFragment : Fragment(), ClinicaAdapter.OnClinicaListener{
    private lateinit var binding : FragmentUsuarioClinicaBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var clinicaRepository: ClinicaRepository
    private lateinit var adapterClinica: ClinicaAdapter
    private var listaFiltrada = ArrayList<Clinica>()

    /* EXPLICACIÓN DEL METODO <onAttach()> : despliega para leer...
        Inicializa los servicios de Firebase y el repositorio de clínicas al vincularse con la actividad.
        Prepara la infraestructura de datos necesaria para realizar consultas a la base de datos
        en tiempo real basándose en la ubicación seleccionada por el usuario.
    */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        auth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance(FirebaseUtils.URL_RTDB)
        databaseReference = firebaseDatabase.reference
        val remoteClinica = ClinicaRemote(databaseReference)
        clinicaRepository = ClinicaRepository(remoteClinica)
    }

    /* EXPLICACIÓN DEL METODO <onCreateView()> : despliega para leer...
        Infla el diseño XML del fragmento utilizando la clase de vinculación de vistas generada.
        Establece la interfaz de usuario inicial y devuelve la vista raíz que contiene el
        listado y los controles de filtrado de clínicas.
    */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View? {
        binding = FragmentUsuarioClinicaBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    /* EXPLICACIÓN DEL METODO <onViewCreated()> : despliega para leer...
        Configura los componentes de la interfaz, los adaptadores y el controlador del botón de retroceso.
        Asegura que el sistema de navegación devuelva al usuario a la pantalla de inicio al usar
        el gesto nativo de Android mientras se encuentra en el buscador de clínicas.
    */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        instancias()
        configurarRecycler()
        configurarSpinner()

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navegacionFragment(2)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    /* EXPLICACIÓN DEL METODO <onResume()> : despliega para leer...
        Establece los escuchadores de clics para los elementos interactivos cuando el fragmento está activo.
        Configura el acceso directo al mapa general para que el usuario pueda visualizar todas las
        clínicas disponibles sin filtros específicos.
    */
    override fun onResume() {
        super.onResume()

        /* Acciones de los botones del fragment:
            Botón Ver Mapa => Navega a UsuarioClinicaMapaFragment
        */
        binding.btnVerMapa.setOnClickListener {
            navegacionFragment(1)
        }
    }

    /* EXPLICACIÓN DEL METODO <navegacionFragment()> : despliega para leer...
        Centraliza la lógica de navegación hacia el mapa de clínicas o de vuelta al inicio de usuario.
        Utiliza el NavigatorUsuario para ejecutar las transiciones entre fragmentos, manteniendo
        un flujo de navegación coherente dentro del módulo de usuario.
    */
    fun navegacionFragment(num : Int) {
        when (num) {
            1 -> NavigatorUsuario.UsuarioClinica_to_UsuarioClinicaMapa(this)
            2 -> NavigatorUsuario.UsuarioClinica_to_UsuarioInicio(this@UsuarioClinicaFragment)
        }
    }

    /* EXPLICACIÓN DEL METODO <instancias()> : despliega para leer...
        Inicializa la colección de datos filtrados y el adaptador específico para el listado de clínicas.
        Vicula el fragmento como listener de clics para permitir la interacción con cada tarjeta
        del RecyclerView que representa a un centro veterinario.
    */
    private fun instancias() {
        listaFiltrada = ArrayList()
        adapterClinica = ClinicaAdapter(listaFiltrada, requireContext(), this)
    }

    /* EXPLICACIÓN DEL METODO <configurarRecycler()> : despliega para leer...
        Define la estructura técnica del RecyclerView estableciendo su gestor de diseño y su adaptador.
        Prepara el componente visual para renderizar la lista de clínicas de forma vertical y
        eficiente a medida que se cargan los datos desde el servidor.
    */
    private fun configurarRecycler() {
        binding.rvClinicas.layoutManager = LinearLayoutManager(context)
        binding.rvClinicas.adapter = adapterClinica
    }

    /* EXPLICACIÓN DEL METODO <configurarSpinner()> : despliega para leer...
        Configura el selector de zonas geográficas con un adaptador de recursos y un escuchador de selección.
        Gestiona la lógica de filtrado en cascada, disparando la carga de datos específicos cada vez
        que el usuario elige una comunidad autónoma diferente en el desplegable.
    */
    private fun configurarSpinner() {
        val spinnerAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.opciones_comunidades,
            android.R.layout.simple_spinner_item
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spZona.adapter = spinnerAdapter

        binding.spZona.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val comunidadSeleccionada = parent?.getItemAtPosition(position).toString()

                if (position == 0) {
                    listaFiltrada.clear()
                    adapterClinica.notifyDataSetChanged()
                } else {
                    val claveComunidad = obtenerClaveComunidad(comunidadSeleccionada)
                    if (claveComunidad != null) {
                        cargarClinicasPorComunidad(claveComunidad)
                    } else {
                        listaFiltrada.clear()
                        adapterClinica.notifyDataSetChanged()
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    /* EXPLICACIÓN DEL METODO <obtenerClaveComunidad()> : despliega para leer...
        Mapea el nombre visual de la comunidad autónoma con su identificador técnico en el backend.
        Transforma la selección del usuario en una clave compatible con la estructura de nodos de
        Firebase, permitiendo realizar consultas filtradas precisas.
    */
    private fun obtenerClaveComunidad(nombreVisible: String): String? {
        return when (nombreVisible) {
            "Andalucía" -> "andalucia"
            "Aragón" -> "aragon"
            "Asturias" -> "asturias"
            "Baleares" -> "islas_baleares"
            "Canarias" -> "islas_canarias"
            "Cantabria" -> "cantabria"
            "Castilla-La Mancha" -> "castilla_la_mancha"
            "Castilla y León" -> "castilla_y_leon"
            "Cataluña" -> "cataluna"
            "Comunidad de Madrid" -> "madrid"
            "Comunidad Valenciana" -> "valencia"
            "Extremadura" -> "extremadura"
            "Galicia" -> "galicia"
            "La Rioja" -> "la_rioja"
            "Murcia" -> "murcia"
            "Navarra" -> "navarra"
            "País Vasco" -> "pais_vasco"
            else -> null
        }
    }

    /* EXPLICACIÓN DEL METODO <cargarClinicasPorComunidad()> : despliega para leer...
        Solicita al repositorio los centros activos pertenecientes a la clave de comunidad proporcionada.
        Limpia los resultados anteriores, añade los nuevos datos a la lista filtrada y notifica al
        adaptador para que actualice la interfaz visual de forma inmediata.
    */
    private fun cargarClinicasPorComunidad(claveComunidad: String) {
        clinicaRepository.obtenerClinicasActivasPorComunidad(
            claveComunidad,
            onSuccess = { lista ->
                listaFiltrada.clear()
                listaFiltrada.addAll(lista.map { it.second })
                adapterClinica.notifyDataSetChanged()
            },
            onError = { error ->
                listaFiltrada.clear()
                adapterClinica.notifyDataSetChanged()
                mostrarSnackbar(error ?: "ERROR al cargar clínicas")
            }
        )
    }

    /* EXPLICACIÓN DEL METODO <onClinicaClick()> : despliega para leer...
        Captura la interacción con una clínica individual para enfocarla directamente en el mapa.
        Empaqueta el objeto Clinica en un Bundle serializable y utiliza el NavController para
        navegar al fragmento del mapa pasando la información del centro seleccionado.
    */
    override fun onClinicaClick(clinica: Clinica) {
        val bundle = Bundle().apply {
            putSerializable("clinica_foco", clinica)
        }

        findNavController().navigate(
            R.id.usuarioClinicaMapaFragment,
            bundle
        )
    }
}