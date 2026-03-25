package com.example.vetycare.ui.fragment.mascota

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.vetycare.databinding.FragmentMascotaTratamientoInfoBinding
import com.example.vetycare.navigation.NavigatorMascota

class MascotaTratamientoInfoFragment : Fragment() {
    private lateinit var binding : FragmentMascotaTratamientoInfoBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMascotaTratamientoInfoBinding.inflate(layoutInflater,container,false)
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
}