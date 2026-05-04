package com.example.vetycare.ui.fragment.usuario

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.example.vetycare.database.remote.PropietarioRemote
import com.example.vetycare.database.repository.PropietarioRepository
import com.example.vetycare.databinding.FragmentUsuarioInicioBinding
import com.example.vetycare.navigation.NavigatorRoot
import com.example.vetycare.utils.FirebaseUtils
import com.example.vetycare.utils.mostrarSnackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

/* EXPLICACIÓN DE LA CLASE <UsuarioInicioFragment()> : despliega para leer...
    Fragmento de bienvenida que actúa como pantalla de inicio personalizada para el usuario autenticado.
    Muestra el nombre completo del propietario y adapta el saludo según el género registrado,
    sirviendo como punto de entrada principal al panel de control del usuario.
 */
class UsuarioInicioFragment : Fragment() {
    private lateinit var binding : FragmentUsuarioInicioBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var propietarioRepository: PropietarioRepository

    /* EXPLICACIÓN DEL METODO <onAttach()> : despliega para leer...
        Inicializa las instancias de Firebase Auth y Realtime Database al vincular el fragmento con la actividad.
        Establece las referencias de red necesarias para que el repositorio pueda consultar
        la información del perfil del usuario de forma segura.
    */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        auth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance(FirebaseUtils.URL_RTDB)
        databaseReference = firebaseDatabase.reference
    }

    /* EXPLICACIÓN DEL METODO <onCreateView()> : despliega para leer...
        Infla la jerarquía de vistas utilizando ViewBinding para establecer la interfaz de usuario.
        Genera el objeto binding que permite el acceso a los componentes visuales de la
        pantalla de bienvenida de forma eficiente durante el ciclo de vida del fragmento.
    */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View? {
        binding = FragmentUsuarioInicioBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    /* EXPLICACIÓN DEL METODO <onViewCreated()> : despliega para leer...
        Configura el repositorio de propietarios y lanza la carga inicial de los datos del perfil.
        Implementa también la lógica para gestionar el botón de retroceso nativo, asegurando
        que el usuario regrese a la pantalla de login principal al intentar salir.
    */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val remotePropietario = PropietarioRemote(databaseReference)
        propietarioRepository = PropietarioRepository(remotePropietario)
        cargarDatosUsuario()

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navegacionFragment(1)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    /* EXPLICACIÓN DEL METODO <onResume()> : despliega para leer...
        Ejecuta una recarga de los datos del usuario cada vez que el fragmento vuelve al primer plano.
        Garantiza que la información de bienvenida y el nombre mostrado en pantalla estén
        siempre actualizados con los últimos cambios registrados en Firebase.
    */
    override fun onResume() {
        super.onResume()
        cargarDatosUsuario()
    }

    /* EXPLICACIÓN DEL METODO <navegacionFragment()> : despliega para leer...
        Centraliza el flujo de salida para cerrar la sesión y retornar a la pantalla de inicio de la app.
        Utiliza el NavigatorRoot para gestionar la transición entre los contenedores,
        asegurando una desconexión limpia del flujo de usuario actual.
    */
    fun navegacionFragment(num : Int) {
        when (num) {
            1 -> NavigatorRoot.Usuario_to_Inicio(this@UsuarioInicioFragment)
        }
    }

    /* EXPLICACIÓN DEL METODO <cargarDatosUsuario()> : despliega para leer...
        Recupera el identificador del usuario activo y solicita su perfil completo al repositorio.
        Actualiza dinámicamente los TextViews con el nombre completo y ajusta el género gramatical
        del saludo de bienvenida basándose en la información de la base de datos.
    */
    private fun cargarDatosUsuario() {
        val auth = auth.currentUser?.uid
        if (auth.isNullOrEmpty()) {
            mostrarSnackbar("No se ha podido encontrar el autentificador")
            return
        }

        propietarioRepository.obtenerPropietario(
            auth,
            { id, propietario ->
                binding.tvNombreUsuario.setText(propietario.nombre + " " + propietario.apellido)
                if (propietario.sexo.equals("Femenino")) {
                    binding.tvTitulo.setText("BIENVENIDA")
                }
            },
            { mensajeDeError ->
                mostrarSnackbar(mensajeDeError?:"ERROR")
            }
        )
    }
}