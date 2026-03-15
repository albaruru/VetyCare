package com.example.vetycare.navigation

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.vetycare.R

object NavigatorMascota {

    /* NAVEGACIÓN CONTAINER MASCOTA: desglose por métodos

    - MascotaInformeInfo_to_MascotaInforme           => Navegamos del Fragment Informe Info      => Fragment Informe
    - MascotaInforme_to_MascotaInformeInfo           => Navegamos del Fragment Informe           => Fragment Informe Info

    - MascotaTratamientoInfo_to_MascotaTratamiento   => Navegamos del Fragment Tratamiento Info  => Fragment Tratamiento
    - MascotaTratamiento_to_MascotaTratamientoInfo   => Navegamos del Fragment Tratamiento       => Fragment Tratamiento Info

    */

    /* --- NAVEGACIÓN DE INFORMES --- */
    fun MascotaInformeInfo_to_MascotaInforme(fragment: Fragment) {
        fragment.findNavController().navigate(R.id.action_MascotaInformeInfoFragment_to_MascotaInformeFragment)
    }
    fun MascotaInforme_to_MascotaInformeInfo(fragment: Fragment) {
        fragment.findNavController().navigate(R.id.action_MascotaInformeFragment_to_MascotaInformeInfoFragment)
    }

    /* --- NAVEGACIÓN DE TRATAMIENTOS --- */
    fun MascotaTratamientoInfo_to_MascotaTratamiento(fragment: Fragment) {
        fragment.findNavController().navigate(R.id.action_MascotaTratamientoInfoFragment_to_MascotaTratamientoFragment)
    }
    fun MascotaTratamiento_to_MascotaTratamientoInfo(fragment: Fragment) {
        fragment.findNavController().navigate(R.id.action_MascotaTratamientoFragment_to_MascotaTratamientoInfoFragment)
    }
}