package com.example.vetycare.ui.fragment.inicio

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.vetycare.databinding.FragmentInicioPrincipalBinding
import com.example.vetycare.navigation.NavigatorInicio
import com.example.vetycare.navigation.NavigatorRoot
import com.example.vetycare.utils.FirebaseUtils
import com.example.vetycare.utils.mostrarSnackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.example.vetycare.utils.ocultarTeclado

/* EXPLICACIÓN DE LA CLASE <InicioPrincipalFragment()> : despliega para leer...
    Fragmento principal de acceso que gestiona el inicio de sesión de los usuarios en la aplicación.
    Permite la autenticación mediante Firebase, la redirección a registros o recuperación de claves,
    y el guardado de credenciales localmente para agilizar futuros accesos.
 */
class InicioPrincipalFragment : Fragment() {
    private lateinit var binding : FragmentInicioPrincipalBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private val PREFS_NAME = "VetyCarePrefs"
    private val KEY_CORREO = "correo_recordado"
    private val KEY_RECORDAR = "recordar_check"

    /* EXPLICACIÓN DEL METODO <onAttach()> : despliega para leer...
        Inicializa los servicios fundamentales de Firebase al vincular el fragmento con la actividad.
        Garantiza que las instancias de autenticación y la base de datos en tiempo real estén
        correctamente configuradas antes de procesar cualquier intento de acceso.
    */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        auth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance(FirebaseUtils.URL_RTDB)
    }

    /* EXPLICACIÓN DEL METODO <onCreateView()> : despliega para leer...
        Infla la jerarquía de vistas del fragmento utilizando ViewBinding para establecer la interfaz.
        Retorna la vista raíz que contiene los campos de texto, botones y elementos interactivos
        necesarios para que el usuario pueda introducir sus datos de acceso.
    */
    override fun onCreateView (inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View? {
        binding = FragmentInicioPrincipalBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    /* EXPLICACIÓN DEL METODO <onResume()> : despliega para leer...
        Configura los listeners de interacción y restaura las preferencias de usuario almacenadas.
        Carga automáticamente el correo electrónico guardado si la opción de recordar sesión
        estaba activa, facilitando una experiencia de usuario más rápida y eficiente.
    */
    override fun onResume() {
        super.onResume()

        /* Acciones de los botones del fragment:
            Boton entrar => Recoge nombre del usuario y contraseña. Verifica con la FireBase y entra en caso afirmativo.
            Boton registrarse => Navega al Fragment Inicio Registro Ususario
            Boton Olvidar Contraseña => Navega al Fragment Inicio Recuperacion Contraseña
        */
        binding.btnEntrar.setOnClickListener {
            ocultarTeclado()
            comprobarInicioSesion()
        }
        binding.tvLinkRegistrate.setOnClickListener{ navegacionFragment(2) }
        binding.tvOlvideContrasenha.setOnClickListener { navegacionFragment(3) }

        val sharedPref = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val correoGuardado = sharedPref.getString(KEY_CORREO, "")
        val estaRecordado = sharedPref.getBoolean(KEY_RECORDAR, false)

        if (estaRecordado) {
            binding.etCorreo.setText(correoGuardado)
            binding.cbRecordar.isChecked = true
        }
    }

    /* EXPLICACIÓN DEL METODO <navegacionFragment()> : despliega para leer...
        Centraliza el flujo de navegación hacia los diferentes destinos del módulo de inicio.
        Gestiona el salto hacia el registro de nuevos usuarios, la recuperación de contraseñas
        olvidadas o el acceso al contenedor principal de la aplicación tras el login.
    */
    fun navegacionFragment(num : Int) {
        when (num) {
            1 -> NavigatorRoot.Inicio_to_Usuario(this) // Navega al Container Usuario
            2 -> NavigatorInicio.InicioPrincipal_to_InicioRegistro(this) // Navega al Fragment Inicio Registro Ususario
            3 -> NavigatorInicio.InicioPrincipal_to_InicioRecPass(this) // Navega al Fragment Inicio Recuperacion Contraseña
        }
    }

    /* EXPLICACIÓN DEL METODO <comprobarInicioSesion()> : despliega para leer...
        Valida las credenciales introducidas consultando el servicio de Authentication de Firebase.
        Gestiona el almacenamiento o limpieza de las SharedPreferences según la casilla de verificación
        y autoriza la entrada al sistema principal si la respuesta del servidor es exitosa.
    */
    fun comprobarInicioSesion() {
        val correo = binding.etCorreo.text.toString().trim()
        val pass = binding.etContrasenha.text.toString().trim()
        val recordar = binding.cbRecordar.isChecked

        if (correo.isEmpty() || pass.isEmpty()) {
            mostrarSnackbar("Por favor, rellena todos los campos")
            return
        }

        auth
            .signInWithEmailAndPassword(
                binding.etCorreo.text.toString(),
                binding.etContrasenha.text.toString()
            ).addOnCompleteListener {
                if (it.isSuccessful) {
                    val sharedPref = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    val editor = sharedPref.edit()

                    if (recordar) {
                        editor.putString(KEY_CORREO, correo)
                        editor.putBoolean(KEY_RECORDAR, true)
                    } else {
                        editor.clear()
                    }
                    editor.apply()

                    mostrarSnackbar("Bienvenido a VetyCare!")
                    navegacionFragment(1)
                }
                else {
                    mostrarSnackbar("Correo o contraseña incorrectos")
                }
            }
    }
}