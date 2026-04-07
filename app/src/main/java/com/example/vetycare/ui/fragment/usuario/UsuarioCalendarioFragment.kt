package com.example.vetycare.ui.fragment.usuario

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.vetycare.databinding.FragmentUsuarioCalendarioBinding
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.ViewContainer
import java.time.YearMonth
import java.util.Locale
//import androidx.lifecycle.lifecycleScope
//import com.example.vetycare.ui.container.CitasAdapter
//import com.example.vetycare.ui.container.CitasViewModel

class UsuarioCalendarioFragment : Fragment() {
    private lateinit var binding: FragmentUsuarioCalendarioBinding
    //private val viewModel: CitasViewModel by viewModels()
    //private val citasAdapter = CitasAdapter()

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
        // configurarRecyclerView()  // Firebase
        // observarDatos()           // Firebase
    }

    override fun onResume() {
        super.onResume()
        // TODO: El boton pedir cita ha quedado descartado por parte de la caudilla
    }

    // ── DiaViewContainer fuera del comentario para que compile ──
    inner class DiaViewContainer(view: View) : ViewContainer(view) {
        val tvDia: TextView = view.findViewById(com.example.vetycare.R.id.tvDia)
        val layoutIndicadores: LinearLayout = view.findViewById(com.example.vetycare.R.id.layoutIndicadores)
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
                container.tvDia.text = data.date.dayOfMonth.toString()
                container.tvDia.alpha = if (data.position == DayPosition.MonthDate) 1f else 0.3f
                container.layoutIndicadores.removeAllViews()
                // TODO: pintaremos los puntos de color cuando conectemos Firebase
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