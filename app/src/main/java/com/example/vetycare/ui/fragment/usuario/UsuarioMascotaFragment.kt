package com.example.vetycare.ui.fragment.usuario

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.vetycare.databinding.FragmentUsuarioMascotaBinding
import com.example.vetycare.navigation.NavigatorUsuario

class UsuarioMascotaFragment: Fragment() {
    private lateinit var binding : FragmentUsuarioMascotaBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View? {
        binding = FragmentUsuarioMascotaBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        /* Acciones de los botones del fragment:
        -
        */
        binding.tvTitulo.setOnClickListener {

            navegacionFragment(1)
        }

    }

    /* NAVEGACION ENTRE FRAGMENTS

    */
    fun navegacionFragment(num : Int) {
        when (num) {
            1 -> NavigatorUsuario.UsuarioMascota_to_UsuarioRegMascota(this)
        }
    }
}