package com.example.vetycare.ui.fragment.mascota

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.vetycare.databinding.FragmentMascotaTratamientoBinding
import com.example.vetycare.navigation.NavigatorMascota

class MascotaTratamientoFragment : Fragment() {
    private lateinit var binding : FragmentMascotaTratamientoBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMascotaTratamientoBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        // TODO: CUANDO PRESIONES UN TRATAMIENTO SE ABRA MascotaTratamientoInfoFragment
        // PROVISIONAL PARA LLEGAR A LA ZONA DE TRATAMIENTO INFO
        binding.tvTitulo.setOnClickListener {

            navegacionFragment(1)
        }
    }

    // NAVEGACION ENTRE FRAGMENTS
    fun navegacionFragment(num: Int) {
        when (num) {
            1 -> NavigatorMascota.MascotaTratamiento_to_MascotaTratamientoInfo(this)
        }
    }
}