package com.example.vetycare.ui.fragment.usuario

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.vetycare.databinding.FragmentUsuarioClinicaBinding
import com.example.vetycare.navigation.NavigatorRoot

class UsuarioClinicaFragment : Fragment (){
    private lateinit var binding : FragmentUsuarioClinicaBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View? {
        binding = FragmentUsuarioClinicaBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        /* Acciones de los botones del fragment:
        * -
        * */
        binding.tvTitulo.setOnClickListener {
            NavigatorRoot.Usuario_to_Mascota(this)
        }
    }

}