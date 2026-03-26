package com.example.vetycare.ui.fragment.usuario

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
//import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vetycare.R
import com.example.vetycare.databinding.FragmentUsuarioCalendarioBinding
import com.example.vetycare.model.Cita
import com.example.vetycare.model.TipoCita
import com.example.vetycare.navigation.NavigatorUsuario
//import com.example.vetycare.ui.container.CitasAdapter
//import com.example.vetycare.ui.container.CitasViewModel
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.ViewContainer
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

class UsuarioCalendarioFragment : Fragment() {
    private lateinit var binding : FragmentUsuarioCalendarioBinding
    // ── AÑADIDO ──────────────────────────────────────
    //private val viewModel: CitasViewModel by viewModels()
    //private val citasAdapter = CitasAdapter()
    //

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View? {
        binding = FragmentUsuarioCalendarioBinding.inflate(layoutInflater,container,false)
        return binding.root
    }
    // ── AÑADIDO ──────────────────────────────────────
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //configurarRecyclerView()
        //configurarCalendario()
        //observarDatos()
    }
    // ─────────────────────────────────────────────────
    override fun onResume() {
        super.onResume()

        /* Acciones de los botones del fragment:
        * -
        * */
        // TODO: El boton pedir cita ha quedado descartado por parte de la caudilla

    }

    // ── TODO LO DE AQUÍ ABAJO ES AÑADIDO (ALBA) ─────────────
    /* FIXME: Está comentado porque todavia no está conectado con firebase
    private fun configurarRecyclerView() {
        binding.rvCitasDia.layoutManager = LinearLayoutManager(requireContext())
        //binding.rvCitasDia.adapter = citasAdapter
    }

    private fun configurarCalendario() {
        val mesActual = YearMonth.now()
        val diasSemana = daysOfWeek()

        binding.calendarView.setup(
            mesActual.minusMonths(1),
            mesActual.plusMonths(12),
            diasSemana.first()
        )
        binding.calendarView.scrollToMonth(mesActual)

        binding.calendarView.dayBinder = object : MonthDayBinder<DiaViewContainer> {
            override fun create(view: View) = DiaViewContainer(view)
            override fun bind(container: DiaViewContainer, data: CalendarDay) {
                container.bind(data, viewModel.todasLasCitas.value)
            }
        }
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
                if (citas.isEmpty()) {
                    binding.tvCitasDia.visibility = View.GONE
                    binding.rvCitasDia.visibility = View.GONE
                } else {
                    binding.tvCitasDia.visibility = View.VISIBLE
                    binding.rvCitasDia.visibility = View.VISIBLE
                }
            }
        }
    }

    inner class DiaViewContainer(view: View) : ViewContainer(view) {
        val tvDia = view.findViewById<TextView>(R.id.tvDia)
        val layoutIndicadores = view.findViewById<LinearLayout>(R.id.layoutIndicadores)
        lateinit var day: CalendarDay

        init {
            view.setOnClickListener {
                if (day.position == DayPosition.MonthDate) {
                    val fecha = "%04d-%02d-%02d".format(
                        day.date.year,
                        day.date.monthValue,
                        day.date.dayOfMonth
                    )
                    viewModel.seleccionarDia(fecha)
                }
            }
        }

        fun bind(data: CalendarDay, citas: List<Cita>) {
            day = data
            tvDia.text = data.date.dayOfMonth.toString()
            tvDia.alpha = if (data.position == DayPosition.MonthDate) 1f else 0.3f

            if (data.date == LocalDate.now() && data.position == DayPosition.MonthDate) {
                tvDia.setBackgroundResource(R.drawable.background_cuadro_formulario)
            } else {
                tvDia.background = null
            }

            layoutIndicadores.removeAllViews()
            if (data.position == DayPosition.MonthDate) {
                val fechaDia = "%04d-%02d-%02d".format(
                    data.date.year,
                    data.date.monthValue,
                    data.date.dayOfMonth
                )
                val tiposCitasDelDia = citas
                    .filter { it.fechaHoraInicio.startsWith(fechaDia) }
                    .mapNotNull { cita ->
                        when (cita.tipoCita.lowercase()) {
                            "vacunacion", "vacunación" -> TipoCita.VACUNACION
                            "revision", "revisión"    -> TipoCita.REVISION
                            "consulta"                -> TipoCita.CONSULTA
                            "pruebas"                 -> TipoCita.PRUEBAS
                            "medicamentos"            -> TipoCita.MEDICAMENTOS
                            else                      -> null
                        }
                    }
                    .toSet()

                tiposCitasDelDia.forEach { tipo ->
                    val punto = View(requireContext()).apply {
                        val params = LinearLayout.LayoutParams(8, 8)
                        params.marginEnd = 2
                        layoutParams = params
                        background = GradientDrawable().apply {
                            shape = GradientDrawable.OVAL
                            setColor(ContextCompat.getColor(requireContext(), tipo.colorRes))
                        }
                    }
                    layoutIndicadores.addView(punto)
                }
            }
        }
    }*/
}