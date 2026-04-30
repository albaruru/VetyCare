package com.example.vetycare.ui.fragment.mascota

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.example.vetycare.R
import com.example.vetycare.database.remote.CitaRemote
import com.example.vetycare.database.remote.ClinicaRemote
import com.example.vetycare.database.remote.VeterinarioRemote
import com.example.vetycare.database.repository.CitaRepository
import com.example.vetycare.database.repository.ClinicaRepository
import com.example.vetycare.database.repository.VeterinarioRepository
import com.example.vetycare.databinding.FragmentMascotaCitaBinding
import com.example.vetycare.model.entities.Cita
import com.example.vetycare.model.entities.Clinica
import com.example.vetycare.model.entities.Veterinario
import com.example.vetycare.model.relational.CitaClinica
import com.example.vetycare.model.relational.CitaMascota
import com.example.vetycare.model.relational.CitaVeterinario
import com.example.vetycare.navigation.NavigatorMascota
import com.example.vetycare.ui.container.MascotaContainerFragment
import com.example.vetycare.ui.dialog.ConfirmacionDialog
import com.example.vetycare.utils.mostrarSnackbar
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/* EXPLICACIÓN DE LA CLASE <MascotaCitaFragment()> : despliega para leer...
    Fragmento encargado de la gestión y solicitud de nuevas citas médicas para una mascota.
    Permite filtrar clínicas por comunidad, seleccionar veterinarios disponibles y
    programar la fecha y hora de la consulta sincronizando los datos con Firebase.
 */
class MascotaCitaFragment : Fragment() {

    private var _binding: FragmentMascotaCitaBinding? = null
    private val binding get() = _binding!!
    private val keyConfirmacion = "confirmacion_cita"

    /* EXPLICACIÓN DE LAS VARIABLES <Repository> : despliega para leer...
        Instancias de los repositorios encargados de la comunicación con Firebase Realtime Database.
        Gestionan la obtención de clínicas por comunidad, veterinarios por clínica y el registro
        final de la cita médica mediante el patrón Repository.
     */
    private val databaseRef by lazy { FirebaseDatabase.getInstance().reference }
    private val citaRepository by lazy {
        CitaRepository(CitaRemote(databaseRef))
    }
    private val clinicaRepository by lazy {
        ClinicaRepository(ClinicaRemote(databaseRef))
    }
    private val veterinarioRepository by lazy {
        VeterinarioRepository(VeterinarioRemote(databaseRef))
    }
    private var clinicasCargadas = emptyList<Pair<String, Clinica>>()
    private var veterinariosCargados = emptyList<Pair<String, Veterinario>>()
    private var clinicaSeleccionada: Pair<String, Clinica>? = null
    private var veterinarioSeleccionado: Pair<String, Veterinario>? = null
    private var fechaSeleccionada: Calendar? = null


    private var idMascota: String = ""
    private var idPropietario: String = ""
    private var nombreMascota: String = ""
    private var especieMascota: String = ""

    /* EXPLICACIÓN DEL METODO <onCreateView()> : despliega para leer...
        Inicializa la vista del fragmento utilizando ViewBinding para enlazar el layout XML.
        Crea el objeto binding necesario para interactuar con los componentes visuales de la
        pantalla de solicitud de citas médicas.
    */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMascotaCitaBinding.inflate(inflater, container, false)
        return binding.root
    }

    /* EXPLICACIÓN DEL METODO <onViewCreated()> : despliega para leer...
        Configura la lógica inicial del fragmento tras la creación de la vista.
        Establece los escuchadores de resultados de diálogos, inicializa los adaptadores de los
        spinners y registra el callback para gestionar el botón de retroceso del sistema.
    */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cargarDatosMascotaDesdeContainer()

        parentFragmentManager.setFragmentResultListener(keyConfirmacion, viewLifecycleOwner) { _, bundle ->
            val confirmado = bundle.getBoolean(ConfirmacionDialog.KEY_CONFIRMADO)
            if (confirmado) {
                guardarCita()
            }
        }

        configurarSpinners()
        configurarListeners()
        cargarComunidades()

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navegacionFragment(1)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    /* EXPLICACIÓN DEL METODO <navegacionFragment()> : despliega para leer...
        Gestiona el flujo de navegación para retornar a la pantalla del perfil de la mascota.
        Utiliza el NavigatorMascota para cerrar la vista de solicitud de cita y volver
        a la pantalla anterior de forma controlada y segura.
    */
    fun navegacionFragment(num : Int) {
        when (num) {
            1 -> NavigatorMascota.MascotaCita_to_MascotaPerfil(this@MascotaCitaFragment)
        }
    }

    /* EXPLICACIÓN DEL METODO <configurarSpinners()> : despliega para leer...
        Establece el estado inicial de los selectores desplegables de clínica y veterinario.
        Asigna valores por defecto y bloquea la interacción hasta que el usuario seleccione
        una comunidad autónoma válida que permita cargar datos reales.
    */
    private fun configurarSpinners() {
        setAdapter(binding.spClinica, listOf("Selecciona una clínica"))
        setAdapter(binding.spVeterinario, listOf("Selecciona un veterinario"))

        binding.spClinica.isEnabled = false
        binding.spVeterinario.isEnabled = false
    }

    /* EXPLICACIÓN DEL METODO <configurarListeners()> : despliega para leer...
        Define el comportamiento interactivo de los selectores de fecha, comunidad y clínica.
        Establece una lógica de cascada donde la selección de un elemento (como la comunidad)
        desencadena la carga de datos en el siguiente nivel (las clínicas correspondientes).
    */
    private fun configurarListeners() {
        binding.ibCalendar.setOnClickListener { abrirDatePicker() }
        binding.tvFecha.setOnClickListener { abrirDatePicker() }

        binding.spComunidad.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == 0) {
                    limpiarClinicas()
                    limpiarVeterinarios()
                    return
                }

                val comunidadVisible = binding.spComunidad.selectedItem.toString()
                val clave = obtenerClaveComunidad(comunidadVisible)

                cargarClinicas(clave)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }

        binding.spClinica.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == 0) {
                    clinicaSeleccionada = null
                    limpiarVeterinarios()
                    return
                }

                clinicaSeleccionada = clinicasCargadas[position - 1]
                cargarVeterinarios(clinicaSeleccionada!!.first)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }

        binding.spVeterinario.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                veterinarioSeleccionado =
                    if (position == 0) null else veterinariosCargados[position - 1]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }

        binding.btnGuardar.setOnClickListener {
            if (comprobarCampos()) {
                ConfirmacionDialog.nuevoDialog(
                    "CONFIRMAR CITA",
                    "¿Estás seguro de que quieres solicitar esta cita?",
                    keyConfirmacion
                ).show(parentFragmentManager, "ConfirmacionDialog")
            }
        }
    }

    /* EXPLICACIÓN DEL METODO <cargarDatosMascotaDesdeContainer()> : despliega para leer...
        Recupera la información básica de la mascota seleccionada desde el fragmento contenedor.
        Almacena el nombre, especie e identificadores necesarios para vincular correctamente
        la nueva cita con el animal y su propietario en la base de datos.
    */
    private fun cargarDatosMascotaDesdeContainer() {
        val container = parentFragment?.parentFragment as? MascotaContainerFragment

        val mascota = container?.obtenerMascotaSeleccionada()
        val idMascotaContainer = container?.obtenerIdMascotaSeleccionada()

        idMascota = idMascotaContainer.orEmpty()
        idPropietario = mascota?.idPropietario.orEmpty()
        nombreMascota = mascota?.nombre.orEmpty()
        especieMascota = mascota?.especie.orEmpty()
    }

    /* EXPLICACIÓN DEL METODO <cargarComunidades()> : despliega para leer...
        Rellena el spinner de comunidades autónomas utilizando un recurso de array de strings.
        Carga la lista de opciones disponibles en el adaptador para que el usuario pueda
        iniciar el proceso de filtrado de clínicas veterinarias por ubicación.
    */
    private fun cargarComunidades() {
        val lista = resources.getStringArray(R.array.opciones_comunidades).toList()
        setAdapter(binding.spComunidad, lista)
    }

    /* EXPLICACIÓN DEL METODO <cargarClinicas()> : despliega para leer...
        Solicita al repositorio la lista de centros veterinarios activos en una comunidad específica.
        Tras recibir los datos de Firebase, actualiza el spinner de clínicas y habilita la
        interacción para que el usuario pueda elegir el centro deseado.
    */
    private fun cargarClinicas(claveComunidad: String) {
        limpiarClinicas()
        limpiarVeterinarios()

        clinicaRepository.obtenerClinicasActivasPorComunidad(
            claveComunidad = claveComunidad,
            onSuccess = { lista ->
                clinicasCargadas = lista

                val nombres = mutableListOf("Selecciona una clínica")
                nombres.addAll(lista.map { it.second.nombre ?: "" })

                setAdapter(binding.spClinica, nombres)
                binding.spClinica.isEnabled = true
            },
            onError = { error ->
                mostrarSnackbar(error ?: "No se pudieron cargar las clínicas.")
            }
        )
    }

    /* EXPLICACIÓN DEL METODO <cargarVeterinarios()> : despliega para leer...
        Obtiene del repositorio los veterinarios activos asociados a una clínica determinada.
        Mapea los resultados para mostrar el nombre completo de los profesionales en el
        spinner correspondiente y permite continuar con el proceso de solicitud de cita.
    */
    private fun cargarVeterinarios(idClinica: String) {
        limpiarVeterinarios()

        veterinarioRepository.obtenerVeterinariosActivosPorClinica(
            idClinica = idClinica,
            onSuccess = { lista ->
                veterinariosCargados = lista

                val nombres = mutableListOf("Selecciona un veterinario")
                nombres.addAll(
                    lista.map {
                        "${it.second.nombre.orEmpty()} ${it.second.apellido.orEmpty()}".trim()
                    }
                )

                setAdapter(binding.spVeterinario, nombres)
                binding.spVeterinario.isEnabled = true
            },
            onError = { error ->
                mostrarSnackbar(error ?: "No se pudieron cargar los veterinarios.")
            }
        )
    }

    /* EXPLICACIÓN DEL METODO <abrirDatePicker()> : despliega para leer...
        Despliega un calendario emergente para que el usuario seleccione el día de la cita.
        Configura la fecha mínima como el día actual para evitar citas en el pasado y
        actualiza el campo de texto con el formato de fecha legible (dd/MM/yyyy).
    */
    private fun abrirDatePicker() {
        val calendar = fechaSeleccionada ?: Calendar.getInstance()

        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                fechaSeleccionada = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth, 0, 0, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                binding.tvFecha.text =
                    String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = Calendar.getInstance().timeInMillis
            show()
        }
    }

    /* EXPLICACIÓN DEL METODO <guardarCita()> : despliega para leer...
        Procesa el registro definitivo de la cita médica en Firebase Realtime Database.
        Genera un ID único, construye el objeto Cita con todos los datos relacionales y
        notifica el éxito de la operación antes de limpiar el formulario.
    */
    private fun guardarCita() {
        val clinica = clinicaSeleccionada ?: return
        val veterinario = veterinarioSeleccionado ?: return

        val fechaHoraInicio = construirFechaHoraInicio()
        val fechaHoraFin = construirFechaHoraFin(fechaHoraInicio)

        citaRepository.generarIdCita(
            onSuccess = { idCita ->

                val nuevaCita = Cita(
                    id = idCita,
                    idClinica = clinica.first,
                    idMascota = idMascota,
                    idPropietario = idPropietario,
                    idVeterinario = veterinario.first,
                    estadoCita = "Programada",
                    fechaCreacion = fechaActualIso(),
                    fechaHoraInicio = fechaHoraInicio,
                    fechaHoraFin = fechaHoraFin,
                    motivoConsulta = binding.etMotivo.text.toString().trim(),
                    observaciones = "",
                    tipoCita = binding.spTipo.selectedItem.toString(),
                    clinica = CitaClinica(
                        nombre = clinica.second.nombre ?: "",
                        direccion = clinica.second.direccion ?: ""
                    ),
                    veterinario = CitaVeterinario(
                        nombre = veterinario.second.nombre ?: "",
                        apellido = veterinario.second.apellido ?: ""
                    ),
                    mascota = CitaMascota(
                        nombre = nombreMascota,
                        especie = especieMascota
                    )
                )

                citaRepository.registrarCita(
                    idCita = idCita,
                    cita = nuevaCita,
                    onSuccess = {
                        mostrarSnackbar("Cita solicitada con éxito.")
                        limpiarFormulario()
                    },
                    onError = { error ->
                        mostrarSnackbar(error ?: "No se pudo registrar la cita.")
                    }
                )
            },
            onError = { error ->
                mostrarSnackbar(error ?: "No se pudo generar el id de la cita.")
            }
        )
    }

    /* EXPLICACIÓN DEL METODO <comprobarCampos()> : despliega para leer...
        Valida que toda la información obligatoria del formulario haya sido cumplimentada.
        Verifica la selección de clínica, veterinario, fecha, hora y motivo, asegurando que
        no se envíen solicitudes incompletas a la base de datos.
    */
    private fun comprobarCampos(): Boolean {
        if (idMascota.isBlank()) {
            mostrarSnackbar("No se ha podido identificar la mascota seleccionada.")
            return false
        }

        if (idPropietario.isBlank()) {
            mostrarSnackbar("No se ha podido identificar el propietario de la mascota.")
            return false
        }

        if (binding.spComunidad.selectedItemPosition == 0) {
            mostrarSnackbar("Selecciona una comunidad autónoma.")
            return false
        }

        if (binding.spClinica.selectedItemPosition == 0) {
            mostrarSnackbar("Selecciona una clínica.")
            return false
        }

        if (binding.spVeterinario.selectedItemPosition == 0) {
            mostrarSnackbar("Selecciona un veterinario.")
            return false
        }

        if (binding.tvFecha.text.toString().isBlank()) {
            mostrarSnackbar("Selecciona una fecha.")
            return false
        }

        if (binding.spHora.selectedItemPosition == 0) {
            mostrarSnackbar("Selecciona una hora.")
            return false
        }

        val motivo = binding.etMotivo.text.toString().trim()
        if (motivo.isEmpty()) {
            mostrarSnackbar("Por favor, indica el motivo de la cita.")
            binding.etMotivo.requestFocus()
            return false
        }

        return true
    }

    /* EXPLICACIÓN DEL METODO <limpiarClinicas() / limpiarVeterinarios()> : despliega para leer...
        Reinicia los selectores de centros y profesionales a su estado por defecto.
        Vacía las listas de datos cargados y bloquea la interacción con los spinners
        para mantener la integridad del flujo de selección en cascada.
    */
    private fun limpiarClinicas() {
        clinicasCargadas = emptyList()
        clinicaSeleccionada = null
        setAdapter(binding.spClinica, listOf("Selecciona una clínica"))
        binding.spClinica.isEnabled = false
    }

    private fun limpiarVeterinarios() {
        veterinariosCargados = emptyList()
        veterinarioSeleccionado = null
        setAdapter(binding.spVeterinario, listOf("Selecciona un veterinario"))
        binding.spVeterinario.isEnabled = false
    }

    /* EXPLICACIÓN DEL METODO <limpiarFormulario()> : despliega para leer...
        Restablece todos los componentes de entrada del fragmento a sus valores iniciales.
        Limpia los campos de texto, reinicia las selecciones de los spinners y anula
        la fecha seleccionada para permitir una nueva solicitud de cita desde cero.
    */
    private fun limpiarFormulario() {
        binding.spComunidad.setSelection(0)
        limpiarClinicas()
        limpiarVeterinarios()
        binding.spTipo.setSelection(0)
        binding.spHora.setSelection(0)
        binding.etMotivo.text?.clear()
        binding.tvFecha.text = ""
        fechaSeleccionada = null
    }

    /* EXPLICACIÓN DEL METODO <setAdapter()> : despliega para leer...
        Configura un ArrayAdapter genérico para cualquier componente Spinner del fragmento.
        Define el diseño visual de los elementos de la lista y la apariencia del menú desplegable
        utilizando los recursos estándar de Android.
    */
    private fun setAdapter(spinner: android.widget.Spinner, items: List<String>) {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            items
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    /* EXPLICACIÓN DEL METODO <obtenerClaveComunidad()> : despliega para leer...
        Mapea el nombre visual de la comunidad autónoma con su identificador técnico en Firebase.
        Normaliza el texto para evitar errores por tildes o mayúsculas, garantizando que
        la consulta al repositorio utilice la clave de nodo correcta.
    */
    private fun obtenerClaveComunidad(texto: String): String {
        return when (texto.trim().lowercase(Locale.getDefault())) {
            "andalucía", "andalucia" -> "andalucia"
            "aragón", "aragon" -> "aragon"
            "asturias" -> "asturias"
            "cantabria" -> "cantabria"
            "castilla-la mancha", "castilla la mancha" -> "castilla_la_mancha"
            "castilla y león", "castilla y leon" -> "castilla_y_leon"
            "cataluña", "cataluna" -> "cataluna"
            "comunidad valenciana", "valencia" -> "valencia"
            "extremadura" -> "extremadura"
            "galicia" -> "galicia"
            "islas baleares", "baleares" -> "islas_baleares"
            "islas canarias", "canarias" -> "islas_canarias"
            "la rioja" -> "la_rioja"
            "comunidad de madrid", "madrid" -> "madrid"
            "región de murcia", "region de murcia", "murcia" -> "murcia"
            "comunidad foral de navarra", "navarra" -> "navarra"
            "país vasco", "pais vasco" -> "pais_vasco"
            else -> ""
        }
    }

    /* EXPLICACIÓN DE LOS METODOS <FechaHora()> : despliega para leer...
        Funciones auxiliares para el procesamiento y formateo de marcas de tiempo en formato ISO.
        Construyen las cadenas de texto necesarias para definir el inicio y el fin de la
        cita médica, permitiendo su correcta persistencia y ordenación en la base de datos.
    */
    private fun fechaActualIso(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            .format(Calendar.getInstance().time)
    }

    private fun construirFechaHoraInicio(): String {
        val fecha = fechaSeleccionada ?: Calendar.getInstance()
        val horaTexto = binding.spHora.selectedItem.toString()
        val partes = horaTexto.split(":")
        val hora = partes[0].toInt()
        val minuto = partes[1].toInt()

        val cal = fecha.clone() as Calendar
        cal.set(Calendar.HOUR_OF_DAY, hora)
        cal.set(Calendar.MINUTE, minuto)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)

        return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(cal.time)
    }

    private fun construirFechaHoraFin(fechaInicio: String): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val fecha = sdf.parse(fechaInicio) ?: Calendar.getInstance().time
        val cal = Calendar.getInstance().apply {
            time = fecha
            add(Calendar.MINUTE, 30)
        }
        return sdf.format(cal.time)
    }

    /* EXPLICACIÓN DEL METODO <onDestroyView()> : despliega para leer...
        Libera la referencia al objeto binding cuando la vista del fragmento es destruida.
        Previene fugas de memoria asegurando que las referencias a los componentes visuales
        se anulen correctamente al finalizar el ciclo de vida de la vista.
    */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}