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
import com.example.vetycare.adapter.MascotaAdapter
import com.example.vetycare.databinding.FragmentUsuarioClinicaBinding
import com.example.vetycare.model.entities.Clinica
import com.example.vetycare.navigation.NavigatorRoot
import com.example.vetycare.navigation.NavigatorUsuario

class UsuarioClinicaFragment : Fragment(), ClinicaAdapter.OnClinicaListener{
    private lateinit var binding : FragmentUsuarioClinicaBinding
    private lateinit var adapterClinica: ClinicaAdapter
    private val listaTodasClinicas = ArrayList<Clinica>()
    private var listaFiltrada = ArrayList<Clinica>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View? {
        binding = FragmentUsuarioClinicaBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        instancias()
        crearClinicasDePrueba() // Llenamos la lista con datos de prueba
        configurarRecycler() // Configuramos el RecyclerView
        configurarSpinner() //
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

    private fun configurarSpinner() {
        // Cargamos las opciones del string-array que me has pasado
        val spinnerAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.opciones_comunidades,
            android.R.layout.simple_spinner_item
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spZona.adapter = spinnerAdapter

        // Listener para detectar cuando el usuario elige una comunidad
        binding.spZona.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val comunidadSeleccionada = parent?.getItemAtPosition(position).toString()

                // Si la posición es 0 ("Seleccione una comunidad:") o vacío, mostramos todas
                if (position == 0) {
                    mostrarTodas()
                } else {
                    filtrarPorComunidad(comunidadSeleccionada)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
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
    }

    override fun onClinicaClick(clinica: Clinica) {

    }

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
}