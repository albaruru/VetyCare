package com.example.vetycare.ui.fragment.mascota

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vetycare.adapter.TratamientoAdapter
import com.example.vetycare.databinding.FragmentMascotaTratamientoBinding
import com.example.vetycare.model.entities.Medicamento
import com.example.vetycare.model.entities.Tratamiento
import com.example.vetycare.model.relational.MedicamentoPorTratamiento
import com.example.vetycare.navigation.NavigatorMascota

class MascotaTratamientoFragment : Fragment(), TratamientoAdapter.OnTratamientoListener {
    private lateinit var binding : FragmentMascotaTratamientoBinding
    private lateinit var adapterTratamiento: TratamientoAdapter
    private var listaTratamientos = ArrayList<Tratamiento>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMascotaTratamientoBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configuración del Recycler
        adapterTratamiento = TratamientoAdapter(listaTratamientos, requireContext(), this)
        binding.rvTratamientos.layoutManager = LinearLayoutManager(context)
        binding.rvTratamientos.adapter = adapterTratamiento

        crearTratamientosDePrueba()
    }

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

    override fun onTratamientoClick(tratamiento: Tratamiento) {
        navegacionFragment(2, tratamiento)
    }

    override fun onResume() {
        super.onResume()
        // PROVISIONAL PARA LLEGAR A LA ZONA DE TRATAMIENTO INFO
        binding.tvTitulo.setOnClickListener {

            navegacionFragment(1)
        }
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
}