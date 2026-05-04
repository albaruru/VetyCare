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
import com.example.vetycare.adapter.InformeAdapter
import com.example.vetycare.database.remote.DiagnosticoRemote
import com.example.vetycare.database.repository.DiagnosticoRepository
import com.example.vetycare.databinding.FragmentMascotaInformeBinding
import com.example.vetycare.model.entities.Diagnostico
import com.example.vetycare.model.entities.Mascota
import com.example.vetycare.navigation.NavigatorMascota
import com.example.vetycare.ui.container.MascotaContainerFragment
import com.example.vetycare.utils.FirebaseUtils
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

/* EXPLICACIÓN DE LA CLASE <MascotaInformeFragment()> : despliega para leer...
    Fragmento encargado de listar los informes médicos y diagnósticos de una mascota.
    Gestiona la recuperación de datos desde Firebase, su ordenación cronológica y la
    visualización en un listado interactivo mediante un adaptador personalizado.
 */
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

    /* EXPLICACIÓN DEL METODO <onAttach()> : despliega para leer...
        Inicializa las referencias de Firebase y el repositorio de diagnósticos al vincular el fragmento.
        Establece la conexión con la base de datos en tiempo real para asegurar que el sistema
        pueda consultar los informes médicos en cuanto la vista esté lista.
    */
    override fun onAttach(context: Context) {
        super.onAttach(context)

        firebaseDatabase = FirebaseDatabase.getInstance(FirebaseUtils.URL_RTDB)
        databaseReference = firebaseDatabase.reference

        val remoteDiagnostico = DiagnosticoRemote(databaseReference)
        diagnosticoRepository = DiagnosticoRepository(remoteDiagnostico)
    }

    /* EXPLICACIÓN DEL METODO <onCreateView()> : despliega para leer...
        Infla la jerarquía de vistas del fragmento utilizando la clase de vinculación generada.
        Prepara el contenedor visual para el listado de informes y devuelve la vista raíz
        necesaria para el ciclo de vida del componente.
    */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentMascotaInformeBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    /* EXPLICACIÓN DEL METODO <onViewCreated()> : despliega para leer...
        Configura el RecyclerView, el adaptador y recupera los datos de la mascota del contenedor padre.
        Implementa también el control del botón físico de retroceso para asegurar que el usuario
        regrese correctamente al perfil de la mascota al intentar salir.
    */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val containerFragment = parentFragment?.parentFragment as? MascotaContainerFragment
        mascotaSeleccionada = containerFragment?.obtenerMascotaSeleccionada()
        idMascotaSeleccionada = containerFragment?.obtenerIdMascotaSeleccionada()

        adapterInforme = InformeAdapter(listaInformes, requireContext(), this)
        binding.rvInformes.layoutManager = LinearLayoutManager(context)
        binding.rvInformes.adapter = adapterInforme

        cargarInformesMascota()

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navegacionFragment(3)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    /* EXPLICACIÓN DEL METODO <navegacionFragment()> : despliega para leer...
        Centraliza el flujo de navegación hacia el detalle del informe o de vuelta al perfil.
        Permite transferir el objeto diagnóstico seleccionado hacia la pantalla de información
        detallada o invocar al NavigatorMascota para retroceder en la aplicación.
    */
    fun navegacionFragment(num: Int, informe: Diagnostico? = null) {
        when (num) {
            1 -> NavigatorMascota.MascotaInforme_to_MascotaInformeInfo(this)
            2 -> {
                if (informe != null) {
                    NavigatorMascota.MascotaInforme_to_MascotaInformeInfo(this, informe)
                }
            }
            3 -> NavigatorMascota.MascotaInforme_to_MascotaPerfil(this@MascotaInformeFragment)
        }
    }

    /* EXPLICACIÓN DEL METODO <cargarInformesMascota()> : despliega para leer...
        Consulta al repositorio los diagnósticos asociados al identificador de la mascota activa.
        Procesa la lista recibida para ordenarla por fecha descendente y notifica al adaptador
        para que refresque la interfaz visual con los nuevos datos cargados.
    */
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

    /* EXPLICACIÓN DEL METODO <onInformeClick()> : despliega para leer...
        Captura el evento de selección de un informe individual dentro del listado del RecyclerView.
        Delega la acción al metodo de navegación para abrir la pantalla con los detalles
        técnicos del diagnóstico médico seleccionado por el usuario.
    */
    override fun onInformeClick(informe: Diagnostico) {
        navegacionFragment(2, informe)
    }

    /* EXPLICACIÓN DEL METODO <mostrarToast()> : despliega para leer...
        Muestra un mensaje emergente breve en la parte inferior de la pantalla para informar al usuario.
        Se utiliza principalmente para notificar errores de carga o confirmar la ausencia de
        registros médicos para la mascota actual.
    */
    private fun mostrarToast(mensaje: String) {
        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show()
    }

    /* EXPLICACIÓN DEL METODO <onDestroyView()> : despliega para leer...
        Anula la referencia al objeto binding cuando la vista del fragmento se destruye.
        Esta operación es fundamental para prevenir fugas de memoria, asegurando que
        los recursos de la interfaz se liberen correctamente al finalizar el ciclo de vida.
    */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}