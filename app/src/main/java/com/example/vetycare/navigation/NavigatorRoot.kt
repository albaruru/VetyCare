package com.example.vetycare.navigation

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.vetycare.R
import com.example.vetycare.model.entities.Mascota
import com.example.vetycare.ui.container.MascotaContainerFragment

object NavigatorRoot {

    /* NAVEGACION CONTAINER INICIO:
    Controla la navegación entre los containers principales de la app,
    gestionando el flujo global entre secciones y el paso de datos entre módulos.

    Desgloce por métodos:
     - InicioToUsuario => Navegamos del Container Inicio => Container Usuario
    */
    fun Inicio_to_Usuario (fragment: Fragment) {
        fragment.requireActivity()
            .findNavController(R.id.nav_host_root)
            .navigate(R.id.action_inicioContainerFragment_to_usuarioContainerFragment)
    }

    /* NAVEGACIÓN CONTAINER USUARIO
    * - UsuarioToInicio => Navegamos del Container Usuario   => Container Inicio
    * - UsuarioToMascota => Navegamos del Container Usuario  => Container Mascota
    * */
    fun Usuario_to_Inicio (fragment: Fragment) {
        fragment.requireActivity()
            .findNavController(R.id.nav_host_root)
            .navigate(R.id.action_usuarioContainerFragment_to_inicioContainerFragment)
    }
    fun Usuario_to_Mascota (fragment: Fragment) {
        fragment.requireActivity()
            .findNavController(R.id.nav_host_root)
            .navigate(R.id.action_usuarioContainerFragment_to_mascotaContainerFragment)
    }

    /* NAVEGACIÓN CONTAINER MASCOTA
    * - MascotaToUsuario => Navegamos del Container Mascota  => Container Usuario
    * */
    fun Mascota_to_Usuario (fragment: Fragment) {
        fragment.requireActivity()
            .findNavController(R.id.nav_host_root)
            .navigate(R.id.action_mascotaContainerFragment_to_usuarioContainerFragment)
    }

    fun UsuarioMascota_to_MascotaPerfil(fragment: Fragment, idMascota: String?, mascota: Mascota) {
        val bundle = Bundle().apply {
            putString(MascotaContainerFragment.ARG_ID_MASCOTA, idMascota)
            putSerializable(MascotaContainerFragment.ARG_MASCOTA, mascota)
        }

        // Usamos el mismo NavHost que en el resto de tus funciones
        fragment.requireActivity()
            .findNavController(R.id.nav_host_root)
            .navigate(R.id.action_usuarioContainerFragment_to_mascotaContainerFragment, bundle)
    }
}