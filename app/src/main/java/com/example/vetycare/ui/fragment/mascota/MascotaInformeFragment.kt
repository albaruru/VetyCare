package com.example.vetycare.ui.fragment.mascota

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vetycare.adapter.InformeAdapter
import com.example.vetycare.databinding.FragmentMascotaInformeBinding
import com.example.vetycare.model.entities.Diagnostico
import com.example.vetycare.model.entities.Medicamento
import com.example.vetycare.model.entities.Patologia
import com.example.vetycare.model.entities.Tratamiento
import com.example.vetycare.navigation.NavigatorMascota

class MascotaInformeFragment : Fragment(), InformeAdapter.OnInformeListener {
    private lateinit var binding: FragmentMascotaInformeBinding
    private lateinit var adapterInforme: InformeAdapter
    private var listaInformes = ArrayList<Diagnostico>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMascotaInformeBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar adaptador y recycler
        adapterInforme = InformeAdapter(listaInformes, requireContext(), this)
        binding.rvInformes.layoutManager = LinearLayoutManager(context)
        binding.rvInformes.adapter = adapterInforme

        crearInformesDePrueba()
    }

    private fun crearInformesDePrueba() {
        listaInformes.clear()

        // Informe 1: Revisión Rutinaria
        listaInformes.add(Diagnostico(
            id = "2024/001",
            fechaDiagnostico = "15/03/2024",
            valoracion = "Paciente en buen estado general. Se realiza desparasitación interna y revisión de constantes. Corazón y pulmones normales.",
            importeTotal = 45.00,
            patologia = Patologia(nombre = "Chequeo Preventivo"),
            medicamento = Medicamento(nombreComercial = "Milbemax Comprimidos"),
            tratamiento = Tratamiento(tipoTratamiento = "Protocolo Desparasitación")
        ))

        // Informe 2: Infección de Oído
        listaInformes.add(Diagnostico(
            id = "2024/002",
            fechaDiagnostico = "22/03/2024",
            valoracion = "Se observa inflamación y secreción en el conducto auditivo derecho. El paciente muestra dolor a la palpación.",
            importeTotal = 62.50,
            patologia = Patologia(nombre = "Otitis Externa"),
            medicamento = Medicamento(nombreComercial = "Posatex Gotas"),
            tratamiento = Tratamiento(tipoTratamiento = "Limpieza y gotas diarias")
        ))

        // Informe 3: Alergia Alimentaria
        listaInformes.add(Diagnostico(
            id = "2024/003",
            fechaDiagnostico = "05/04/2024",
            valoracion = "Dermatitis por rascado en zona abdominal. Se sospecha de intolerancia a la proteína de pollo en el pienso actual.",
            importeTotal = 30.00,
            patologia = Patologia(nombre = "Alergia Cutánea"),
            medicamento = Medicamento(nombreComercial = "Apoquel 5.4mg"),
            tratamiento = Tratamiento(tipoTratamiento = "Cambio a dieta hidrolizada")
        ))

        // Notificamos al adaptador para refrescar el RecyclerView
        adapterInforme.notifyDataSetChanged()
    }

    override fun onInformeClick(informe: Diagnostico) {
        navegacionFragment(2, informe) // Pasamos el objeto al navegar
    }

    override fun onResume() {
        super.onResume()
        // PROVISIONAL PARA LLEGAR A LA ZONA DE INFORME INFO
        binding.tvTitulo.setOnClickListener {

            navegacionFragment(1)
        }
    }

    // NAVEGACION ENTRE FRAGMENTS
    fun navegacionFragment(num: Int, informe: Diagnostico? = null) {
        when (num) {
            1 -> NavigatorMascota.MascotaInforme_to_MascotaInformeInfo(this)
            2 -> {
                if (informe != null) {
                    NavigatorMascota.MascotaInforme_to_MascotaInformeInfo(this, informe)
                }
            }
        }
    }
}