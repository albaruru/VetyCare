package com.example.vetycare.navigation

import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.vetycare.R

object NavigatorRoot {

    /* NAVEGACION CONTAINER INICIO: desgloce por métodos
    * - InicioToUsuario => Navegamos del Container Inicio    => Container Usuario
    * */
    fun InicioToUsuario (fragment: Fragment) {
        fragment.requireActivity()
            .findNavController(R.id.nav_host_root)
            .navigate(R.id.action_inicioContainerFragment_to_usuarioContainerFragment)
    }

    /* NAVEGACIÓN CONTAINER USUARIO
    * - UsuarioToInicio => Navegamos del Container Usuario   => Container Inicio
    * - UsuarioToMascota => Navegamos del Container Usuario  => Container Mascota
    * */
    fun UsuarioToInicio (fragment: Fragment) {
        fragment.requireActivity()
            .findNavController(R.id.nav_host_root)
            .navigate(R.id.action_usuarioContainerFragment_to_inicioContainerFragment)
    }
    fun UsuarioToMascota (fragment: Fragment) {
        fragment.requireActivity()
            .findNavController(R.id.nav_host_root)
            .navigate(R.id.action_usuarioContainerFragment_to_mascotaContainerFragment)
    }

    /* NAVEGACIÓN CONTAINER MASCOTA
    * - MascotaToUsuario => Navegamos del Container Mascota  => Container Usuario
    * */
    fun MascotaToUsuario (fragment: Fragment) {
        fragment.requireActivity()
            .findNavController(R.id.nav_host_root)
            .navigate(R.id.action_mascotaContainerFragment_to_usuarioContainerFragment)
    }
}