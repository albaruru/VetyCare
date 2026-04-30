package com.example.vetycare.ui.fragment.inicio

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.example.vetycare.databinding.FragmentInicioRecPassBinding
import com.example.vetycare.navigation.NavigatorInicio
import com.example.vetycare.ui.dialog.CancelacionDialog
import com.example.vetycare.utils.FirebaseUtils
import com.example.vetycare.utils.mostrarSnackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

/* EXPLICACIÓN DE LA CLASE <InicioRecPassFragment()> : despliega para leer...
    Fragmento encargado de gestionar la recuperación de contraseñas de los usuarios.
    Proporciona una interfaz para introducir el correo electrónico y solicitar un enlace de
    restablecimiento a través de los servicios de Firebase Authentication.
 */
class InicioRecPassFragment : Fragment() {
    private lateinit var binding : FragmentInicioRecPassBinding
    private val keyCancelacion = "cancelacion_recuperacion"
    private lateinit var auth : FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase

    /* EXPLICACIÓN DEL METODO <onAttach()> : despliega para leer...
        Inicializa las referencias necesarias de Firebase Auth y Realtime Database al vincular
        el fragmento con su actividad anfitriona. Asegura que los servicios de comunicación
        con el backend estén listos antes de cualquier interacción.
    */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        auth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance(FirebaseUtils.URL_RTDB)
    }

    /* EXPLICACIÓN DEL METODO <onCreate()> : despliega para leer...
        Establece el escuchador de resultados para gestionar la respuesta del diálogo de cancelación.
        Si el usuario confirma que desea abandonar el proceso, se ejecuta automáticamente
        la navegación de retorno hacia la pantalla de inicio principal.
    */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        parentFragmentManager.setFragmentResultListener(keyCancelacion, this) {_, bundle ->
            val cancelado = bundle.getBoolean(CancelacionDialog.KEY_CANCELADO)
            if (cancelado) {
                navegacionFragment(1)
            }
        }
    }

    /* EXPLICACIÓN DEL METODO <onCreateView()> : despliega para leer...
        Infla la jerarquía de vistas del fragmento utilizando la clase de vinculación generada.
        Prepara el contenedor visual y devuelve la vista raíz que será renderizada en la
        pantalla del dispositivo durante el ciclo de vida del fragmento.
    */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View? {
        binding = FragmentInicioRecPassBinding.inflate(layoutInflater, container,false)
        return binding.root
    }

    /* EXPLICACIÓN DEL METODO <onViewCreated()> : despliega para leer...
        Configura el comportamiento del botón físico de retroceso del dispositivo mediante un callback.
        Garantiza que, si el usuario intenta volver atrás usando el sistema nativo de Android,
        la aplicación lo redirija de forma controlada a la pantalla de login.
    */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navegacionFragment(2)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    /* EXPLICACIÓN DEL METODO <onResume()> : despliega para leer...
        Define los eventos de interacción para los botones de enviar solicitud y cancelar proceso.
        Mantiene activa la escucha de eventos de la interfaz cada vez que el fragmento vuelve a
        estar en primer plano para el usuario.
    */
    override fun onResume() {
        super.onResume()

        /* Acciones de los botones del fragment:
            Botón Enviar => Recogeremos el correo y le mandaremos un email para la recuperación de contraseña
            Botón Volver => Descarta cualquier información introducida en nuestros bloques de texto y volvemos a la pantalla login
        */
        binding.btnEnviar.setOnClickListener {
            // Solo si la validación es correcta, mostramos el diálogo de confirmación
            recuperacionPassFirebase()
        }
        binding.btnVolver.setOnClickListener {
            mensaje("cancelacion")
        }
    }

    /* EXPLICACIÓN DEL METODO <navegacionFragment()> : despliega para leer...
        Centraliza la lógica de navegación para retornar a la pantalla de acceso principal.
        Utiliza el navegador específico del módulo de inicio para realizar la transición de
        salida y asegurar un flujo de pantallas coherente y sin errores.
    */
    fun navegacionFragment(num: Int) {
        when (num) {
            1 -> NavigatorInicio.InicioRecPass_to_InicioPrincipal(this)
            2 -> NavigatorInicio.InicioRecPass_to_InicioPrincipal(this@InicioRecPassFragment)
        }
    }

    /* EXPLICACIÓN DEL METODO <mensaje()> : despliega para leer...
        Gestiona la visualización del diálogo de advertencia antes de descartar los datos introducidos.
        Permite personalizar el título y contenido del mensaje de cancelación, asegurando que
        el usuario entienda las consecuencias de abandonar el proceso actual.
    */
    fun mensaje (tipo: String) {
        when (tipo) {
            "cancelacion" -> {
                CancelacionDialog.nuevoDialog(
                    "CANCELACION RECUPERAR CONTRASEÑA",
                    "¿Deseas cancelar el proceso? \nLos cambios no se guardarán.",
                    keyCancelacion
                ).show(parentFragmentManager,"CancelacionDialog")
            }
        }
    }

    /* EXPLICACIÓN DEL METODO <recuperacionPassFirebase()> : despliega para leer...
        Valida el formato del correo introducido y solicita el envío del email de recuperación a Firebase.
        Gestiona las respuestas de éxito o error del servidor, notificando al usuario mediante
        Snackbars y redirigiéndolo al inicio si la operación fue exitosa.
    */
    fun recuperacionPassFirebase(): Boolean {
        val correo = binding.etCorreo.text.toString().trim()


        if(correo.isEmpty()){
            mostrarSnackbar("Por favor, introduce tu correo electrónico.")
            return false
        }

        auth.sendPasswordResetEmail(correo)
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    mostrarSnackbar("Correo de recuperación enviado a: $correo")
                    navegacionFragment(1)
                } else {
                    mostrarSnackbar("Error: No se ha podido enviar el correo. Verifica que existe")
            }
        }
        return true
    }
}