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
}