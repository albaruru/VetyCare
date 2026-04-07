package com.example.vetycare.ui.fragment.mascota

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.vetycare.databinding.FragmentMascotaInformeInfoBinding
import com.example.vetycare.model.entities.Diagnostico
import com.example.vetycare.navigation.NavigatorInicio
import com.example.vetycare.navigation.NavigatorMascota
import com.example.vetycare.utils.mostrarSnackbar

class MascotaInformeInfoFragment : Fragment() {
    private lateinit var binding : FragmentMascotaInformeInfoBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMascotaInformeInfoBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        /* Acciones de los botones del fragment:
        - Botón Volver => Navega al MascotaInformeFragment
        */
        binding.btnVolver.setOnClickListener {
            navegacionFragment(1)
        }
    }

    // NAVEGACION ENTRE FRAGMENTS
    fun navegacionFragment(num: Int) {
        when (num) {
            1 -> NavigatorMascota.MascotaInformeInfo_to_MascotaInforme(this)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Recuperamos el informe del Bundle de forma segura
        val informe = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable("informe_key", Diagnostico::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getSerializable("informe_key") as? Diagnostico
        }

        //Si el informe existe, pintamos los datos en la pantalla
        if (informe != null) {
            pintarDatosInforme(informe)
        }
    }

    private fun pintarDatosInforme(i: Diagnostico) {
        // Mapeamos los campos del objeto Diagnostico a los IDs del XML
        binding.tvTitulo.text = "Informe #${i.id}"
        binding.tvConcepto.text = i.patologia?.nombre ?: "Sin concepto"
        binding.tvFecha.text = i.fechaDiagnostico
        binding.tvValoracion.text = i.valoracion // El informe redactado

        // Usamos los nombres de campos que definimos en los informes de prueba
        binding.tvMedicamento.text = i.medicamento?.nombreComercial ?: "N/A"
        binding.tvTratamiento.text = i.tratamiento?.tipoTratamiento ?: "N/A"

        binding.tvImporte.text = "${i.importeTotal} €"
    }
}