package com.example.vetycare.ui.container

import android.os.Bundle
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.example.vetycare.R

class UsuarioContainerFragment : Fragment (R.layout.fragment_container_usuario) {

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        val navHostFragment = childFragmentManager
            .findFragmentById(R.id.container_host_usuario) as NavHostFragment

        val navController = navHostFragment.navController

        /* Bindemos nuestras variables con los botones existentes */
        val botonPerfilMascotas = view?.findViewById<ImageButton> (R.id.btnIniMasc)
        val botonCalendario = view?.findViewById<ImageButton> (R.id.btnCalendarUsu)
        val botonPerfilUsuario = view?.findViewById<ImageButton> (R.id.btnUsuario)
        val botonClinicas = view?.findViewById<ImageButton> (R.id.btnClinicas)

        /* Asignamos la navegación en las variables de los botones creados:
        *
        * */
        botonPerfilMascotas?.setOnClickListener {
            navController.navigate(R.id.UsuarioMascotaFragment)
        }
        botonCalendario?.setOnClickListener {
            navController.navigate(R.id.UsuarioCalendario)
        }
        botonPerfilUsuario?.setOnClickListener {
            navController.navigate(R.id.UsuarioPerfilFragment)
        }
        botonClinicas?.setOnClickListener {
            navController.navigate(R.id.UsuarioClinicaFragment)
        }
    }
}