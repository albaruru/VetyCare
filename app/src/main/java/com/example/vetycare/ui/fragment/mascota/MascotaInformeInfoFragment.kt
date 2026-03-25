package com.example.vetycare.ui.fragment.mascota

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.vetycare.databinding.FragmentMascotaInformeInfoBinding

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
        // TODO: BINDING PARA EL BOTON DE VER MENOS Y PDF
    }
}