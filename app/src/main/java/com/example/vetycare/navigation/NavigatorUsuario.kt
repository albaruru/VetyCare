package com.example.vetycare.navigation

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.vetycare.R

object NavigatorUsuario {

    /* NAVEGACION CONTAINER INICIO: desgloce por métodos
    *
    * */

    /* --- FRAGMENT DE INICIO ---
    * UsuarioInicio_to_UsuarioMascota      =>
    * UsuarioInicio_to_UsuarioCalendario   =>
    * UsuarioInicio_to_UsuarioPerfil       =>
    * UsuarioInicio_to_UsuarioClinica      =>
    * */
    fun UsuarioInicio_to_UsuarioMascota (fragment: Fragment) {
        fragment.findNavController().navigate(R.id.action_UsuarioInicioFragment_to_UsuarioMascotaFragment)
    }
    fun UsuarioInicio_to_UsuarioCalendario (fragment: Fragment) {
        fragment.findNavController().navigate(R.id.action_UsuarioInicioFragment_to_UsuarioCalendario)
    }
    fun UsuarioInicio_to_UsuarioPerfil (fragment: Fragment) {
        fragment.findNavController().navigate(R.id.action_UsuarioInicioFragment_to_UsuarioPerfilFragment)
    }
    fun UsuarioInicio_to_UsuarioClinica (fragment: Fragment) {
        fragment.findNavController().navigate(R.id.action_UsuarioInicioFragment_to_UsuarioClinicaFragment)
    }

    /* --- FRAGMENT PERFIL MASCOTAS ---
    * UsuarioMascota_to_UsuarioRegMascota     =>
    * UsuarioMascota_to_UsuarioInicio         =>
    * */
    fun UsuarioMascota_to_UsuarioRegMascota (fragment: Fragment) {
        fragment.findNavController().navigate(R.id.action_UsuarioMascotaFragment_to_UsuarioRegMascota)
    }
    fun UsuarioMascota_to_UsuarioInicio (fragment: Fragment) {
        fragment.findNavController().navigate(R.id.action_UsuarioMascotaFragment_to_UsuarioInicioFragment)
    }

    /* --- FRAGMENT DE REGISTRO MASCOTAS ---
    * UsuarioRegMascotaToUsuarioInicio    =>
    * */
    fun UsuarioRegMascota_to_UsuarioMascota (fragment: Fragment) {
        fragment.findNavController().navigate(R.id.action_UsuarioRegMascota_to_UsuarioMascotaFragment)
    }

    /* --- FRAGMENT DEL CALENDARIO DE CITAS ---
    UsuarioCalendario_to_UsuarioCalendarioCita  =>
    UsuarioCalendario_to_UsuarioInicio          =>
    */
    fun UsuarioCalendario_to_UsuarioCalendarioCita (fragment: Fragment) {
        fragment.findNavController().navigate(R.id.action_UsuarioCalendario_to_UsuarioCalendarioCita)
    }
    fun UsuarioCalendario_to_UsuarioInicio (fragment: Fragment) {
        fragment.findNavController().navigate(R.id.action_UsuarioCalendario_to_UsuarioInicioFragment)
    }

    /* --- FRAGMENT DEL CALENDARIO PARA AGENDAR CITAS ---
    UsuarioCalendarioCita_to_UsuarioCalendario      =>
    */
    fun UsuarioCalendarioCita_to_UsuarioCalendario (fragment: Fragment) {
        fragment.findNavController().navigate(R.id.action_UsuarioCalendarioCita_to_UsuarioCalendario)
    }

    /* --- FRAGMENT USUARIO PERFIL USUARIO ---
    UsuarioPerfil_to_UsuarioInicio =>
    */
    fun UsuarioPerfil_to_UsuarioInicio (fragment: Fragment) {
        fragment.findNavController().navigate(R.id.action_UsuarioPerfilFragment_to_UsuarioInicioFragment)
    }

    /* --- FRAGMENT USUARIO CLINICAS ---
    UsuarioClinica_to_UsuarioInicio =>
    */
    fun UsuarioClinica_to_UsuarioInicio (fragment: Fragment) {
        fragment.findNavController().navigate(R.id.action_UsuarioClinicaFragment_to_UsuarioInicioFragment)
    }
}