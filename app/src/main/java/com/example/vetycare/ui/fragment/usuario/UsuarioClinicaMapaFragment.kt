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
import com.example.vetycare.database.remote.ClinicaRemote
import com.example.vetycare.database.repository.ClinicaRepository
import com.example.vetycare.databinding.FragmentUsuarioClinicaMapaBinding
import com.example.vetycare.model.entities.Clinica
import com.example.vetycare.navigation.NavigatorUsuario
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.generated.OnPointAnnotationClickListener
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import androidx.core.graphics.createBitmap

class UsuarioClinicaMapaFragment : Fragment() {

    private lateinit var binding: FragmentUsuarioClinicaMapaBinding
    private lateinit var clinicaRepository: ClinicaRepository
    private lateinit var pointAnnotationManager: com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
    private val marcadoresClinica = mutableMapOf<String, Clinica>()

    override fun onAttach(context: Context) {
        super.onAttach(context)

        val firebaseDatabase = com.google.firebase.database.FirebaseDatabase
            .getInstance(com.example.vetycare.utils.FirebaseUtils.URL_RTDB)
        val databaseReference = firebaseDatabase.reference

        val remoteClinica = ClinicaRemote(databaseReference)
        clinicaRepository = ClinicaRepository(remoteClinica)
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
        iniciarMapa()
    }

    override fun onResume() {
        super.onResume()
        binding.btnVerListado.setOnClickListener {
            navegacionFragment(1)
        }
    }

    private fun iniciarMapa() {
        binding.mapView.mapboxMap.loadStyle(Style.MAPBOX_STREETS) {
            binding.mapView.mapboxMap.setCamera(
                CameraOptions.Builder()
                    .center(Point.fromLngLat(-4.0, 40.0))
                    .zoom(5.5)
                    .build()
            )
            pointAnnotationManager = binding.mapView.annotations.createPointAnnotationManager()
            pointAnnotationManager.addClickListener(OnPointAnnotationClickListener { annotation ->
                val clinica = marcadoresClinica[annotation.id.toString()]
                clinica?.let {
                    mostrarBottomSheet(
                        nombre = it.nombre ?: "",
                        provincia = it.provincia ?: "",
                        direccion = it.direccion ?: "",
                        telefono = it.telefono?.toString() ?: ""
                    )
                }
                true
            })
            cargarClinicasEnMapa()
        }
    }

    private fun cargarClinicasEnMapa() {
        clinicaRepository.obtenerTodasLasClinicasActivas(
            onSuccess = { lista ->
                marcadoresClinica.clear()
                pointAnnotationManager.deleteAll()

                val drawable = requireContext().getDrawable(R.drawable.chincheta_vetycare)
                if (drawable == null) {
                    Toast.makeText(requireContext(), "No se pudo cargar el icono del marcador", Toast.LENGTH_SHORT).show()
                    return@obtenerTodasLasClinicasActivas
                }

                val bitmap = drawableToBitmap(drawable)

                lista.forEach { clinica ->
                    val latitud = clinica.coordenadas?.latitud ?: return@forEach
                    val longitud = clinica.coordenadas?.longitud ?: return@forEach

                    val annotation = pointAnnotationManager.create(
                        PointAnnotationOptions()
                            .withPoint(Point.fromLngLat(longitud, latitud))
                            .withIconImage(bitmap)
                            .withIconSize(0.10)
                    )

                    marcadoresClinica[annotation.id.toString()] = clinica
                }

                pointAnnotationManager.addClickListener(OnPointAnnotationClickListener { annotation ->
                    val clinica = marcadoresClinica[annotation.id.toString()]
                    clinica?.let {
                        mostrarBottomSheet(
                            nombre = it.nombre ?: "",
                            provincia = it.provincia ?: "",
                            direccion = it.direccion ?: "",
                            telefono = it.telefono?.toString() ?: ""
                        )
                    }
                    true
                })
            },
            { mensajeDeError ->
                Toast.makeText(requireContext(), mensajeDeError ?: "ERROR al cargar clínicas", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun mostrarBottomSheet(
        nombre: String, provincia: String,
        direccion: String, telefono: String
        ) {
        val bottomSheet = BottomSheetDialog(requireContext())
        val sheetView = layoutInflater.inflate(R.layout.recycler_clinica, null)

        sheetView.findViewById<TextView>(R.id.tv_nombre_clinica).text = nombre
        sheetView.findViewById<TextView>(R.id.tv_provincia).text = provincia
        sheetView.findViewById<TextView>(R.id.tv_direccion).text = direccion
        sheetView.findViewById<TextView>(R.id.tv_telefono).text = telefono

        bottomSheet.setContentView(sheetView)
        bottomSheet.show()
    }

    private fun drawableToBitmap(drawable: android.graphics.drawable.Drawable): android.graphics.Bitmap {
        val bitmap = createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight)
        val canvas = android.graphics.Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    fun navegacionFragment(num: Int) {
        when (num) {
            1 -> NavigatorUsuario.UsuarioClinicaMapa_to_UsuarioClinica(this)
        }
    }

    override fun onStart() { super.onStart(); binding.mapView.onStart() }
    override fun onStop() { super.onStop(); binding.mapView.onStop() }
    override fun onLowMemory() { super.onLowMemory(); binding.mapView.onLowMemory() }
    override fun onDestroyView() {
        super.onDestroyView()
        binding.mapView.onDestroy()
    }
}