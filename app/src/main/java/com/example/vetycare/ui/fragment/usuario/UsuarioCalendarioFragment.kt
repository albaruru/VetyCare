package com.example.vetycare.ui.fragment.usuario

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.vetycare.databinding.FragmentUsuarioCalendarioBinding
import com.example.vetycare.navigation.NavigatorUsuario

class UsuarioCalendarioFragment : Fragment() {
    private lateinit var binding : FragmentUsuarioCalendarioBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View? {
        binding = FragmentUsuarioCalendarioBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        /* Acciones de los botones del fragment:
        * -
        * */
<<<<<<< HEAD
        binding.rbSemanal.setOnClickListener {

            navegacionFragment(1)
        }
    }

    fun navegacionFragment(num: Int) {
        when (num) {
            1 -> NavigatorUsuario.UsuarioCalendario_to_UsuarioCalendarioCita(this)
        }
=======
        binding.rbPedirCita.setOnClickListener { NavigatorUsuario.UsuarioCalendario_to_UsuarioCalendarioCita(this) }
>>>>>>> feature/visual
    }
}