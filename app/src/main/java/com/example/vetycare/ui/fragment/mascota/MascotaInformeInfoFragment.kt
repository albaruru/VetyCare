package com.example.vetycare.ui.fragment.mascota

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.vetycare.database.remote.MedicamentoRemote
import com.example.vetycare.database.remote.TratamientoRemote
import com.example.vetycare.database.repository.MedicamentoRepository
import com.example.vetycare.database.repository.TratamientoRepository
import com.example.vetycare.databinding.FragmentMascotaInformeInfoBinding
import com.example.vetycare.model.entities.Diagnostico
import com.example.vetycare.model.entities.Tratamiento
import com.example.vetycare.model.relational.MedicamentoPorTratamiento
import com.example.vetycare.navigation.NavigatorInicio
import com.example.vetycare.navigation.NavigatorMascota
import com.example.vetycare.utils.FirebaseUtils
import com.example.vetycare.utils.mostrarSnackbar
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MascotaInformeInfoFragment : Fragment() {
    /* FIXME: PRUEBA DE IR DIRECTAMENTE DE INFORMES A TRATAMIENTOS
    private lateinit var binding : FragmentMascotaInformeInfoBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMascotaInformeInfoBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        /* Acciones de los botones del fragment:
        - Botón Volver => Navega al MascotaInformeFragment
        */
        binding.btnVolver.setOnClickListener {
            navegacionFragment(1)
        }
    }

    // NAVEGACION ENTRE FRAGMENTS
    fun navegacionFragment(num: Int) {
        when (num) {
            1 -> NavigatorMascota.MascotaInformeInfo_to_MascotaInforme(this)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Recuperamos el informe del Bundle de forma segura
        val informe = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable("informe_key", Diagnostico::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getSerializable("informe_key") as? Diagnostico
        }

        //Si el informe existe, pintamos los datos en la pantalla
        if (informe != null) {
            pintarDatosInforme(informe)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun pintarDatosInforme(i: Diagnostico) {
        // Mapeamos los campos del objeto Diagnostico a los IDs del XML
        binding.tvTitulo.text = "Informe #${i.id?.uppercase()}"
        binding.tvConcepto.text = i.patologia?.nombre ?: "Sin concepto"
        binding.tvFecha.text = i.fechaDiagnostico
        binding.tvValoracion.text = i.valoracion // El informe redactado

        // Usamos los nombres de campos que definimos en los informes de prueba
        binding.tvMedicamento.text = i.medicamento?.nombreComercial ?: "N/A"
        binding.tvTratamiento.text = i.tratamiento?.tipoTratamiento ?: "N/A"

        binding.tvImporte.text = "${i.importeTotal} €"
    }*/
    private var _binding: FragmentMascotaInformeInfoBinding? = null
    private val binding get() = _binding!!

    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var tratamientoRepository: TratamientoRepository
    private lateinit var medicamentoRepository: MedicamentoRepository

    private var informeActual: Diagnostico? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        firebaseDatabase = FirebaseDatabase.getInstance(FirebaseUtils.URL_RTDB)
        databaseReference = firebaseDatabase.reference

        val remoteTratamiento = TratamientoRemote(databaseReference)
        tratamientoRepository = TratamientoRepository(remoteTratamiento)

        val remoteMedicamento = MedicamentoRemote(databaseReference)
        medicamentoRepository = MedicamentoRepository(remoteMedicamento)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMascotaInformeInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        informeActual = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable("informe_key", Diagnostico::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getSerializable("informe_key") as? Diagnostico
        }

        informeActual?.let { pintarDatosInforme(it) }
    }

    override fun onResume() {
        super.onResume()

        binding.btnVolver.setOnClickListener {
            NavigatorMascota.MascotaInformeInfo_to_MascotaInforme(this)
        }

        binding.btnVerTratamiento.setOnClickListener {
            abrirTratamientoDelInforme()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun pintarDatosInforme(i: Diagnostico) {
        binding.tvTitulo.text = "Informe #${i.id?.uppercase()}"
        binding.tvConcepto.text = i.patologia?.nombre ?: "Sin concepto"
        binding.tvFecha.text = i.fechaDiagnostico ?: "N/A"
        binding.tvValoracion.text = i.valoracion ?: "N/A"
        binding.tvMedicamento.text = i.medicamento?.nombreComercial ?: "N/A"
        binding.tvTratamiento.text = i.tratamiento?.tipoTratamiento ?: "N/A"
        binding.tvImporte.text = "${i.importeTotal ?: 0.0} €"
    }

    private fun abrirTratamientoDelInforme() {
        val informe = informeActual
        val idTratamiento = informe?.idTratamiento

        if (idTratamiento.isNullOrEmpty()) {
            mostrarToast("Este informe no tiene tratamiento asociado")
            return
        }

        tratamientoRepository.obtenerTratamientoPorId(
            idTratamiento = idTratamiento,
            success = { tratamientoBase ->
                cargarTratamientoCompleto(idTratamiento, tratamientoBase)
            },
            error = { mensaje ->
                mostrarToast(mensaje ?: "Error al cargar el tratamiento")
            }
        )
    }

    private fun cargarTratamientoCompleto(
        idTratamiento: String,
        tratamientoBase: Tratamiento
    ) {
        tratamientoRepository.obtenerMedicamentosPorTratamiento(
            idTratamiento = idTratamiento,
            success = { snapshot ->
                val nodoMedicacion = snapshot.children.firstOrNull()
                val idMedicamento = nodoMedicacion?.key

                val detalles = if (nodoMedicacion != null) {
                    MedicamentoPorTratamiento(
                        dosis = nodoMedicacion.child("dosis").getValue(String::class.java),
                        duracion = nodoMedicacion.child("duracion").getValue(String::class.java),
                        fechaFin = nodoMedicacion.child("fechaFin").getValue(String::class.java),
                        fechaInicio = nodoMedicacion.child("fechaInicio")
                            .getValue(String::class.java),
                        frecuencia = nodoMedicacion.child("frecuencia")
                            .getValue(String::class.java),
                        indicaciones = nodoMedicacion.child("indicaciones")
                            .getValue(String::class.java),
                        viaAdministracion = nodoMedicacion.child("viaAdministracion")
                            .getValue(String::class.java)
                    )
                } else {
                    MedicamentoPorTratamiento()
                }

                if (!idMedicamento.isNullOrEmpty()) {
                    medicamentoRepository.obtenerMedicamentoPorId(
                        idMedicamento = idMedicamento,
                        success = { medicamento ->
                            val tratamientoCompleto = tratamientoBase.copy(
                                id = idTratamiento,
                                medicamento = medicamento,
                                detallesMedicacion = detalles
                            )

                            NavigatorMascota.MascotaInformeInfo_to_MascotaTratamientoInfo(
                                this,
                                tratamientoCompleto
                            )
                        },
                        error = { mensaje ->
                            mostrarToast(mensaje ?: "Error al cargar el medicamento")
                        }
                    )
                } else {
                    val tratamientoCompleto = tratamientoBase.copy(
                        id = idTratamiento,
                        detallesMedicacion = detalles
                    )

                    NavigatorMascota.MascotaInformeInfo_to_MascotaTratamientoInfo(
                        this,
                        tratamientoCompleto
                    )
                }
            },
            error = { mensaje ->
                mostrarToast(mensaje ?: "Error al cargar la medicación del tratamiento")
            }
        )
    }

    private fun mostrarToast(mensaje: String) {
        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}