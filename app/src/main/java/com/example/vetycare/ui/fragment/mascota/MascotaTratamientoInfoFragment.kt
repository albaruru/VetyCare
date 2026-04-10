package com.example.vetycare.ui.fragment.mascota

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.vetycare.databinding.FragmentMascotaTratamientoInfoBinding
import com.example.vetycare.model.entities.Tratamiento
import com.example.vetycare.navigation.NavigatorMascota

class MascotaTratamientoInfoFragment : Fragment() {
    private lateinit var _binding : FragmentMascotaTratamientoInfoBinding
    private val binding get() = _binding
    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentMascotaTratamientoInfoBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        /* Acciones de los botones del fragment:
        - Botón Volver => Navega al MascotaTratamientoFragment
        */
        binding.btnVolver.setOnClickListener {
            navegacionFragment(1)
        }
    }

    // NAVEGACION ENTRE FRAGMENTS
    fun navegacionFragment(num: Int) {
        when (num) {
            1 -> NavigatorMascota.MascotaTratamientoInfo_to_MascotaTratamiento(this)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tratamiento = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable("tratamiento_key", Tratamiento::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getSerializable("tratamiento_key") as? Tratamiento
        }

        if (tratamiento != null) {
            pintarDatosTratamiento(tratamiento)
        }
    }

    private fun pintarDatosTratamiento(t: Tratamiento) {
        binding.tvTitulo.text = "Tratamiento #${t.id?.uppercase()}"
        binding.tvProcedimiento.text = t.tipoTratamiento
        binding.tvFechaInicio.text = t.fechaInicio
        binding.tvFechaFin.text = t.fechaFin
        binding.tvObservaciones.text = t.observaciones
        binding.tvMedicacion.text = t.medicamento?.nombreComercial ?: "No especificado"
        binding.tvDosis.text = t.detallesMedicacion?.dosis ?: "N/A"
        binding.tvFrecuencia.text = t.detallesMedicacion?.frecuencia ?: "N/A"
        binding.tvViaAdministracion.text = t.detallesMedicacion?.viaAdministracion ?: "N/A"
    }
}