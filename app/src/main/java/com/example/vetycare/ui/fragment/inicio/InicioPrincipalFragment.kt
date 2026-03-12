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

class InicioPrincipalFragment : Fragment() {
    private lateinit var binding : FragmentInicioPrincipalBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
            1 -> findNavController().navigate(R.id.action_NavLogin_NavUsuario) // Navega a fragment_inicio_usuario
            2 -> findNavController().navigate(R.id.action_InicioFragment_to_RegUsuarioFragment) // Navega a fragment_reg_usuario
            3 -> findNavController().navigate(R.id.action_InicioFragment_to_RecPassFragment) // Navega a fragment_rec_pass
        }
    }
}