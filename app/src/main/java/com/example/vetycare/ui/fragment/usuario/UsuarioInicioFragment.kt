package com.example.vetycare.ui.fragment.usuario

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.vetycare.databinding.FragmentUsuarioInicioBinding

class UsuarioInicioFragment : Fragment() {
    private lateinit var binding : FragmentUsuarioInicioBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View? {
        binding = FragmentUsuarioInicioBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    /* Este fragment no tiene acciones, únicamente es la pantalla de bienvenida.
    */
    override fun onResume() {
        super.onResume()

        // TODO: Hecho. Cuando se conecte con la base de datos, obtener el nombre a partir de la rama propietario
        binding.tvNombreUsuario.setText("Alba Ruano aka LA CAUDILLA")
    }
}