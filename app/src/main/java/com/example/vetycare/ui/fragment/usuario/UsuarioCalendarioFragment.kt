package com.example.vetycare.ui.fragment.usuario

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
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
import com.example.vetycare.utils.FirebaseUtils
import com.example.vetycare.utils.mostrarSnackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.format.TextStyle

/*FIXME: BORRAR
class UsuarioCalendarioFragment : Fragment() {
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        auth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance(FirebaseUtils.URL_RTDB)
        databaseReference = firebaseDatabase.reference
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentUsuarioCalendarioBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val remotePropietario = PropietarioRemote(databaseReference)
        propietarioRepository = PropietarioRepository(remotePropietario)

        val remoteCita = CitaRemote(databaseReference)
        citaRepository = CitaRepository(remoteCita)

        instancias()
        configurarCalendario()
        configurarRecyclerView()

        binding.tvCitasDia.visibility = View.GONE
        binding.rvCitasDia.visibility = View.GONE

        cargarCitasDelPropietario()
    }

    override fun onResume() {
        super.onResume()

    }

    inner class DiaViewContainer(view: View) : ViewContainer(view) {
        val tvDia: TextView = view.findViewById(R.id.tvDia)
        val layoutIndicadores: LinearLayout = view.findViewById(R.id.layoutIndicadores)
        lateinit var day: CalendarDay

        init{
            view.setOnClickListener {
                if(day.position == DayPosition.MonthDate) {
                    filtrarCitasPorFecha(day.date)
                }
            }
        }
    }


    private fun instancias() {
        listaMaestraCitas = ArrayList()
        listaCitasFiltradas = ArrayList()
        citaAdapter = CitaAdapter(listaCitasFiltradas, requireContext())
    }

    private fun configurarRecyclerView(){
        citaAdapter = CitaAdapter(listaCitasFiltradas, requireContext())
        binding.rvCitasDia.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCitasDia.adapter = citaAdapter
    }

    private fun cargarCitasDelPropietario() {
        val authUid = auth.currentUser?.uid

        if (authUid.isNullOrEmpty()) {
            mostrarSnackbar("No se pudo identificar al usuario.")
            return
        }

        propietarioRepository.obtenerPropietario(
            authUid = authUid,
            Success = { idPropietario, _ ->
                citaRepository.obtenerCitasPorPropietario(
                    idPropietario = idPropietario,
                    onSuccess = { lista ->
                        listaMaestraCitas.clear()
                        listaMaestraCitas.addAll(lista.map { it.second })

                        binding.calendarView.notifyCalendarChanged()

                        fechaSeleccionada = LocalDate.now()
                        filtrarCitasPorFecha(fechaSeleccionada)
                    },
                    onError = { error ->
                        mostrarSnackbar(error ?: "No se pudieron cargar las citas.")
                    }
                )
            },
            Error = { error ->
                mostrarSnackbar(error ?: "No se pudo obtener el propietario.")
            }
        )
    }

    private fun configurarCalendario() {
        val mesActual = YearMonth.now()

        binding.calendarView.setup(
            mesActual.minusMonths(1),
            mesActual.plusMonths(12),
            java.time.DayOfWeek.MONDAY
        )
        binding.calendarView.scrollToMonth(mesActual)

        binding.calendarView.dayBinder = object : MonthDayBinder<DiaViewContainer> {
            override fun create(view: View) = DiaViewContainer(view)
            override fun bind(container: DiaViewContainer, data: CalendarDay) {
                container.day = data
                container.tvDia.text = data.date.dayOfMonth.toString()
                container.tvDia.alpha = if (data.position == DayPosition.MonthDate) 1f else 0.3f
                container.layoutIndicadores.removeAllViews()

                // Pintamos los puntos de colores
                pintarIndicadores(container, data.date)
            }
        }

        binding.calendarView.monthScrollListener = { mes ->
            val nombre = mes.yearMonth.month
                .getDisplayName(java.time.format.TextStyle.FULL, Locale("es"))
                .replaceFirstChar { it.uppercase() }
            binding.tvMesAnio.text = "$nombre ${mes.yearMonth.year}"
        }

        binding.btnMesAnterior.setOnClickListener {
            binding.calendarView.findFirstVisibleMonth()?.let {
                binding.calendarView.smoothScrollToMonth(it.yearMonth.minusMonths(1))
            }
        }
        binding.btnMesSiguiente.setOnClickListener {
            binding.calendarView.findFirstVisibleMonth()?.let {
                binding.calendarView.smoothScrollToMonth(it.yearMonth.plusMonths(1))
            }
        }
    }

    private fun pintarIndicadores(container: DiaViewContainer, fecha: LocalDate) {
        container.layoutIndicadores.removeAllViews()
        val fechaStr = fecha.format(formateador)

        // Buscamos si hay citas este día
        val citasDelDia = listaMaestraCitas.filter { it.fechaCreacion == fechaStr }

        citasDelDia.forEach { cita ->
            val punto = View(requireContext())
            val dimension = (8 * resources.displayMetrics.density).toInt() // 8dp
            val params = LinearLayout.LayoutParams(dimension, dimension).apply {
                setMargins(2, 0, 2, 0)
            }
            punto.layoutParams = params
            punto.background = ContextCompat.getDrawable(requireContext(), R.drawable.background_indicador_cita)

            // Obtenemos color del Enum TipoCita
            val color = try {
                TipoCita.valueOf(cita.tipoCita?.uppercase() ?: "REVISION").colorRes
            } catch (e: Exception) {
                R.color.botones
            }
            punto.backgroundTintList = ContextCompat.getColorStateList(requireContext(), color)

            container.layoutIndicadores.addView(punto)
        }
    }

    private fun filtrarCitasPorFecha(fecha: LocalDate) {
        val fechaStr = fecha.format(formateador)
        val filtradas = listaMaestraCitas.filter { it.fechaCreacion == fechaStr }

        citaAdapter.actualizarLista(filtradas)

        // TODO: PREGUNTAR COMO PREFIEREN EL TITULO DE LA CITA
        binding.tvCitasDia.text = "Citas para el $fechaStr"
        binding.tvCitasDia.visibility = if (filtradas.isEmpty()) View.GONE else View.VISIBLE
        binding.rvCitasDia.visibility = if (filtradas.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun crearCitasDePrueba() {
        listaMaestraCitas.clear()
        val hoy = LocalDate.now().format(formateador)

        // 1. VACUNACIÓN
        listaMaestraCitas.add(Cita(
            id = "C-001",
            tipoCita = "VACUNACION",
            fechaCreacion = hoy,
            fechaHoraInicio = "10:00",
            motivoConsulta = "Vacuna Polivalente Anual",
            mascota = Mascota(nombre = "Toby"),
            clinica = Clinica(nombre = "VetyCare Madrid"),
            veterinario = Veterinario(nombre = "Dra. Elena Rodríguez")
        ))

        // 2. REVISIÓN
        listaMaestraCitas.add(Cita(
            id = "C-002",
            tipoCita = "REVISION",
            fechaCreacion = hoy,
            fechaHoraInicio = "12:30",
            motivoConsulta = "Revisión post-operatoria de castración",
            mascota = Mascota(nombre = "Luna"),
            clinica = Clinica(nombre = "VetyCare Madrid"),
            veterinario = Veterinario(nombre = "Dr. Marcos Sanz")
        ))

        // 3. CONSULTA
        listaMaestraCitas.add(Cita(
            id = "C-003",
            tipoCita = "CONSULTA",
            fechaCreacion = "10/04/2026",
            fechaHoraInicio = "09:15",
            motivoConsulta = "El paciente presenta picor excesivo en las orejas",
            mascota = Mascota(nombre = "Rex"),
            clinica = Clinica(nombre = "VetyCare Alcala"),
            veterinario = Veterinario(nombre = "Dr. Alberto Ruiz")
        ))

        // 4. PRUEBAS
        listaMaestraCitas.add(Cita(
            id = "C-004",
            tipoCita = "PRUEBAS",
            fechaCreacion = "30/04/2026",
            fechaHoraInicio = "08:00",
            motivoConsulta = "Análisis de sangre y Ecografía abdominal",
            mascota = Mascota(nombre = "Bella"),
            clinica = Clinica(nombre = "VetyCare Madrid"),
            veterinario = Veterinario(nombre = "Dra. Patricia García")
        ))

        // 5. MEDICAMENTOS
        listaMaestraCitas.add(Cita(
            id = "C-005",
            tipoCita = "MEDICAMENTOS",
            fechaCreacion = "15/04/2026",
            fechaHoraInicio = "11:00",
            motivoConsulta = "Recogida de tratamiento crónico para el corazón",
            mascota = Mascota(nombre = "Coco"),
            clinica = Clinica(nombre = "VetyCare Alcala"),
            veterinario = Veterinario(nombre = "Dra. Marta López")
        ))
    }
}*/

class UsuarioCalendarioFragment : Fragment() {

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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        auth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance(FirebaseUtils.URL_RTDB)
        databaseReference = firebaseDatabase.reference
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUsuarioCalendarioBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

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
    }

    private fun instancias() {
        listaMaestraCitas = ArrayList()
        listaCitasFiltradas = ArrayList()
        citaAdapter = CitaAdapter(listaCitasFiltradas, requireContext())
    }

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

    private fun configurarRecyclerView() {
        binding.rvCitasDia.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCitasDia.adapter = citaAdapter
    }

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

    private fun actualizarTituloMes(yearMonth: YearMonth) {
        val nombre = yearMonth.month
            .getDisplayName(TextStyle.FULL, Locale("es"))
            .replaceFirstChar { it.uppercase() }

        binding.tvMesAnio.text = "$nombre ${yearMonth.year}"
    }

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
                        listaMaestraCitas.addAll(listaResultado.map { it.second })

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
}