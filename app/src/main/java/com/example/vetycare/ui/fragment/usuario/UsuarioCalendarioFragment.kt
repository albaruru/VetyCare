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

        // Para cuando le des al boton de volver del móvil vuelva a UsuarioInicio
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navegacionFragment(1)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    fun navegacionFragment(num : Int) {
        when (num) {
            1 -> NavigatorUsuario.UsuarioCalendario_to_UsuarioInicio(this@UsuarioCalendarioFragment)
        }
    }

    private fun instancias() {
        listaMaestraCitas = ArrayList()
        listaCitasFiltradas = ArrayList()
        citaAdapter = CitaAdapter(listaCitasFiltradas, requireContext(), this)
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

    override fun onCancelarClick(idCita: String) {
        idCitaSeleccionadaParaCancelar = idCita

        ConfirmacionDialog.nuevoDialog(
            "CANCELAR CITA",
            "¿Estás seguro de que deseas cancelar esta cita?\nEsta acción no se puede deshacer.",
            keyConfirmarCancelacion
        ).show(parentFragmentManager, "ConfirmacionDialog")
    }

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