package com.example.vetycare.ui.fragment.mascota

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vetycare.adapter.InformeAdapter
import com.example.vetycare.database.remote.DiagnosticoRemote
import com.example.vetycare.database.repository.DiagnosticoRepository
import com.example.vetycare.databinding.FragmentMascotaInformeBinding
import com.example.vetycare.model.entities.Diagnostico
import com.example.vetycare.model.entities.Mascota
import com.example.vetycare.model.entities.Medicamento
import com.example.vetycare.model.entities.Patologia
import com.example.vetycare.model.entities.Tratamiento
import com.example.vetycare.navigation.NavigatorMascota
import com.example.vetycare.ui.container.MascotaContainerFragment
import com.example.vetycare.utils.FirebaseUtils
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MascotaInformeFragment : Fragment(), InformeAdapter.OnInformeListener {
    private var _binding: FragmentMascotaInformeBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapterInforme: InformeAdapter
    private val listaInformes = ArrayList<Diagnostico>()
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var diagnosticoRepository: DiagnosticoRepository
    private var mascotaSeleccionada: Mascota? = null
    private var idMascotaSeleccionada: String? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)

        firebaseDatabase = FirebaseDatabase.getInstance(FirebaseUtils.URL_RTDB)
        databaseReference = firebaseDatabase.reference

        val remoteDiagnostico = DiagnosticoRemote(databaseReference)
        diagnosticoRepository = DiagnosticoRepository(remoteDiagnostico)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentMascotaInformeBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val containerFragment = parentFragment?.parentFragment as? MascotaContainerFragment
        mascotaSeleccionada = containerFragment?.obtenerMascotaSeleccionada()
        idMascotaSeleccionada = containerFragment?.obtenerIdMascotaSeleccionada()

        adapterInforme = InformeAdapter(listaInformes, requireContext(), this)
        binding.rvInformes.layoutManager = LinearLayoutManager(context)
        binding.rvInformes.adapter = adapterInforme

        cargarInformesMascota()
    }
    /* FIXME: BORRAR
    private fun crearInformesDePrueba() {
        listaInformes.clear()

        // Informe 1: Revisión Rutinaria
        listaInformes.add(Diagnostico(
            id = "2024/001",
            fechaDiagnostico = "15/03/2024",
            valoracion = "Paciente en buen estado general. Se realiza desparasitación interna y revisión de constantes. Corazón y pulmones normales.",
            importeTotal = 45.00,
            patologia = Patologia(nombre = "Chequeo Preventivo"),
            medicamento = Medicamento(nombreComercial = "Milbemax Comprimidos"),
            tratamiento = Tratamiento(tipoTratamiento = "Protocolo Desparasitación")
        ))

        // Informe 2: Infección de Oído
        listaInformes.add(Diagnostico(
            id = "2024/002",
            fechaDiagnostico = "22/03/2024",
            valoracion = "Se observa inflamación y secreción en el conducto auditivo derecho. El paciente muestra dolor a la palpación.",
            importeTotal = 62.50,
            patologia = Patologia(nombre = "Otitis Externa"),
            medicamento = Medicamento(nombreComercial = "Posatex Gotas"),
            tratamiento = Tratamiento(tipoTratamiento = "Limpieza y gotas diarias")
        ))

        // Informe 3: Alergia Alimentaria
        listaInformes.add(Diagnostico(
            id = "2024/003",
            fechaDiagnostico = "05/04/2024",
            valoracion = "Dermatitis por rascado en zona abdominal. Se sospecha de intolerancia a la proteína de pollo en el pienso actual.",
            importeTotal = 30.00,
            patologia = Patologia(nombre = "Alergia Cutánea"),
            medicamento = Medicamento(nombreComercial = "Apoquel 5.4mg"),
            tratamiento = Tratamiento(tipoTratamiento = "Cambio a dieta hidrolizada")
        ))

        // Notificamos al adaptador para refrescar el RecyclerView
        adapterInforme.notifyDataSetChanged()
    }*/

    override fun onResume() {
        super.onResume()
    }

    // NAVEGACION ENTRE FRAGMENTS
    fun navegacionFragment(num: Int, informe: Diagnostico? = null) {
        when (num) {
            1 -> NavigatorMascota.MascotaInforme_to_MascotaInformeInfo(this)
            2 -> {
                if (informe != null) {
                    NavigatorMascota.MascotaInforme_to_MascotaInformeInfo(this, informe)
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun cargarInformesMascota() {
        val idMascota = idMascotaSeleccionada

        if (idMascota.isNullOrEmpty()) {
            mostrarToast("No se ha podido obtener la mascota seleccionada")
            return
        }

        diagnosticoRepository.obtenerDiagnosticosPorMascota(
            idMascota = idMascota,
            success = { lista ->
                listaInformes.clear()

                val informesOrdenados = lista
                    .map { (idDiagnostico, diagnostico) ->
                        diagnostico.copy(id = idDiagnostico)
                    }
                    .sortedByDescending { it.fechaDiagnostico ?: "" }

                listaInformes.addAll(informesOrdenados)
                adapterInforme.notifyDataSetChanged()

                if (listaInformes.isEmpty()) {
                    mostrarToast("Esta mascota no tiene informes registrados")
                }
            },
            error = { mensaje ->
                mostrarToast(mensaje ?: "Error al cargar los informes")
            }
        )
    }

    override fun onInformeClick(informe: Diagnostico) {
        navegacionFragment(2, informe)
    }

    private fun mostrarToast(mensaje: String) {
        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}