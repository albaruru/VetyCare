package com.example.vetycare.navigation

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.vetycare.R

object NavigatorUsuario {

    /* NAVEGACION CONTAINER INICIO: desgloce por métodos
    *
    * */

    /* --- FRAGMENT DE INICIO ---
    * UsuarioInicioToUsuarioRegMascota          =>
    * UsuarioInicioToUsuarioMascota             =>
    * UsuarioInicioToUsuarioCalendario          =>
    * UsuarioInicioToUsuarioCalendarioCita      =>
    * */
    fun UsuarioInicioToUsuarioRegMascota (fragment: Fragment) {
        fragment.findNavController().navigate(R.id.action_UsuarioInicioFragment_to_UsuarioRegMascota)
    }
    fun UsuarioInicioToUsuarioMascota (fragment: Fragment) {
        fragment.findNavController().navigate(R.id.action_UsuarioInicioFragment_to_UsuarioMascotaFragment)
    }
    fun UsuarioInicioToUsuarioCalendario (fragment: Fragment) {
        fragment.findNavController().navigate(R.id.action_UsuarioInicioFragment_to_UsuarioCalendario)
    }
    fun UsuarioInicioToUsuarioCalendarioCita (fragment: Fragment) {
        fragment.findNavController().navigate(R.id.action_UsuarioInicioFragment_to_UsuarioCalendarioCita)
    }

    /* --- FRAGMENT PERFIL MASCOTAS ---
    * UsuarioMascotaToUsuarioInicio             =>
    * UsuarioMascotaToUsuarioRegMascota         =>
    * UsuarioMascotaToUsuarioCalendario         =>
    * UsuarioMascotaToUsuarioCalendarioCita     =>
    * */
    fun UsuarioMascotaToUsuarioInicio (fragment: Fragment) {
        fragment.findNavController().navigate(R.id.action_UsuarioMascotaFragment_to_UsuarioInicioFragment)
    }
    fun UsuarioMascotaToUsuarioRegMascota (fragment: Fragment) {
        fragment.findNavController().navigate(R.id.action_UsuarioMascotaFragment_to_UsuarioRegMascota)
    }
    fun UsuarioMascotaToUsuarioCalendario (fragment: Fragment) {
        fragment.findNavController().navigate(R.id.action_UsuarioMascotaFragment_to_UsuarioCalendario)
    }
    fun UsuarioMascotaToUsuarioCalendarioCita (fragment: Fragment) {
        fragment.findNavController().navigate(R.id.action_UsuarioMascotaFragment_to_UsuarioCalendarioCita)
    }

    /* --- FRAGMENT DE REGISTRO MASCOTAS ---
    * UsuarioRegMascotaToUsuarioInicio          =>
    * UsuarioRegMascotaToUsuarioMascota         =>
    * UsuarioRegMascotaToUsuarioCalendario      =>
    * UsuarioRegMascotaToUsuarioCalendarioCita  =>
    * */
    fun UsuarioRegMascotaToUsuarioInicio (fragment: Fragment) {
        fragment.findNavController().navigate(R.id.action_UsuarioRegMascota_to_UsuarioInicioFragment)
    }
    fun UsuarioRegMascotaToUsuarioMascota (fragment: Fragment) {
        fragment.findNavController().navigate(R.id.action_UsuarioRegMascota_to_UsuarioMascotaFragment)
    }
    fun UsuarioRegMascotaToUsuarioCalendario (fragment: Fragment) {
        fragment.findNavController().navigate(R.id.action_UsuarioRegMascota_to_UsuarioCalendario)
    }
    fun UsuarioRegMascotaToUsuarioCalendarioCita (fragment: Fragment) {
        fragment.findNavController().navigate(R.id.action_UsuarioRegMascota_to_UsuarioCalendarioCita)
    }

    /* --- FRAGMENT DEL CALENDARIO DE CITAS ---
    * UsuarioCalendarioToUsuarioInicio          =>
    * UsuarioCalendarioToUsuarioRegMascota      =>
    * UsuarioCalendarioToUsuarioMascota         =>
    * UsuarioCalendarioToUsuarioCalendarioCita  =>
    * */
    fun UsuarioCalendarioToUsuarioInicio (fragment: Fragment) {
        fragment.findNavController().navigate(R.id.action_UsuarioCalendario_to_UsuarioInicioFragment)
    }
    fun UsuarioCalendarioToUsuarioRegMascota (fragment: Fragment) {
        fragment.findNavController().navigate(R.id.action_UsuarioCalendario_to_UsuarioRegMascota)
    }
    fun UsuarioCalendarioToUsuarioMascota (fragment: Fragment) {
        fragment.findNavController().navigate(R.id.action_UsuarioCalendario_to_UsuarioMascotaFragment)
    }
    fun UsuarioCalendarioToUsuarioCalendarioCita (fragment: Fragment) {
        fragment.findNavController().navigate(R.id.action_UsuarioCalendario_to_UsuarioCalendarioCita)
    }

    /* --- FRAGMENT DEL CALENDARIO PARA AGENDAR CITAS ---
    * UsuarioCalendarioCitaToUsuarioInicio      =>
    * UsuarioCalendarioCitaToUsuarioRegMascota  =>
    * UsuarioCalendarioCitaToUsuarioMascota     =>
    * UsuarioCalendarioCitaToUsuarioCalendario  =>
    * */
    fun UsuarioCalendarioCitaToUsuarioInicio (fragment: Fragment) {
        fragment.findNavController().navigate(R.id.action_UsuarioCalendarioCita_to_UsuarioInicioFragment)
    }
    fun UsuarioCalendarioCitaToUsuarioRegMascota (fragment: Fragment) {
        fragment.findNavController().navigate(R.id.action_UsuarioCalendarioCita_to_UsuarioRegMascota)
    }
    fun UsuarioCalendarioCitaToUsuarioMascota (fragment: Fragment) {
        fragment.findNavController().navigate(R.id.action_UsuarioCalendarioCita_to_UsuarioMascotaFragment)
    }
    fun UsuarioCalendarioCitaToUsuarioCalendario (fragment: Fragment) {
        fragment.findNavController().navigate(R.id.action_UsuarioCalendarioCita_to_UsuarioCalendario)
    }
}