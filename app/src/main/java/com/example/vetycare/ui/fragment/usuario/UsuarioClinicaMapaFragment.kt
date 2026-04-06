package com.example.vetycare.ui.fragment.usuario

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.vetycare.R
import com.example.vetycare.databinding.FragmentUsuarioClinicaMapaBinding
import com.example.vetycare.navigation.NavigatorUsuario
import com.google.android.material.bottomsheet.BottomSheetDialog
/* DESCOMENTAR CUANDO ESTEMOS EN LA RAMA DATABASE
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.OnPointAnnotationClickListener
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager*/

class UsuarioClinicaMapaFragment : Fragment() {

    private lateinit var binding: FragmentUsuarioClinicaMapaBinding
    /* -->DESCOMENTAR EN DATABASE CUANDO SE HAGA MERGE
        private val db = Firebase.database.reference
        private val datosClinicas = mutableMapOf<String, Map<String, String>>()
     */


    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUsuarioClinicaMapaBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //iniciarMapa() --> DESCOMENTAR EN DATABASE CUANDO SE HAGA MERGE
    }

    override fun onResume() {
        super.onResume()
        binding.btnVerListado.setOnClickListener {
            navegacionFragment(1)
        }
    }
    /* DESCOMENTAR EN DATABASE CUANDO SE HAGA MERGE
    private fun iniciarMapa() {
        binding.mapView.mapboxMap.loadStyle(Style.MAPBOX_STREETS) {
            binding.mapView.mapboxMap.setCamera(
                CameraOptions.Builder()
                    .center(Point.fromLngLat(-4.0, 40.0))
                    .zoom(5.5)
                    .build()
            )
        }
    }

    private fun cargarClinicas() {
        db.child("clinicas").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val pointAnnotationManager =
                    binding.mapView.annotations.createPointAnnotationManager()

                for (clinica in snapshot.children) {
                    val activa = clinica.child("activa").getValue(Boolean::class.java) ?: false
                    if (!activa) continue

                    val latitud = clinica.child("coordenadas")
                        .child("latitud").getValue(Double::class.java) ?: continue
                    val longitud = clinica.child("coordenadas")
                        .child("longitud").getValue(Double::class.java) ?: continue

                    val nombre = clinica.child("nombre").getValue(String::class.java) ?: ""
                    val provincia = clinica.child("provincia").getValue(String::class.java) ?: ""
                    val direccion = clinica.child("direccion").getValue(String::class.java) ?: ""
                    val telefono = clinica.child("telefono").getValue(Long::class.java)?.toString() ?: ""

                    val clave = "$latitud,$longitud"
                    datosClinicas[clave] = mapOf(
                        "nombre" to nombre,
                        "provincia" to provincia,
                        "direccion" to direccion,
                        "telefono" to telefono
                    )

                    val marcador = PointAnnotationOptions()
                        .withPoint(Point.fromLngLat(longitud, latitud))

                    pointAnnotationManager.create(marcador)
                }

                pointAnnotationManager.addClickListener(OnPointAnnotationClickListener { annotation ->
                    val clave = "${annotation.point.latitude()},${annotation.point.longitude()}"
                    datosClinicas[clave]?.let { datos ->
                        mostrarBottomSheet(
                            nombre = datos["nombre"] ?: "",
                            provincia = datos["provincia"] ?: "",
                            direccion = datos["direccion"] ?: "",
                            telefono = datos["telefono"] ?: ""
                        )
                    }
                    true
                })
            }

            override fun onCancelled(error: DatabaseError) {
                //para crear mensaje de error

            }
        })
    }
    */

    /*
    private fun mostrarBottomSheet(
        nombre: String, provincia: String,
        direccion: String, telefono: String
    ) {
        val bottomSheet = BottomSheetDialog(requireContext())
        val sheetView = layoutInflater.inflate(R.layout.recycler_clinica, null)

        sheetView.findViewById<TextView>(R.id.tv_nombre_clinica).text = nombre
        sheetView.findViewById<TextView>(R.id.tv_ciudad).text = provincia
        sheetView.findViewById<TextView>(R.id.tv_direccion).text = direccion
        sheetView.findViewById<TextView>(R.id.tv_telefono).text = telefono

        bottomSheet.setContentView(sheetView)
        bottomSheet.show()
    }
    */

    fun navegacionFragment(num: Int) {
        when (num) {
            1 -> NavigatorUsuario.UsuarioClinicaMapa_to_UsuarioClinica(this)
        }
    }
    /* DESCOMENTAR EN DATABASE CUANDO SE HAGA MERGE
    override fun onStart() { super.onStart(); binding.mapView.onStart() }
    override fun onStop() { super.onStop(); binding.mapView.onStop() }
    override fun onLowMemory() { super.onLowMemory(); binding.mapView.onLowMemory() }
    override fun onDestroyView() {
        super.onDestroyView()
        binding.mapView.onDestroy()
    }*/
}