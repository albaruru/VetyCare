package com.example.vetycare.ui.fragment.inicio

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.vetycare.R
import com.example.vetycare.databinding.FragmentInicioRegistroBinding
import com.example.vetycare.navigation.NavigatorInicio

class InicioRegistroFragment : Fragment() {
    private lateinit var binding : FragmentInicioRegistroBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInicioRegistroBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        /* Acciones de los botones del fragment:
        * - Botón Guardar => Recogeremos el correo y la contraseña para cambiar las credenciales del usuario en FireBase
        * - Botón Volver => Descarta cualquier información introducida en nuestros bloques de texto y volvemos a la pantalla login
        * */
        binding.btnGuardar.setOnClickListener {

            navegacionFragment(1)
        }
        binding.btnVolver.setOnClickListener {

            navegacionFragment(2)
        }
    }

    /* NAVEGACION ENTRE FRAGMENTS */
    fun navegacionFragment(num : Int) {
        when (num) {
            1 -> NavigatorInicio.InicioRegistroToConfirmacionDialog(this) // Navega al Diagog Confirmacion
            2 -> NavigatorInicio.InicioRegistroToCancelacionDialog(this) // Navega al Dialog Cancelacion
        }
    }
}