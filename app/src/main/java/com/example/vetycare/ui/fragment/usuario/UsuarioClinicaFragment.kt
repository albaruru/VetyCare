package com.example.vetycare.ui.fragment.usuario

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
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

class UsuarioClinicaFragment : Fragment(), ClinicaAdapter.OnClinicaListener{
    private lateinit var binding : FragmentUsuarioClinicaBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var clinicaRepository: ClinicaRepository
    private lateinit var adapterClinica: ClinicaAdapter
    private var listaFiltrada = ArrayList<Clinica>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        auth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance(FirebaseUtils.URL_RTDB)
        databaseReference = firebaseDatabase.reference
        val remoteClinica = ClinicaRemote(databaseReference)
        clinicaRepository = ClinicaRepository(remoteClinica)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View? {
        binding = FragmentUsuarioClinicaBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        instancias()
        // FIXME: BORRAR => crearClinicasDePrueba() // Llenamos la lista con datos de prueba
        configurarRecycler() // Configuramos el RecyclerView
        configurarSpinner() //
    }

    /* FIXME: BORRAR ESTE APARTADO
    private fun crearClinicasDePrueba() {
        listaTodasClinicas.clear()

        listaTodasClinicas.add(Clinica(
            nombre = "Clínica VetyCare Madrid",
            comunidadAutonoma = "Comunidad de Madrid",
            provincia = "Madrid",
            direccion = "Calle Mayor 5",
            telefono = 912345678L
        ))
        listaTodasClinicas.add(Clinica(
            nombre = "VetyCare Sevilla",
            comunidadAutonoma = "Andalucía",
            provincia = "Sevilla",
            direccion = "Av. de la Palmera 10",
            telefono = 954123456L
        ))
        listaTodasClinicas.add(Clinica(
            nombre = "VetyCare Barcelona",
            comunidadAutonoma = "Cataluña",
            provincia = "Barcelona",
            direccion = "Carrer de Balmes 20",
            telefono = 934567890L
        ))
        listaTodasClinicas.add(Clinica(nombre = "VetyCare Málaga",
            comunidadAutonoma = "Andalucía",
            provincia = "Málaga",
            direccion = "Calle Larios 2",
            telefono = 952000111L
        ))

        // Notificamos al adaptador para que pinte los datos de prueba
        adapterClinica.notifyDataSetChanged()
    }


    private fun mostrarTodas() {
        listaFiltrada.clear()
        listaFiltrada.addAll(listaTodasClinicas)
        adapterClinica.notifyDataSetChanged()
    }

    private fun filtrarPorComunidad(comunidad: String) {
        listaFiltrada.clear()
        // Filtramos de la lista maestra aquellas que coincidan con la comunidad
        val filtradas = listaTodasClinicas.filter { it.comunidadAutonoma == comunidad }
        listaFiltrada.addAll(filtradas)
        adapterClinica.notifyDataSetChanged()
    }*/

    override fun onResume() {
        super.onResume()

        /* Acciones de los botones del fragment:
        * -
        * */
        binding.btnVerMapa.setOnClickListener {
            navegacionFragment(1)
        }
    }

    fun navegacionFragment(num : Int) {
        when (num) {
            1 -> NavigatorUsuario.UsuarioClinica_to_UsuarioClinicaMapa(this)
        }
    }

    private fun instancias() {
        listaFiltrada = ArrayList()
        // Pasamos 'this' como tercer parámetro (el listener)
        adapterClinica = ClinicaAdapter(listaFiltrada, requireContext(), this)
    }

    private fun configurarRecycler() {
        // Asignamos el LayoutManager y el Adapter al XML
        binding.rvClinicas.layoutManager = LinearLayoutManager(context)
        binding.rvClinicas.adapter = adapterClinica
    }

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

    override fun onClinicaClick(clinica: Clinica) {

    }
}