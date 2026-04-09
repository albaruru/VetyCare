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
import com.example.vetycare.model.entities.Clinica
import com.example.vetycare.model.entities.Mascota
import com.example.vetycare.model.entities.Veterinario
import com.example.vetycare.model.enums.TipoCita
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.ViewContainer
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale
//import androidx.lifecycle.lifecycleScope
//import com.example.vetycare.ui.container.CitasAdapter
//import com.example.vetycare.ui.container.CitasViewModel

class UsuarioCalendarioFragment : Fragment() {
    private lateinit var binding: FragmentUsuarioCalendarioBinding
    //private val viewModel: CitasViewModel by viewModels()
    private lateinit var citaAdapter: CitaAdapter

    private val listaMaestraCitas = ArrayList<Cita>()
    private val listaCitasFiltradas = ArrayList<Cita>()
    private val formateador = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentUsuarioCalendarioBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configurarCalendario()
        configurarRecyclerView()
        crearCitasDePrueba()
        // observarDatos()           // Firebase
    }

    override fun onResume() {
        super.onResume()
        // TODO: El boton pedir cita ha quedado descartado por parte de la caudilla
    }

    // ── DiaViewContainer fuera del comentario para que compile ──
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

    private fun configurarRecyclerView(){
        citaAdapter = CitaAdapter(listaCitasFiltradas, requireContext())
        binding.rvCitasDia.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCitasDia.adapter = citaAdapter
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

    /* FIXME: Comentado hasta conectar Firebase
    private fun configurarRecyclerView() {
        binding.rvCitasDia.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCitasDia.adapter = citasAdapter
    }

    private fun observarDatos() {
        lifecycleScope.launch {
            viewModel.todasLasCitas.collect {
                binding.calendarView.notifyCalendarChanged()
            }
        }
        lifecycleScope.launch {
            viewModel.citasDelDia.collect { citas ->
                citasAdapter.submitList(citas)
                binding.tvCitasDia.visibility = if (citas.isEmpty()) View.GONE else View.VISIBLE
                binding.rvCitasDia.visibility = if (citas.isEmpty()) View.GONE else View.VISIBLE
            }
        }
    }
    */
}