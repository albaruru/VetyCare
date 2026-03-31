package com.example.vetycare.ui.container


import android.media.Image
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.example.vetycare.R
import com.example.vetycare.navigation.NavigatorRoot

class MascotaContainerFragment : Fragment (R.layout.fragment_container_mascota) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navHostFragment = childFragmentManager
            .findFragmentById(R.id.container_host_mascota) as NavHostFragment

        val navController = navHostFragment.navController

        val botonCita = view?.findViewById<ImageButton>(R.id.btnCita)
        val botonTratamiento = view?.findViewById<ImageButton>(R.id.btnTratamiento)
        val botonInforme = view?.findViewById<ImageButton>(R.id.btnInforme)
        val botonMascota = view?.findViewById<ImageButton>(R.id.btnMascotas)
        val botonRegresar = view?.findViewById<ImageButton>(R.id.btnRegresar)
        val botonVetyCare = view?.findViewById<ImageButton>(R.id.iv_logo)

        botonCita?.setOnClickListener {
            navController.navigate(R.id.MascotaCitaFragment)
        }
        botonTratamiento?.setOnClickListener {
            navController.navigate(R.id.MascotaTratamientoFragment)
        }
        botonInforme?.setOnClickListener {
            navController.navigate(R.id.MascotaInformeFragment)
        }
        botonMascota?.setOnClickListener {
            navController.navigate(R.id.MascotaPerfilFragment)
        }
        botonRegresar?.setOnClickListener {
            NavigatorRoot.Mascota_to_Usuario(this)
        }
        botonVetyCare?.setOnClickListener {
            NavigatorRoot.Mascota_to_Usuario(this)
        }
    }
}