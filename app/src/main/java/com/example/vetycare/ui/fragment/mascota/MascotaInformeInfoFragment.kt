package com.example.vetycare.ui.fragment.mascota

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.example.vetycare.database.remote.MedicamentoRemote
import com.example.vetycare.database.remote.TratamientoRemote
import com.example.vetycare.database.repository.MedicamentoRepository
import com.example.vetycare.database.repository.TratamientoRepository
import com.example.vetycare.databinding.FragmentMascotaInformeInfoBinding
import com.example.vetycare.model.entities.Diagnostico
import com.example.vetycare.model.entities.Tratamiento
import com.example.vetycare.model.relational.MedicamentoPorTratamiento
import com.example.vetycare.navigation.NavigatorMascota
import com.example.vetycare.utils.FirebaseUtils
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

/* EXPLICACIÓN DE LA CLASE <MascotaInformeInfoFragment()> : despliega para leer...
    Fragmento encargado de mostrar el detalle completo de un informe médico seleccionado.
    Permite visualizar la valoración del veterinario, el importe y los datos técnicos,
    ofreciendo además acceso directo al tratamiento farmacológico asociado.
 */
class MascotaInformeInfoFragment : Fragment() {
    private var _binding: FragmentMascotaInformeInfoBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var tratamientoRepository: TratamientoRepository
    private lateinit var medicamentoRepository: MedicamentoRepository
    private var informeActual: Diagnostico? = null

    /* EXPLICACIÓN DEL METODO <onAttach()> : despliega para leer...
        Inicializa las referencias de Firebase y los repositorios de tratamientos y medicamentos.
        Garantiza que todas las capas de acceso a datos estén preparadas para realizar consultas
        relacionales en cuanto el fragmento se vincule con la actividad.
    */
    override fun onAttach(context: Context) {
        super.onAttach(context)

        firebaseDatabase = FirebaseDatabase.getInstance(FirebaseUtils.URL_RTDB)
        databaseReference = firebaseDatabase.reference

        val remoteTratamiento = TratamientoRemote(databaseReference)
        tratamientoRepository = TratamientoRepository(remoteTratamiento)

        val remoteMedicamento = MedicamentoRemote(databaseReference)
        medicamentoRepository = MedicamentoRepository(remoteMedicamento)
    }

    /* EXPLICACIÓN DEL METODO <onCreateView()> : despliega para leer...
        Infla la jerarquía de vistas del fragmento utilizando la clase de vinculación generada.
        Prepara el contenedor visual para mostrar la información del diagnóstico y devuelve
        la vista raíz necesaria para el ciclo de vida del componente.
    */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMascotaInformeInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    /* EXPLICACIÓN DEL METODO <onViewCreated()> : despliega para leer...
        Recupera el objeto diagnóstico pasado por argumentos y rellena los campos de la interfaz.
        Configura también el callback del botón físico de retroceso para asegurar que el usuario
        pueda volver al listado de informes de forma controlada y segura.
    */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        informeActual = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable("informe_key", Diagnostico::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getSerializable("informe_key") as? Diagnostico
        }

        informeActual?.let { pintarDatosInforme(it) }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navegacionFragment(2)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    /* EXPLICACIÓN DEL METODO <onResume()> : despliega para leer...
        Define los eventos de interacción para el botón de retorno y la consulta de tratamientos.
        Asegura que los listeners se mantengan activos cada vez que el usuario visualiza el
        detalle del informe para permitir una navegación fluida por el historial médico.
    */
    override fun onResume() {
        super.onResume()
        /* Acciones de los botones del fragment:
            Botón Volver => Navega al MascotaInformeFragment
            Botón Ver Tratamiento => Navega a dicho tratamiento
        */

        binding.btnVolver.setOnClickListener {
            navegacionFragment(1)
        }

        binding.btnVerTratamiento.setOnClickListener {
            abrirTratamientoDelInforme()
        }
    }

    /* EXPLICACIÓN DEL METODO <navegacionFragment()> : despliega para leer...
        Centraliza el flujo de salida hacia la pantalla del listado general de informes médicos.
        Utiliza el navegador específico del módulo de mascotas para cerrar la vista de detalle
        y retornar a la pantalla anterior del flujo de navegación de la aplicación.
    */
    fun navegacionFragment(num: Int) {
        when (num) {
            1 -> NavigatorMascota.MascotaInformeInfo_to_MascotaInforme(this)
            2 -> NavigatorMascota.MascotaInformeInfo_to_MascotaInforme(this@MascotaInformeInfoFragment)
        }
    }

    /* EXPLICACIÓN DEL METODO <pintarDatosInforme()> : despliega para leer...
        Vincula las propiedades del diagnóstico (patología, valoración, importe) con las vistas del layout.
        Asigna dinámicamente los textos a los componentes visuales y formatea el importe total para
        mostrarlo correctamente junto al símbolo de la moneda.
    */
    @SuppressLint("SetTextI18n")
    private fun pintarDatosInforme(i: Diagnostico) {
        binding.tvTitulo.text = "Informe #${i.id?.uppercase()}"
        binding.tvConcepto.text = i.patologia?.nombre ?: "Sin concepto"
        binding.tvFecha.text = i.fechaDiagnostico ?: "N/A"
        binding.tvValoracion.text = i.valoracion ?: "N/A"
        binding.tvMedicamento.text = i.medicamento?.nombreComercial ?: "N/A"
        binding.tvTratamiento.text = i.tratamiento?.tipoTratamiento ?: "N/A"
        binding.tvImporte.text = "${i.importeTotal ?: 0.0} €"
    }

    /* EXPLICACIÓN DEL METODO <abrirTratamientoDelInforme()> : despliega para leer...
        Inicia la consulta al repositorio para obtener el registro del tratamiento asociado al informe.
        Valida la existencia de un identificador de tratamiento antes de proceder con la carga
        completa de la medicación y las pautas sanitarias relacionadas.
    */
    private fun abrirTratamientoDelInforme() {
        val informe = informeActual
        val idTratamiento = informe?.idTratamiento

        if (idTratamiento.isNullOrEmpty()) {
            mostrarToast("Este informe no tiene tratamiento asociado")
            return
        }

        tratamientoRepository.obtenerTratamientoPorId(
            idTratamiento = idTratamiento,
            success = { tratamientoBase ->
                cargarTratamientoCompleto(idTratamiento, tratamientoBase)
            },
            error = { mensaje ->
                mostrarToast(mensaje ?: "Error al cargar el tratamiento")
            }
        )
    }

    /* EXPLICACIÓN DEL METODO <cargarTratamientoCompleto()> : despliega para leer...
        Coordina múltiples llamadas al repositorio para consolidar los datos de medicación y pautas.
        Una vez estructurada la información, ejecuta la navegación hacia la pantalla de detalle
        de tratamiento, pasando el objeto completo con sus detalles técnicos.
    */
    private fun cargarTratamientoCompleto(
        idTratamiento: String,
        tratamientoBase: Tratamiento
    ) {
        tratamientoRepository.obtenerMedicamentosPorTratamiento(
            idTratamiento = idTratamiento,
            success = { snapshot ->
                val nodoMedicacion = snapshot.children.firstOrNull()
                val idMedicamento = nodoMedicacion?.key

                val detalles = if (nodoMedicacion != null) {
                    MedicamentoPorTratamiento(
                        dosis = nodoMedicacion.child("dosis").getValue(String::class.java),
                        duracion = nodoMedicacion.child("duracion").getValue(String::class.java),
                        fechaFin = nodoMedicacion.child("fechaFin").getValue(String::class.java),
                        fechaInicio = nodoMedicacion.child("fechaInicio")
                            .getValue(String::class.java),
                        frecuencia = nodoMedicacion.child("frecuencia")
                            .getValue(String::class.java),
                        indicaciones = nodoMedicacion.child("indicaciones")
                            .getValue(String::class.java),
                        viaAdministracion = nodoMedicacion.child("viaAdministracion")
                            .getValue(String::class.java)
                    )
                } else {
                    MedicamentoPorTratamiento()
                }

                if (!idMedicamento.isNullOrEmpty()) {
                    medicamentoRepository.obtenerMedicamentoPorId(
                        idMedicamento = idMedicamento,
                        success = { medicamento ->
                            val tratamientoCompleto = tratamientoBase.copy(
                                id = idTratamiento,
                                medicamento = medicamento,
                                detallesMedicacion = detalles
                            )

                            NavigatorMascota.MascotaInformeInfo_to_MascotaTratamientoInfo(
                                this,
                                tratamientoCompleto
                            )
                        },
                        error = { mensaje ->
                            mostrarToast(mensaje ?: "Error al cargar el medicamento")
                        }
                    )
                } else {
                    val tratamientoCompleto = tratamientoBase.copy(
                        id = idTratamiento,
                        detallesMedicacion = detalles
                    )

                    NavigatorMascota.MascotaInformeInfo_to_MascotaTratamientoInfo(
                        this,
                        tratamientoCompleto
                    )
                }
            },
            error = { mensaje ->
                mostrarToast(mensaje ?: "Error al cargar la medicación del tratamiento")
            }
        )
    }

    /* EXPLICACIÓN DEL METODO <mostrarToast()> : despliega para leer...
        Muestra un mensaje emergente breve en la pantalla para informar al usuario sobre eventos del sistema.
        Se utiliza para notificar la ausencia de tratamientos vinculados o posibles errores
        de conexión durante la descarga de información desde el repositorio.
    */
    private fun mostrarToast(mensaje: String) {
        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show()
    }

    /* EXPLICACIÓN DEL METODO <onDestroyView()> : despliega para leer...
        Anula la referencia al objeto binding cuando la vista del fragmento es destruida por el sistema.
        Esta operación previene fugas de memoria, garantizando que los recursos de la interfaz
        de usuario se liberen correctamente al finalizar el ciclo de vida de la vista.
    */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}