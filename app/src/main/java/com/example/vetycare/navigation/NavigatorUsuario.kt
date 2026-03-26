package com.example.vetycare.navigation

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.vetycare.R

object NavigatorUsuario {

    /* NAVEGACIÓN CONTAINER USUARIO: desglose por métodos

    - UsuarioMascota_to_UsuarioInicio             => Navegamos del Fragment Mascota           => Fragment Inicio
    - UsuarioMascota_to_UsuarioRegMascota         => Navegamos del Fragment Mascota           => Fragment Registro Mascota
    - UsuarioRegMascota_to_UsuarioMascota         => Navegamos del Fragment Registro Mascota  => Fragment Mascota

    - UsuarioCalendario_to_UsuarioInicio          => Navegamos del Fragment Calendario        => Fragment Inicio
    - UsuarioCalendario_to_UsuarioCalendarioCita  => Navegamos del Fragment Calendario        => Fragment Calendario Cita
    - UsuarioCalendarioCita_to_UsuarioCalendario  => Navegamos del Fragment Calendario Cita   => Fragment Calendario

    - UsuarioPerfil_to_UsuarioInicio              => Navegamos del Fragment Perfil            => Fragment Inicio

    - UsuarioClinica_to_UsuarioInicio             => Navegamos del Fragment Clinica           => Fragment Inicio

     */

    /* --- NAVEGACIÓN DE PERFIL MASCOTAS --- */
    fun UsuarioMascota_to_UsuarioInicio (fragment: Fragment) {
        fragment.findNavController().navigate(R.id.action_UsuarioMascotaFragment_to_UsuarioInicioFragment)
    }
    fun UsuarioMascota_to_UsuarioRegMascota (fragment: Fragment) {
        fragment.findNavController().navigate(R.id.action_UsuarioMascotaFragment_to_UsuarioRegMascota)
    }
    fun UsuarioRegMascota_to_UsuarioMascota (fragment: Fragment) {
        fragment.findNavController().navigate(R.id.action_UsuarioRegMascota_to_UsuarioMascotaFragment)
    }

    /* --- NAVEGACIÓN DE CALENDARIO DE CITAS --- */
    fun UsuarioCalendario_to_UsuarioInicio (fragment: Fragment) {
        fragment.findNavController().navigate(R.id.action_UsuarioCalendario_to_UsuarioInicioFragment)
    }
    fun UsuarioCalendario_to_UsuarioCalendarioCita (fragment: Fragment) {
        fragment.findNavController().navigate(R.id.action_UsuarioCalendario_to_UsuarioCalendarioCita)
    }
    fun UsuarioCalendarioCita_to_UsuarioCalendario (fragment: Fragment) {
        fragment.findNavController().navigate(R.id.action_UsuarioCalendarioCita_to_UsuarioCalendario)
    }

    /* --- NAVEGACIÓN DE USUARIO PERFIL --- */
    fun UsuarioPerfil_to_UsuarioInicio (fragment: Fragment) {
        fragment.findNavController().navigate(R.id.action_UsuarioPerfilFragment_to_UsuarioInicioFragment)
    }

    /* --- NAVEGACIÓN DE USUARIO CLINICAS --- */
    fun UsuarioClinica_to_UsuarioInicio (fragment: Fragment) {
        fragment.findNavController().navigate(R.id.action_UsuarioClinicaFragment_to_UsuarioInicioFragment)
    }
    fun UsuarioClinica_to_UsuarioClinicaMapa (fragment: Fragment) {
        fragment.findNavController().navigate(R.id.action_UsuarioClinicaFragment_to_usuarioClinicaMapaFragment)
    }
    fun UsuarioClinicaMapa_to_UsuarioClinica (fragment: Fragment) {
        fragment.findNavController().navigate(R.id.action_usuarioClinicaMapaFragment_to_UsuarioClinicaFragment)
    }
}