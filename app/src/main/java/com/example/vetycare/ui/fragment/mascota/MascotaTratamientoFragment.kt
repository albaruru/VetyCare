package com.example.vetycare.ui.fragment.mascota

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vetycare.adapter.TratamientoAdapter
import com.example.vetycare.database.remote.MedicamentoRemote
import com.example.vetycare.database.remote.TratamientoRemote
import com.example.vetycare.database.repository.MedicamentoRepository
import com.example.vetycare.database.repository.TratamientoRepository
import com.example.vetycare.databinding.FragmentMascotaTratamientoBinding
import com.example.vetycare.model.entities.Mascota
import com.example.vetycare.model.entities.Tratamiento
import com.example.vetycare.model.relational.MedicamentoPorTratamiento
import com.example.vetycare.navigation.NavigatorMascota
import com.example.vetycare.ui.container.MascotaContainerFragment
import com.example.vetycare.utils.FirebaseUtils
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

/* EXPLICACIÓN DE LA CLASE <MascotaTratamientoFragment()> : despliega para leer...
    Fragmento encargado de gestionar y mostrar el listado de tratamientos de una mascota.
    Coordina la carga de datos relacionales entre tratamientos y medicamentos de Firebase,
    permitiendo al usuario visualizar las pautas médicas activas de sus animales.
 */
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

    /* EXPLICACIÓN DEL METODO <onAttach()> : despliega para leer...
        Inicializa las instancias de Firebase y los repositorios necesarios para la consulta.
        Prepara las capas de acceso a datos para tratamientos y medicamentos, garantizando
        que el fragmento pueda realizar peticiones al backend desde su vinculación.
    */
    override fun onAttach(context: Context) {
        super.onAttach(context)

        firebaseDatabase = FirebaseDatabase.getInstance(FirebaseUtils.URL_RTDB)
        databaseReference = firebaseDatabase.reference

        val remoteTratamiento = TratamientoRemote(databaseReference)
        tratamientoRepository = TratamientoRepository(remoteTratamiento)

        val remoteMedicamento = MedicamentoRemote(databaseReference)
        medicamentoRepository = MedicamentoRepository(remoteMedicamento)
    }

    /* EXPLICACIÓN DEL METODO <onCreateView()> : despliega para leer...
        Infla la jerarquía de vistas utilizando ViewBinding para establecer la interfaz del usuario.
        Genera el objeto binding que permite el acceso directo a los componentes visuales del
        listado de tratamientos médicos del animal.
    */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentMascotaTratamientoBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    /* EXPLICACIÓN DEL METODO <onViewCreated()> : despliega para leer...
        Configura el RecyclerView, obtiene los datos del contenedor y gestiona el botón de retroceso.
        Asegura que el listado se cargue con la información de la mascota seleccionada y que el
        retorno al perfil sea fluido mediante el dispatcher de la actividad.
    */
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

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navegacionFragment(3)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    /* EXPLICACIÓN DEL METODO <navegacionFragment()> : despliega para leer...
        Centraliza la lógica de navegación hacia el detalle del tratamiento o hacia el perfil.
        Utiliza el NavigatorMascota para redirigir al usuario según la acción realizada,
        asegurando una transición correcta entre las diferentes pantallas del módulo.
    */
    fun navegacionFragment(num: Int, tratamiento: Tratamiento? = null) {
        when (num) {
            1 -> NavigatorMascota.MascotaTratamiento_to_MascotaTratamientoInfo(this)
            2 -> if (tratamiento != null) {
                NavigatorMascota.MascotaTratamiento_to_MascotaTratamientoInfo(this, tratamiento)
            }
            3 -> NavigatorMascota.MascotaTratamiento_to_MascotaPerfil(this@MascotaTratamientoFragment)
        }
    }

    /* EXPLICACIÓN DEL METODO <cargarTratamientosMascota()> : despliega para leer...
        Solicita al repositorio la lista base de tratamientos asociados a la mascota activa.
        Tras recibir la respuesta de Firebase, inicia el proceso de enriquecimiento de datos
        para incluir la información detallada de medicación y frecuencia en el listado.
    */
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

    /* EXPLICACIÓN DEL METODO <completarTratamientosConFrecuencia()> : despliega para leer...
        Realiza consultas adicionales para obtener los detalles técnicos de cada tratamiento.
        Sincroniza múltiples peticiones asíncronas para construir objetos completos antes de
        ordenar la lista cronológicamente y notificar los cambios al adaptador del listado.
    */
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

    /* EXPLICACIÓN DEL METODO <mostrarToast()> : despliega para leer...
        Muestra mensajes informativos breves en la interfaz para notificar estados al usuario.
        Se utiliza para confirmar la ausencia de datos o alertar sobre errores de conexión
        durante la descarga de información desde los repositorios de Firebase.
    */
    private fun mostrarToast(mensaje: String) {
        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show()
    }

    /* EXPLICACIÓN DEL METODO <onTratamientoClick()> : despliega para leer...
        Captura el evento de selección de un tratamiento individual dentro del RecyclerView.
        Delega la acción al metodo de navegación para cargar el fragmento de información detallada
        pasando el objeto técnico completo como parámetro.
    */
    override fun onTratamientoClick(tratamiento: Tratamiento) {
        navegacionFragment(2, tratamiento)
    }

    /* EXPLICACIÓN DEL METODO <onDestroyView()> : despliega para leer...
        Anula la referencia al objeto binding cuando la vista del fragmento se destruye por el sistema.
        Esta operación es vital para prevenir fugas de memoria, asegurando que los recursos de la
        interfaz de usuario se liberen correctamente al finalizar el ciclo de vida.
    */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}