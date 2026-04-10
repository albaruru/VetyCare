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
import com.example.vetycare.adapter.TratamientoAdapter
import com.example.vetycare.database.remote.MedicamentoRemote
import com.example.vetycare.database.remote.TratamientoRemote
import com.example.vetycare.database.repository.MedicamentoRepository
import com.example.vetycare.database.repository.TratamientoRepository
import com.example.vetycare.databinding.FragmentMascotaTratamientoBinding
import com.example.vetycare.model.entities.Mascota
import com.example.vetycare.model.entities.Medicamento
import com.example.vetycare.model.entities.Tratamiento
import com.example.vetycare.model.relational.MedicamentoPorTratamiento
import com.example.vetycare.navigation.NavigatorMascota
import com.example.vetycare.ui.container.MascotaContainerFragment
import com.example.vetycare.utils.FirebaseUtils
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MascotaTratamientoFragment : Fragment(), TratamientoAdapter.OnTratamientoListener {
    private var _binding : FragmentMascotaTratamientoBinding ?= null
    private val binding get() = _binding!!
    private lateinit var adapterTratamiento: TratamientoAdapter
    private var listaTratamientos = ArrayList<Tratamiento>()
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var tratamientoRepository: TratamientoRepository
    private lateinit var medicamentoRepository: MedicamentoRepository
    private var mascotaSeleccionada: Mascota? = null
    private var idMascotaSeleccionada: String? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)

        firebaseDatabase = FirebaseDatabase.getInstance(FirebaseUtils.URL_RTDB)
        databaseReference = firebaseDatabase.reference

        val remoteTratamiento = TratamientoRemote(databaseReference)
        tratamientoRepository = TratamientoRepository(remoteTratamiento)

        val remoteMedicamento = MedicamentoRemote(databaseReference)
        medicamentoRepository = MedicamentoRepository(remoteMedicamento)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentMascotaTratamientoBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val container = parentFragment?.parentFragment as? MascotaContainerFragment
        mascotaSeleccionada = container?.obtenerMascotaSeleccionada()
        idMascotaSeleccionada = container?.obtenerIdMascotaSeleccionada()

        // Configuración del Recycler
        adapterTratamiento = TratamientoAdapter(listaTratamientos, requireContext(), this)
        binding.rvTratamientos.layoutManager = LinearLayoutManager(context)
        binding.rvTratamientos.adapter = adapterTratamiento

        cargarTratamientosMascota()
        //FIXME: BORRAR => crearTratamientosDePrueba()
    }

    /* FIXME: BORRAR
    private fun crearTratamientosDePrueba() {
        listaTratamientos.clear()
        listaTratamientos.add(Tratamiento(
            id = "TR-001",
            tipoTratamiento = "Antibiótico",
            medicamento = Medicamento(nombreComercial = "Amoxicilina 250mg"),
            detallesMedicacion = MedicamentoPorTratamiento(
                dosis = "1 pastilla",
                frecuencia = "Cada 12 horas",
                viaAdministracion = "Oral"
            ),
            fechaInicio = "01/04/2026",
            fechaFin = "10/04/2026",
            observaciones = "Dar con comida."
        ))
        listaTratamientos.add(Tratamiento(
            id = "TR-002",
            tipoTratamiento = "Desparasitación",
            medicamento = Medicamento(nombreComercial = "Milbemax 200mg"),
            detallesMedicacion = MedicamentoPorTratamiento(
                dosis = "5ml",
                frecuencia = "Cada 24 horas",
                viaAdministracion = "Oral"
            ),
            fechaInicio = "05/04/2026",
            fechaFin = "05/04/2026",
            observaciones = "Repetir en 3 meses."
        ))
        listaTratamientos.add(Tratamiento(
            id = "TR-003",
            tipoTratamiento = "Oftálmico",
            medicamento = Medicamento(nombreComercial = "Amoxicilina 250mg"),
            detallesMedicacion = MedicamentoPorTratamiento(
                dosis = "15ml",
                frecuencia = "Cada 8 horas",
                viaAdministracion = "Oral"
            ),
            fechaInicio = "07/04/2026",
            fechaFin = "14/04/2026",
            observaciones = "Limpiar ojo antes."
        ))
        adapterTratamiento.notifyDataSetChanged()
    }
    */

    override fun onResume() {
        super.onResume()
    }

    // NAVEGACION ENTRE FRAGMENTS
    fun navegacionFragment(num: Int, tratamiento: Tratamiento? = null) {
        when (num) {
            1 -> NavigatorMascota.MascotaTratamiento_to_MascotaTratamientoInfo(this)
            2 -> if (tratamiento != null) {
                NavigatorMascota.MascotaTratamiento_to_MascotaTratamientoInfo(this, tratamiento)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun cargarTratamientosMascota() {
        val idMascota = idMascotaSeleccionada

        if (idMascota.isNullOrEmpty()) {
            mostrarToast("No se ha podido obtener la mascota seleccionada")
            return
        }

        tratamientoRepository.obtenerTratamientosPorMascota(
            idMascota = idMascota,
            { lista ->
                if (lista.isEmpty()) {
                    listaTratamientos.clear()
                    adapterTratamiento.notifyDataSetChanged()
                    mostrarToast("Esta mascota no tiene tratamientos registrados")
                    return@obtenerTratamientosPorMascota
                }

                completarTratamientosConFrecuencia(lista)
            },
            { mensajeDeError ->
                mostrarToast(mensajeDeError ?: "Error al cargar los tratamientos")
            }
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun completarTratamientosConFrecuencia(listaBase: List<Pair<String, Tratamiento>>) {
        val listaFinal = mutableListOf<Tratamiento>()
        var restantes = listaBase.size
        var hayError = false

        listaBase.forEach { (idTratamiento, tratamientoBase) ->

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
                            fechaInicio = nodoMedicacion.child("fechaInicio").getValue(String::class.java),
                            frecuencia = nodoMedicacion.child("frecuencia").getValue(String::class.java),
                            indicaciones = nodoMedicacion.child("indicaciones").getValue(String::class.java),
                            viaAdministracion = nodoMedicacion.child("viaAdministracion").getValue(String::class.java)
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

                                listaFinal.add(tratamientoCompleto)

                                restantes--
                                if (restantes == 0 && !hayError) {
                                    listaTratamientos.clear()
                                    listaTratamientos.addAll(
                                        listaFinal.sortedByDescending { it.fechaInicio ?: "" }
                                    )
                                    adapterTratamiento.notifyDataSetChanged()
                                }
                            },
                            error = { mensaje ->
                                if (!hayError) {
                                    hayError = true
                                    mostrarToast(mensaje ?: "Error al cargar el medicamento")
                                }
                            }
                        )
                    } else {
                        val tratamientoCompleto = tratamientoBase.copy(
                            id = idTratamiento,
                            detallesMedicacion = detalles
                        )

                        listaFinal.add(tratamientoCompleto)

                        restantes--
                        if (restantes == 0 && !hayError) {
                            listaTratamientos.clear()
                            listaTratamientos.addAll(
                                listaFinal.sortedByDescending { it.fechaInicio ?: "" }
                            )
                            adapterTratamiento.notifyDataSetChanged()
                        }
                    }
                },
                error = { mensaje ->
                    if (!hayError) {
                        hayError = true
                        mostrarToast(mensaje ?: "Error al cargar la medicación del tratamiento")
                    }
                }
            )
        }
    }

    private fun mostrarToast(mensaje: String) {
        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show()
    }

    override fun onTratamientoClick(tratamiento: Tratamiento) {
        navegacionFragment(2, tratamiento)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}