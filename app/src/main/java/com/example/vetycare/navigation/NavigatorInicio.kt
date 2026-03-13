package com.example.vetycare.navigation

import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.vetycare.R

object NavigatorInicio {

    /* NAVEGACION CONTAINER INICIO: desgloce por métodos

    * - InicioPrincipalToInicioRegistro      => Navegamos del Fragment Principal                => Fragment Registro Usuario
    * - InicioPrincipalToInicioRecPass       => Navegamos del Fragment Principal                => Fragment Recuperacion Contraseña
    *
    * - InicioRecPassToInicioPrincipal       => Navegamos del Fragment Recuperacion Contraseña  => Fragment Principal
    *
    * - InicioRegistroToConfirmacionDialog   => Navegamos del Fragment Registro Usuario         => Dialog Confirmacion
    * - DialogConfirmacionToInicioPrincipal  => Navegamos del Dialog Confirmacion               => Fragment Principal
    *
    * - InicioRegistroToCancelacionDialog    => Navegamos del Fragment Registro Usuario         => Dialog Cancelacion
    * - DialogCancelacionToInicioPrincipal   => Navegamos del Dialog Cancelacion                => Fragment Principal
    * */

    fun InicioPrincipalToInicioRegistro (fragment: Fragment) {
        fragment.findNavController().navigate(R.id.action_InicioPrincipalFragment_to_InicioRegistroFragment)
    }
    fun InicioPrincipalToInicioRecPass (fragment: Fragment) {
        fragment.findNavController().navigate(R.id.action_InicioPrincipalFragment_to_InicioRecPassFragment)
    }
    fun InicioRecPassToInicioPrincipal (fragment: Fragment) {
        fragment.findNavController().navigate(R.id.action_InicioRecPassFragment_to_InicioPrincipalFragment)
    }
    fun InicioRegistroToConfirmacionDialog (fragment: Fragment) {
        fragment.findNavController().navigate(R.id.action_InicioRegistroFragment_to_confirmacionDialog)
    }
    fun DialogConfirmacionToInicioPrincipal (fragment: Fragment) {
        fragment.findNavController().navigate(R.id.action_confirmacionDialog_to_InicioPrincipalFragment)
    }
    fun InicioRegistroToCancelacionDialog (fragment: Fragment) {
        fragment.findNavController().navigate(R.id.action_InicioRegistroFragment_to_cancelacionDialog)
    }
    fun DialogCancelacionToInicioPrincipal (fragment: Fragment) {
        fragment.findNavController().navigate(R.id.action_cancelacionDialog_to_InicioPrincipalFragment)
    }
}