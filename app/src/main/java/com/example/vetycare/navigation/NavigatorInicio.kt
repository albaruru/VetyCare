package com.example.vetycare.navigation

import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.vetycare.R

object NavigatorInicio {

    /* NAVEGACIÓN CONTAINER INICIO:
        Gestiona la navegación del módulo de Inicio.
        Centraliza las transiciones entre fragments para evitar duplicación de código
        y mantener un flujo de navegación más organizado y mantenible.

    Desglose por métodos:

    - InicioPrincipal_to_InicioRegistro   => Navegamos del Fragment Principal                => Fragment Registro Usuario
    - InicioPrincipal_to_InicioRecPass    => Navegamos del Fragment Principal                => Fragment Recuperacion Contraseña

    - InicioRecPass_to_InicioPrincipal    => Navegamos del Fragment Recuperacion Contraseña  => Fragment Principal
    - InicioRegistro_to_InicioPrincipal   => Navegamos del Fragment Registro Usuario         => Fragment Principal

    */

    fun InicioPrincipal_to_InicioRegistro (fragment: Fragment) {
        fragment.findNavController().navigate(R.id.action_InicioPrincipalFragment_to_InicioRegistroFragment)
    }
    fun InicioPrincipal_to_InicioRecPass (fragment: Fragment) {
        fragment.findNavController().navigate(R.id.action_InicioPrincipalFragment_to_InicioRecPassFragment)
    }
    fun InicioRecPass_to_InicioPrincipal (fragment: Fragment) {
        fragment.findNavController().navigate(R.id.action_InicioRecPassFragment_to_InicioPrincipalFragment)
    }
    fun InicioRegistro_to_InicioPrincipal (fragment: Fragment) {
        fragment.findNavController().navigate(R.id.action_InicioRegistroFragment_to_InicioPrincipalFragment)
    }
}