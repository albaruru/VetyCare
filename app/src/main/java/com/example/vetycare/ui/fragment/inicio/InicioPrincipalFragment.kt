package com.example.vetycare.ui.fragment.inicio

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.vetycare.R
import com.example.vetycare.databinding.FragmentInicioPrincipalBinding
import com.example.vetycare.navigation.NavigatorInicio
import com.example.vetycare.navigation.NavigatorRoot

class InicioPrincipalFragment : Fragment() {
    private lateinit var binding : FragmentInicioPrincipalBinding
    private lateinit var navegar : NavigatorRoot

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView (inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View? {
        binding = FragmentInicioPrincipalBinding.inflate(layoutInflater,container,false)

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        /* Acciones de los botones del fragment:
        * - Boton entrar => Recoge nombre del usuario y contraseña. Verifica con la FireBase y entra en caso afirmativo.
        * -
        *  */
        binding.btnEntrar.setOnClickListener {

            navegacionFragment(1)
        }
        binding.tvLinkRegistrate.setOnClickListener{

            navegacionFragment(2)
        }
        binding.tvOlvideContrasenha.setOnClickListener {

            navegacionFragment(3)
        }
    }

    /* NAVEGACION ENTRE FRAGMENTS */
    fun navegacionFragment(num : Int) {
        when (num) {
            1 -> NavigatorRoot.InicioToUsuario(this) // Navega al Container Usuario
            2 -> NavigatorInicio.InicioPrincipalToInicioRegistro(this) // Navega al Fragment Inicio Registro Ususario
            3 -> NavigatorInicio.InicioPrincipalToInicioRecPass(this) // Navega al Fragment Inicio Recuperacion Contraseña
        }
    }
}