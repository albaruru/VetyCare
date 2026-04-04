package com.example.vetycare.navigation

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.vetycare.R
import com.example.vetycare.model.entities.Mascota

object NavigatorRoot {

    /* NAVEGACION CONTAINER INICIO: desgloce por métodos
    * - InicioToUsuario => Navegamos del Container Inicio    => Container Usuario
    * */
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

    fun UsuarioMascota_to_MascotaPerfil(fragment: Fragment, mascota: Mascota) {
        val bundle = Bundle()
        bundle.putSerializable("mascota_key", mascota)

        // Usamos el mismo NavHost que en el resto de tus funciones
        fragment.requireActivity()
            .findNavController(R.id.nav_host_root)
            .navigate(R.id.action_usuarioContainerFragment_to_mascotaContainerFragment, bundle)
    }
}