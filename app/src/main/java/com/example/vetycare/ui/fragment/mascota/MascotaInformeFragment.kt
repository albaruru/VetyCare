package com.example.vetycare.ui.fragment.mascota

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.vetycare.databinding.FragmentMascotaInformeBinding
import com.example.vetycare.navigation.NavigatorMascota

class MascotaInformeFragment : Fragment () {
    private lateinit var binding: FragmentMascotaInformeBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMascotaInformeBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        // TODO: CUANDO PRESIONES UN INFORME SE ABRA MascotaInformeInfoFragment
        // PROVISIONAL PARA LLEGAR A LA ZONA DE INFORME INFO
        binding.tvTitulo.setOnClickListener {

            navegacionFragment(1)
        }
    }

    // NAVEGACION ENTRE FRAGMENTS
    fun navegacionFragment(num: Int) {
        when (num) {
            1 -> NavigatorMascota.MascotaInforme_to_MascotaInformeInfo(this)
        }
    }
}