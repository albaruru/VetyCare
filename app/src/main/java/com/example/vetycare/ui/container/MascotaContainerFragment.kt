package com.example.vetycare.ui.container

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.example.vetycare.R
import com.example.vetycare.database.remote.MascotaRemote
import com.example.vetycare.database.repository.MascotaRepository
import com.example.vetycare.model.entities.Mascota
import com.example.vetycare.navigation.NavigatorRoot
import com.example.vetycare.utils.FirebaseUtils
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MascotaContainerFragment : Fragment (R.layout.fragment_container_mascota) {

    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var mascotaRepository: MascotaRepository
    private var mascotaSeleccionada: Mascota? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        auth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance(FirebaseUtils.URL_RTDB)
        databaseReference = firebaseDatabase.reference

        val remoteMascota = MascotaRemote(databaseReference)
        mascotaRepository = MascotaRepository(remoteMascota)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mascotaSeleccionada = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable(ARG_MASCOTA, Mascota::class.java)
        }
        else {
            @Suppress("DEPRECATION")
            arguments?.getSerializable(ARG_MASCOTA) as? Mascota
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navHostFragment = childFragmentManager
            .findFragmentById(R.id.container_host_mascota) as NavHostFragment

        val navController = navHostFragment.navController

        val ivFotoMascota = view.findViewById<ShapeableImageView>(R.id.iv_foto_mascota)
        val tvNombreMascota = view.findViewById<TextView>(R.id.tv_nombre_mascota)

        cargarDatosMascota(ivFotoMascota, tvNombreMascota)

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

        navController.addOnDestinationChangedListener { _, destination, _ ->
            botonMascota?.setImageResource(R.drawable.btn_huella)
            botonCita?.setImageResource(R.drawable.btn_calendar)
            botonTratamiento?.setImageResource(R.drawable.btn_tratamiento)
            botonInforme?.setImageResource(R.drawable.btn_informes)
            botonRegresar?.setImageResource(R.drawable.btn_regresa)

            when (destination.id) {
                R.id.MascotaPerfilFragment -> {
                    botonMascota?.setImageResource(R.drawable.btn_huella_black)
                }
                R.id.MascotaCitaFragment -> {
                    botonCita?.setImageResource(R.drawable.btn_calendar_black)
                }
                R.id.MascotaTratamientoFragment -> {
                    botonTratamiento?.setImageResource(R.drawable.btn_tratamiento_black)
                }
                R.id.MascotaInformeFragment -> {
                    botonInforme?.setImageResource(R.drawable.btn_informes_black)
                }
            }
        }

    }

    private fun cargarDatosMascota(
        ivFotoMascota: ShapeableImageView,
        tvNombreMascota: TextView
    ) {
        val mascota = mascotaSeleccionada ?: return

        tvNombreMascota.text = mascota.nombre ?: "Mascota"

        if (!mascota.urlFotoMasc.isNullOrEmpty()) {
            Glide.with(this)
                .load(mascota.urlFotoMasc)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(ivFotoMascota)
        } else {
            ivFotoMascota.setImageResource(android.R.drawable.ic_menu_camera)
        }
    }

    companion object {
        const val ARG_MASCOTA = "arg_mascota"
    }
}