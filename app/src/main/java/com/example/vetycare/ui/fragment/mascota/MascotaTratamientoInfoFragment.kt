package com.example.vetycare.ui.fragment.mascota

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.example.vetycare.databinding.FragmentMascotaTratamientoInfoBinding
import com.example.vetycare.model.entities.Tratamiento
import com.example.vetycare.navigation.NavigatorMascota

/* EXPLICACIÓN DE LA CLASE <MascotaTratamientoInfoFragment()> : despliega para leer...
    Fragmento especializado en mostrar la ficha técnica detallada de un tratamiento médico.
    Visualiza información sobre el procedimiento, la medicación asignada, dosis, frecuencia
    y vías de administración para que el propietario tenga una guía clara del cuidado.
 */
class MascotaTratamientoInfoFragment : Fragment() {
    private lateinit var _binding : FragmentMascotaTratamientoInfoBinding
    private val binding get() = _binding

    /* EXPLICACIÓN DEL METODO <onAttach()> : despliega para leer...
        Se ejecuta al vincular el fragmento con su actividad anfitriona para realizar preparaciones.
        Garantiza que el contexto esté disponible para futuras operaciones de inicialización y
        configuración de servicios antes de que se cree la vista.
    */
    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    /* EXPLICACIÓN DEL METODO <onCreateView()> : despliega para leer...
        Infla el diseño XML del fragmento utilizando la clase de vinculación de vistas generada.
        Prepara los componentes visuales de la interfaz y devuelve la vista raíz para que
        el sistema pueda renderizarla en la pantalla del dispositivo.
    */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentMascotaTratamientoInfoBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    /* EXPLICACIÓN DEL METODO <onResume()> : despliega para leer...
        Configura los escuchadores de eventos para los botones de la interfaz en primer plano.
        Activa la funcionalidad del botón de retorno para permitir al usuario descartar la
        vista de información y volver al listado general de tratamientos.
    */
    override fun onResume() {
        super.onResume()
        /* Acciones de los botones del fragment:
            Botón Volver => Navega al MascotaTratamientoFragment
        */

        binding.btnVolver.setOnClickListener {
            navegacionFragment(1)
        }
    }

    /* EXPLICACIÓN DEL METODO <navegacionFragment()> : despliega para leer...
        Centraliza el flujo de salida hacia la pantalla del listado general de tratamientos.
        Utiliza el navegador específico del módulo de mascotas para realizar la transición,
        asegurando una navegación consistente y controlada dentro de la aplicación.
    */
    fun navegacionFragment(num: Int) {
        when (num) {
            1 -> NavigatorMascota.MascotaTratamientoInfo_to_MascotaTratamiento(this)
            2 -> NavigatorMascota.MascotaTratamientoInfo_to_MascotaTratamiento(this@MascotaTratamientoInfoFragment)
        }
    }

    /* EXPLICACIÓN DEL METODO <onViewCreated()> : despliega para leer...
        Recupera el objeto tratamiento de los argumentos y activa la lógica de visualización.
        Configura el dispatcher del botón de retroceso nativo de Android para redirigir al
        usuario al listado de tratamientos de forma segura y evitar el cierre del flujo.
    */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tratamiento = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable("tratamiento_key", Tratamiento::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getSerializable("tratamiento_key") as? Tratamiento
        }

        if (tratamiento != null) {
            pintarDatosTratamiento(tratamiento)
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navegacionFragment(2)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    /* EXPLICACIÓN DEL METODO <pintarDatosTratamiento()> : despliega para leer...
        Asigna dinámicamente los atributos de la entidad tratamiento a los TextViews del layout.
        Mapea el ID, fechas, observaciones y pautas de medicación (dosis y frecuencia) para
        componer la información técnica completa en la interfaz de usuario.
    */
    private fun pintarDatosTratamiento(t: Tratamiento) {
        binding.tvTitulo.text = "Tratamiento #${t.id?.uppercase()}"
        binding.tvProcedimiento.text = t.tipoTratamiento
        binding.tvFechaInicio.text = t.fechaInicio
        binding.tvFechaFin.text = t.fechaFin
        binding.tvObservaciones.text = t.observaciones
        binding.tvMedicacion.text = t.medicamento?.nombreComercial ?: "No especificado"
        binding.tvDosis.text = t.detallesMedicacion?.dosis ?: "N/A"
        binding.tvFrecuencia.text = t.detallesMedicacion?.frecuencia ?: "N/A"
        binding.tvViaAdministracion.text = t.detallesMedicacion?.viaAdministracion ?: "N/A"
    }
}