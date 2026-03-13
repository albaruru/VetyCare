package com.example.vetycare.ui.fragment.inicio

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.vetycare.R
import com.example.vetycare.databinding.FragmentInicioRecPassBinding
import com.example.vetycare.navigation.NavigatorInicio

class InicioRecPassFragment : Fragment() {
    private lateinit var binding : FragmentInicioRecPassBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInicioRecPassBinding.inflate(layoutInflater, container,false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        /* Acciones de los botones del fragment:
        * - Botón Guardar => Recogeremos el correo y la contraseña para cambiar las credenciales del usuario en FireBase
        * */
        binding.btnGuardar.setOnClickListener {
            // TODO: Falta definir su funcionalidad...

            navegacionFragment()
        }
    }

    /* NAVEGACION ENTRE FRAGMENTS */
    fun navegacionFragment() {
        NavigatorInicio.InicioRecPassToInicioPrincipal(this) // Navega al Fragment Inicio Principal
    }
}