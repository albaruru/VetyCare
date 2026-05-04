package com.example.vetycare.ui.fragment.usuario

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vetycare.R
import com.example.vetycare.adapter.CitaAdapter
import com.example.vetycare.databinding.FragmentUsuarioCalendarioBinding
import com.example.vetycare.model.entities.Cita
import com.example.vetycare.model.enums.TipoCita
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.ViewContainer
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.example.vetycare.database.remote.CitaRemote
import com.example.vetycare.database.remote.PropietarioRemote
import com.example.vetycare.database.repository.CitaRepository
import com.example.vetycare.database.repository.PropietarioRepository
import com.example.vetycare.navigation.NavigatorUsuario
import com.example.vetycare.ui.dialog.ConfirmacionDialog
import com.example.vetycare.utils.FirebaseUtils
import com.example.vetycare.utils.mostrarSnackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.format.TextStyle

/* EXPLICACIÓN DE LA CLASE <UsuarioCalendarioFragment()> : despliega para leer...
    Fragmento que integra un calendario interactivo para la gestión de citas del usuario.
    Permite visualizar eventos mediante indicadores de colores, filtrar consultas por día
    y gestionar la cancelación de citas programadas mediante comunicación con Firebase.
 */
class UsuarioCalendarioFragment : Fragment(), CitaAdapter.OnCitaListener {

    private lateinit var binding: FragmentUsuarioCalendarioBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var propietarioRepository: PropietarioRepository
    private lateinit var citaRepository: CitaRepository
    private lateinit var citaAdapter: CitaAdapter
    private lateinit var listaMaestraCitas: ArrayList<Cita>
    private lateinit var listaCitasFiltradas: ArrayList<Cita>
    private var fechaSeleccionada: LocalDate = LocalDate.now()
    private val formateador = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    private val keyConfirmarCancelacion = "confirmacion_cancelar_cita"
    private var idCitaSeleccionadaParaCancelar: String? = null

    /* EXPLICACIÓN DEL METODO <onAttach()> : despliega para leer...
        Inicializa las instancias de Firebase Auth y Realtime Database al vincular el fragmento.
        Establece las referencias base necesarias para que los repositorios puedan realizar
        consultas de datos de usuario y citas médicas desde el inicio del ciclo de vida.
    */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        auth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance(FirebaseUtils.URL_RTDB)
        databaseReference = firebaseDatabase.reference
    }

    /* EXPLICACIÓN DEL METODO <onCreateView()> : despliega para leer...
        Infla el diseño XML del calendario utilizando ViewBinding para la gestión de la interfaz.
        Genera el objeto binding que permite el acceso a la vista del calendario y los
        listados de citas, devolviendo la vista raíz para su renderizado.
    */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUsuarioCalendarioBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    /* EXPLICACIÓN DEL METODO <onCreate()> : despliega para leer...
        Configura el listener para capturar la respuesta del diálogo de confirmación de cancelación.
        Si el usuario valida la acción, recupera el identificador de la cita almacenado
        y dispara la lógica de ejecución para dar de baja la cita en la base de datos.
    */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        parentFragmentManager.setFragmentResultListener(keyConfirmarCancelacion, this) { _, bundle ->
            val confirmado = bundle.getBoolean(ConfirmacionDialog.KEY_CONFIRMADO)
            if (confirmado) {
                idCitaSeleccionadaParaCancelar?.let { id ->
                    ejecutarCancelacionCita(id)
                }
            }
        }
    }

    /* EXPLICACIÓN DEL METODO <onViewCreated()> : despliega para leer...
        Inicializa los repositorios, la configuración del RecyclerView y la lógica del calendario.
        Lanza la carga inicial de citas del propietario y define el comportamiento del
        botón físico de retroceso para asegurar un retorno controlado al inicio de usuario.
    */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val remotePropietario = PropietarioRemote(databaseReference)
        propietarioRepository = PropietarioRepository(remotePropietario)

        val remoteCita = CitaRemote(databaseReference)
        citaRepository = CitaRepository(remoteCita)

        instancias()
        configurarRecyclerView()
        configurarCalendario()

        binding.tvCitasDia.visibility = View.GONE
        binding.rvCitasDia.visibility = View.GONE

        cargarCitasDelPropietario()

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navegacionFragment(1)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    /* EXPLICACIÓN DEL METODO <navegacionFragment()> : despliega para leer...
        Gestiona el flujo de navegación para retornar a la pantalla principal del usuario.
        Utiliza el NavigatorUsuario para realizar la transición desde el calendario hacia
        el fragmento de inicio, manteniendo la coherencia en la pila de retroceso.
    */
    fun navegacionFragment(num : Int) {
        when (num) {
            1 -> NavigatorUsuario.UsuarioCalendario_to_UsuarioInicio(this@UsuarioCalendarioFragment)
        }
    }

    /* EXPLICACIÓN DEL METODO <instancias()> : despliega para leer...
        Prepara las colecciones de datos y el adaptador personalizado para el listado de citas.
        Inicializa la lista maestra y la lista filtrada, vinculándolas con el CitaAdapter
        para gestionar la visualización dinámica según la fecha seleccionada.
    */
    private fun instancias() {
        listaMaestraCitas = ArrayList()
        listaCitasFiltradas = ArrayList()
        citaAdapter = CitaAdapter(listaCitasFiltradas, requireContext(), this)
    }

    /* EXPLICACIÓN DE LA CLASE <DiaViewContainer()> : despliega para leer...
        Contenedor de vistas para los días individuales del calendario (ViewContainer).
        Mantiene las referencias al número del día y al layout de indicadores, configurando
        el evento de clic para filtrar el listado de citas al seleccionar una fecha válida.
     */
    inner class DiaViewContainer(view: View) : ViewContainer(view) {
        val tvDia: TextView = view.findViewById(R.id.tvDia)
        val layoutIndicadores: LinearLayout = view.findViewById(R.id.layoutIndicadores)
        lateinit var day: CalendarDay

        init {
            view.setOnClickListener {
                if (day.position == DayPosition.MonthDate) {
                    filtrarCitasPorFecha(day.date)
                }
            }
        }
    }

    /* EXPLICACIÓN DEL METODO <configurarRecyclerView()> : despliega para leer...
        Establece la configuración técnica del listado de citas debajo del calendario.
        Define un LinearLayoutManager vertical y asigna el adaptador de citas, preparando
        el componente para mostrar las consultas médicas de forma organizada.
    */
    private fun configurarRecyclerView() {
        binding.rvCitasDia.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCitasDia.adapter = citaAdapter
    }

    /* EXPLICACIÓN DEL METODO <configurarCalendario()> : despliega para leer...
        Inicializa la biblioteca CalendarView con un rango de meses y configuración en español.
        Define el MonthDayBinder para personalizar el aspecto de cada día y configura los
        listeners de scroll y botones de navegación para cambiar entre meses.
    */
    private fun configurarCalendario() {
        val mesActual = YearMonth.now()

        binding.calendarView.setup(
            mesActual.minusMonths(1),
            mesActual.plusMonths(12),
            DayOfWeek.MONDAY
        )

        binding.calendarView.scrollToMonth(mesActual)
        actualizarTituloMes(mesActual)

        binding.calendarView.dayBinder = object : MonthDayBinder<DiaViewContainer> {
            override fun create(view: View): DiaViewContainer = DiaViewContainer(view)

            override fun bind(container: DiaViewContainer, data: CalendarDay) {
                container.day = data
                container.tvDia.text = data.date.dayOfMonth.toString()
                container.tvDia.alpha = if (data.position == DayPosition.MonthDate) 1f else 0.3f
                container.layoutIndicadores.removeAllViews()

                if (data.position == DayPosition.MonthDate) {
                    pintarIndicadores(container, data.date)
                }
            }
        }

        binding.calendarView.monthScrollListener = { mes ->
            actualizarTituloMes(mes.yearMonth)
        }

        binding.btnMesAnterior.setOnClickListener {
            binding.calendarView.findFirstVisibleMonth()?.let { mesVisible ->
                binding.calendarView.smoothScrollToMonth(mesVisible.yearMonth.minusMonths(1))
            }
        }

        binding.btnMesSiguiente.setOnClickListener {
            binding.calendarView.findFirstVisibleMonth()?.let { mesVisible ->
                binding.calendarView.smoothScrollToMonth(mesVisible.yearMonth.plusMonths(1))
            }
        }
    }

    /* EXPLICACIÓN DEL METODO <actualizarTituloMes()> : despliega para leer...
        Actualiza dinámicamente el texto del encabezado con el nombre del mes y el año.
        Formatea la cadena utilizando la localización en español y capitaliza la primera
        letra para ofrecer una presentación visual profesional y legible.
    */
    private fun actualizarTituloMes(yearMonth: YearMonth) {
        val nombre = yearMonth.month
            .getDisplayName(TextStyle.FULL, Locale("es"))
            .replaceFirstChar { it.uppercase() }

        binding.tvMesAnio.text = "$nombre ${yearMonth.year}"
    }

    /* EXPLICACIÓN DEL METODO <cargarCitasDelPropietario()> : despliega para leer...
        Recupera el historial de citas activas del usuario desde el repositorio de Firebase.
        Filtra las citas canceladas, rellena la lista maestra y solicita al calendario un
        refresco visual para pintar los indicadores de eventos en los días correspondientes.
    */
    private fun cargarCitasDelPropietario() {
        val authUid = auth.currentUser?.uid

        if (authUid.isNullOrEmpty()) {
            mostrarSnackbar("No se ha podido encontrar su identificador")
            return
        }

        propietarioRepository.obtenerPropietario(
            authUid,
            { idPropietario, _ ->
                citaRepository.obtenerCitasPorPropietario(
                    idPropietario = idPropietario,
                    onSuccess = { listaResultado ->
                        listaMaestraCitas.clear()

                        val citasActivas = listaResultado
                            .map { it.second }
                            .filter { it.estadoCita != "cancelada" }

                        listaMaestraCitas.addAll(citasActivas)

                        binding.calendarView.notifyCalendarChanged()

                        fechaSeleccionada = LocalDate.now()
                        filtrarCitasPorFecha(fechaSeleccionada)
                    },
                    onError = { mensajeError ->
                        mostrarSnackbar(mensajeError ?: "ERROR al cargar citas")
                    }
                )
            },
            { mensajeError ->
                mostrarSnackbar(mensajeError ?: "ERROR al obtener propietario")
            }
        )
    }

    /* EXPLICACIÓN DEL METODO <pintarIndicadores()> : despliega para leer...
        Genera puntos de colores dinámicos en cada celda del calendario para representar citas.
        Determina el color basándose en el tipo de consulta (vacuna, revisión, etc.) y añade
        las vistas al contenedor del día para facilitar la identificación visual rápida.
    */
    private fun pintarIndicadores(container: DiaViewContainer, fecha: LocalDate) {
        container.layoutIndicadores.removeAllViews()

        val citasDelDia = listaMaestraCitas.filter { cita ->
            obtenerFechaLocalDeCita(cita) == fecha
        }

        citasDelDia.forEach { cita ->
            val punto = View(requireContext())
            val dimension = (8 * resources.displayMetrics.density).toInt()
            val params = LinearLayout.LayoutParams(dimension, dimension).apply {
                setMargins(2, 0, 2, 0)
            }
            punto.layoutParams = params
            punto.background = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.background_indicador_cita
            )

            val color = when (cita.tipoCita?.uppercase()) {
                "REVISIÓN", "REVISION" -> TipoCita.REVISION.colorRes
                "VACUNACIÓN", "VACUNACION" -> TipoCita.VACUNACION.colorRes
                "CONSULTA GENERAL", "CONSULTA" -> TipoCita.CONSULTA.colorRes
                "PRUEBAS" -> TipoCita.PRUEBAS.colorRes
                "COMPRAR MÁS MEDICAMENTOS", "MEDICAMENTOS" -> TipoCita.MEDICAMENTOS.colorRes
                else -> R.color.botones
            }

            punto.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), color)

            container.layoutIndicadores.addView(punto)
        }
    }

    /* EXPLICACIÓN DEL METODO <filtrarCitasPorFecha()> : despliega para leer...
        Filtra la colección maestra de citas para extraer solo aquellas que coinciden con el día elegido.
        Ordena los resultados cronológicamente, actualiza la lista del adaptador y gestiona la
        visibilidad de los componentes informativos según la existencia de citas.
    */
    private fun filtrarCitasPorFecha(fecha: LocalDate) {
        fechaSeleccionada = fecha

        val filtradas = listaMaestraCitas.filter { cita ->
            obtenerFechaLocalDeCita(cita) == fecha
        }.sortedBy { it.fechaHoraInicio ?: "" }

        citaAdapter.actualizarLista(filtradas)

        val fechaStr = fecha.format(formateador)
        binding.tvCitasDia.text = "Citas para el $fechaStr"
        binding.tvCitasDia.visibility = if (filtradas.isEmpty()) View.GONE else View.VISIBLE
        binding.rvCitasDia.visibility = if (filtradas.isEmpty()) View.GONE else View.VISIBLE
    }

    /* EXPLICACIÓN DEL METODO <obtenerFechaLocalDeCita()> : despliega para leer...
        Realiza el parseo de la cadena ISO de fecha proveniente de Firebase a un objeto LocalDate.
        Implementa una lógica de captura de excepciones para gestionar diferentes formatos de
        entrada, asegurando la compatibilidad con el sistema de filtrado del calendario.
    */
    private fun obtenerFechaLocalDeCita(cita: Cita): LocalDate? {
        val fechaHoraInicio = cita.fechaHoraInicio ?: return null

        return try {
            LocalDateTime.parse(fechaHoraInicio).toLocalDate()
        } catch (_: Exception) {
            try {
                LocalDate.parse(fechaHoraInicio.take(10))
            } catch (_: Exception) {
                null
            }
        }
    }

    /* EXPLICACIÓN DEL METODO <onCancelarClick()> : despliega para leer...
        Captura el evento de cancelación desde el adaptador y lanza el diálogo de confirmación.
        Almacena temporalmente el ID de la cita seleccionada para procesarlo una vez que
        el usuario valide la operación en la ventana emergente de seguridad.
    */
    override fun onCancelarClick(idCita: String) {
        idCitaSeleccionadaParaCancelar = idCita

        ConfirmacionDialog.nuevoDialog(
            "CANCELAR CITA",
            "¿Estás seguro de que deseas cancelar esta cita?\nEsta acción no se puede deshacer.",
            keyConfirmarCancelacion
        ).show(parentFragmentManager, "ConfirmacionDialog")
    }

    /* EXPLICACIÓN DEL METODO <ejecutarCancelacionCita()> : despliega para leer...
        Invoca al repositorio para actualizar el estado de la cita a "cancelada" en el servidor.
        Tras una respuesta exitosa, notifica al usuario mediante un Snackbar y vuelve a
        cargar las citas para refrescar tanto el calendario como el listado diario.
    */
    private fun ejecutarCancelacionCita(idCita: String) {
        citaRepository.cancelarCita(
            idCita = idCita,
            onSuccess = {
                if (isAdded) {
                    mostrarSnackbar("La cita ha sido cancelada correctamente.")
                    idCitaSeleccionadaParaCancelar = null
                    cargarCitasDelPropietario()
                }
            },
            onError = { mensaje ->
                if (isAdded) {
                    mostrarSnackbar(mensaje ?: "Error al intentar cancelar la cita")
                }
            }
        )
    }
}