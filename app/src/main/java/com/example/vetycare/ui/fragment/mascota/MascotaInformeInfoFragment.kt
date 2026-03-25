package com.example.vetycare.ui.fragment.mascota

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.vetycare.databinding.FragmentMascotaInformeInfoBinding
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
        - Botón Ver Menos => Navega al MascotaInformeFragment
        - Botón PDF => Creará un PDF de la información del informe
        */
        binding.btnVerMenos.setOnClickListener {
            navegacionFragment(1)
        }
        binding.btnPdf.setOnClickListener {
            // TODO: CREAR LA LOGICA DE LA FUNCION DE GENERAR PDF
            generarPDF()
        }
    }

    // NAVEGACION ENTRE FRAGMENTS
    fun navegacionFragment(num: Int) {
        when (num) {
            1 -> NavigatorMascota.MascotaInformeInfo_to_MascotaInforme(this)
        }
    }

    // FUNCIÓN PARA GENERAR PDF DE LA INFORMACION DEL INFROME
    private fun generarPDF() {
       
    }
}