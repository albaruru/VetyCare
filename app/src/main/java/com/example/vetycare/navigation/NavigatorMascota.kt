package com.example.vetycare.navigation

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.vetycare.R
import com.example.vetycare.model.entities.Diagnostico
import com.example.vetycare.model.entities.Tratamiento

object NavigatorMascota {

    /* NAVEGACIÓN CONTAINER MASCOTA:
    Gestiona la navegación del módulo de Mascota, centralizando los flujos entre fragments
    y el paso de datos necesarios mediante Bundle.

    Desglose por métodos:

    - MascotaCita_to_MascotaPerfil                   => Navegamos del Fragment Cita              => Fragment Mascota Perfil

    - MascotaInformeInfo_to_MascotaInforme           => Navegamos del Fragment Informe Info      => Fragment Informe
    - MascotaInforme_to_MascotaInformeInfo           => Navegamos del Fragment Informe           => Fragment Informe Info
    - MascotaInforme_to_MascotaPerfil                => Navegamos del Fragment Informe           => Fragment Mascota Perfil

    - MascotaTratamientoInfo_to_MascotaTratamiento   => Navegamos del Fragment Tratamiento Info  => Fragment Tratamiento
    - MascotaTratamiento_to_MascotaTratamientoInfo   => Navegamos del Fragment Tratamiento       => Fragment Tratamiento Info
    - MascotaTratamiento_to_MascotaPerfil            => Navegamos del Fragment Tratamiento       => Fragment Mascota Perfil

    */

    /* --- NAVEGACIÓN DE CITAS --- */
    fun MascotaCita_to_MascotaPerfil(fragment: Fragment) {
        fragment.findNavController()
            .navigate(R.id.action_MascotaCitaFragment_to_MascotaPerfilFragment)
    }

    /* --- NAVEGACIÓN DE INFORMES --- */
    fun MascotaInformeInfo_to_MascotaInforme(fragment: Fragment) {
        fragment.findNavController()
            .navigate(R.id.action_MascotaInformeInfoFragment_to_MascotaInformeFragment)
    }

    fun MascotaInforme_to_MascotaInformeInfo(fragment: Fragment, informe: Diagnostico? = null) {
        val bundle = Bundle()
        if (informe != null) {
            bundle.putSerializable("informe_key", informe)
        }

        fragment.findNavController().navigate(
            R.id.action_MascotaInformeFragment_to_MascotaInformeInfoFragment, bundle
        )
    }
    fun MascotaInforme_to_MascotaPerfil(fragment: Fragment) {
        fragment.findNavController()
            .navigate(R.id.action_MascotaInformeFragment_to_MascotaPerfilFragment)
    }

    /* --- NAVEGACIÓN DE TRATAMIENTOS --- */
    fun MascotaTratamientoInfo_to_MascotaTratamiento(fragment: Fragment) {
        fragment.findNavController()
            .navigate(R.id.action_MascotaTratamientoInfoFragment_to_MascotaTratamientoFragment)
    }

    fun MascotaTratamiento_to_MascotaTratamientoInfo(fragment: Fragment, tratamiento: Tratamiento? = null) {
        val bundle = Bundle()
        if (tratamiento != null) {
            bundle.putSerializable("tratamiento_key", tratamiento)
        }

        fragment.findNavController().navigate(
            R.id.action_MascotaTratamientoFragment_to_MascotaTratamientoInfoFragment, bundle
        )
    }

    fun MascotaInformeInfo_to_MascotaTratamientoInfo(
        fragment: Fragment,
        tratamiento: Tratamiento
    ) {
        val bundle = Bundle().apply {
            putSerializable("tratamiento_key", tratamiento)
        }

        fragment.findNavController().navigate(
            R.id.action_MascotaInformeInfoFragment_to_MascotaTratamientoInfoFragment,
            bundle
        )
    }

    fun MascotaTratamiento_to_MascotaPerfil(fragment: Fragment) {
        fragment.findNavController()
            .navigate(R.id.action_MascotaTratamientoFragment_to_MascotaPerfilFragment)
    }
}