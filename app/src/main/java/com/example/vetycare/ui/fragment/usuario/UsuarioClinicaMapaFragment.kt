package com.example.vetycare.ui.fragment.usuario

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.vetycare.databinding.FragmentUsuarioClinicaMapaBinding
import com.example.vetycare.navigation.NavigatorUsuario

class UsuarioClinicaMapaFragment : Fragment (){
    private lateinit var binding : FragmentUsuarioClinicaMapaBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentUsuarioClinicaMapaBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.btnVerListado.setOnClickListener {
            navegacionFragment(1)
        }
    }

    fun navegacionFragment(num: Int) {
        when (num) {
            1 -> NavigatorUsuario.UsuarioClinicaMapa_to_UsuarioClinica(this)
        }
    }
}