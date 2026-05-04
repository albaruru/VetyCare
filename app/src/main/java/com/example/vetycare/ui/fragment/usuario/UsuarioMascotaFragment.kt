package com.example.vetycare.ui.fragment.usuario

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vetycare.adapter.MascotaAdapter
import com.example.vetycare.database.remote.MascotaRemote
import com.example.vetycare.database.remote.PropietarioRemote
import com.example.vetycare.database.repository.MascotaRepository
import com.example.vetycare.database.repository.PropietarioRepository
import com.example.vetycare.databinding.FragmentUsuarioMascotaBinding
import com.example.vetycare.model.entities.Mascota
import com.example.vetycare.navigation.NavigatorRoot
import com.example.vetycare.navigation.NavigatorUsuario
import com.example.vetycare.utils.FirebaseUtils
import com.example.vetycare.utils.mostrarSnackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

/* EXPLICACIÓN DE LA CLASE <UsuarioMascotaFragment()> : despliega para leer...
    Fragmento encargado de gestionar y mostrar el listado de mascotas pertenecientes al usuario autenticado.
    Coordina la recuperación de datos desde Firebase y permite la navegación hacia el registro de
    nuevos animales o la consulta de perfiles detallados mediante un listado interactivo.
 */
class UsuarioMascotaFragment: Fragment(), MascotaAdapter.OnMascotaListener {
    private lateinit var binding : FragmentUsuarioMascotaBinding
    private lateinit var adapterMascota: MascotaAdapter
    private lateinit var listaMascotas: ArrayList<Pair<String, Mascota>>
    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var propietarioRepository: PropietarioRepository
    private lateinit var mascotaRepository: MascotaRepository

    /* EXPLICACIÓN DEL METODO <onAttach()> : despliega para leer...
        Inicializa los servicios de autenticación y base de datos de Firebase al vincular el fragmento con la actividad.
        Establece las referencias de red necesarias para que los repositorios puedan realizar
        consultas de información de forma segura y eficiente desde el inicio.
    */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        auth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance(FirebaseUtils.URL_RTDB)
        databaseReference = firebaseDatabase.reference
    }

    /* EXPLICACIÓN DEL METODO <onCreateView()> : despliega para leer...
        Infla la jerarquía de vistas del fragmento utilizando la clase de vinculación generada para establecer la interfaz.
        Genera el objeto binding que permite el acceso directo a los componentes visuales del
        listado de mascotas y devuelve la vista raíz para su posterior renderizado.
    */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View? {
        binding = FragmentUsuarioMascotaBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    /* EXPLICACIÓN DEL METODO <onViewCreated()> : despliega para leer...
        Configura los repositorios de datos, inicializa el adaptador y lanza la carga de mascotas del propietario.
        Registra también el callback para gestionar el botón de retroceso nativo, asegurando un
        retorno controlado hacia la pantalla de inicio de usuario.
    */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val remotePropietario = PropietarioRemote(databaseReference)
        propietarioRepository = PropietarioRepository(remotePropietario)

        val remoteMascota = MascotaRemote(databaseReference)
        mascotaRepository = MascotaRepository(remoteMascota)

        instancias()
        configurarRecycler()
        cargarMascotasUsuario()

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navegacionFragment(4)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    /* EXPLICACIÓN DEL METODO <onResume()> : despliega para leer...
    Configura los escuchadores de interacción para los botones de registro y navegación de la interfaz.
    Mantiene activa la escucha de eventos cada vez que el fragmento vuelve a estar en primer plano
    para asegurar una respuesta inmediata a las acciones del usuario.
*/
    override fun onResume() {
        super.onResume()

        /* Acciones de los botones del fragment:
            Botón Mascota => Navega al UsuarioRegMascotaFragment
            Botón Titulo => Navega al Perfil de la Mascota
        */
        binding.btnMascota.setOnClickListener {
            navegacionFragment(1)
        }
        binding.tvTitulo.setOnClickListener {
            navegacionFragment(2)
        }
    }

    /* EXPLICACIÓN DEL METODO <navegacionFragment()> : despliega para leer...
        Centraliza la lógica de flujo hacia el registro de mascotas, perfiles detallados o retorno al inicio.
        Utiliza los navegadores de usuario y raíz para gestionar las transiciones de forma coherente
        y segura entre los distintos fragmentos y contenedores de la aplicación.
    */
    fun navegacionFragment(num : Int, idMascota: String? = null, mascota: Mascota? = null) {
        when (num) {
            1 -> NavigatorUsuario.UsuarioMascota_to_UsuarioRegMascota(this)
            2 -> NavigatorRoot.Usuario_to_Mascota(this)
            3 -> {
                if(mascota != null){
                    NavigatorRoot.UsuarioMascota_to_MascotaPerfil(this, idMascota, mascota)
                }
            }
            4 -> NavigatorUsuario.UsuarioMascota_to_UsuarioInicio(this@UsuarioMascotaFragment)
        }
    }

    /* EXPLICACIÓN DEL METODO <instancias()> : despliega para leer...
        Inicializa la colección de datos de mascotas y el adaptador especializado para el RecyclerView.
        Vicula el fragmento como listener de clics mediante la interfaz OnMascotaListener para
        procesar la selección de cada animal individualmente dentro del listado.
    */
    private fun instancias() {
        listaMascotas = ArrayList()
        adapterMascota = MascotaAdapter(listaMascotas, requireContext(), this)
    }

    /* EXPLICACIÓN DEL METODO <configurarRecycler()> : despliega para leer...
        Define la estructura técnica del listado de mascotas estableciendo su gestor de diseño lineal y su adaptador.
        Prepara el componente visual para renderizar las tarjetas de los animales de forma organizada
        y optimiza el rendimiento del scroll dentro del fragmento.
    */
    private fun configurarRecycler(){
        binding.recyclerMascotas.layoutManager = LinearLayoutManager(context)
        binding.recyclerMascotas.adapter = adapterMascota
    }

    /* EXPLICACIÓN DEL METODO <cargarMascotasUsuario()> : despliega para leer...
        Consulta el perfil del propietario activo para obtener su identificador único y descargar su lista de mascotas.
        Sincroniza las respuestas del repositorio de mascotas para actualizar el adaptador visual
        con los datos más recientes recuperados de Firebase Realtime Database.
    */
    private fun cargarMascotasUsuario() {
        val auth = auth.currentUser?.uid

        if (auth.isNullOrEmpty()) {
            mostrarSnackbar("No se ha podido encontrar su identificador")
            return
        }

        propietarioRepository.obtenerPropietario(
            auth,
            { id, prop ->
                mascotaRepository.obtenerMascotasPorPropietario(
                    id,
                    { listaResultado ->
                        adapterMascota.actualizarLista(listaResultado)
                    },
                    { mensajeDeError ->
                        mostrarSnackbar(mensajeDeError ?: "ERROR al cargar mascotas")
                    }
                )
            },
            { mensajeDeError ->
                mostrarSnackbar(mensajeDeError?: "ERROR")
            }
        )
    }

    /* EXPLICACIÓN DEL METODO <onMascotaClick()> : despliega para leer...
        Captura el evento de pulsación sobre una mascota específica dentro del listado del adaptador.
        Delega la acción al metodo de navegación para abrir la pantalla del perfil detallado,
        pasando el identificador y el objeto técnico de la mascota seleccionada.
    */
    override fun onMascotaClick(idMascota: String, mascota: Mascota){
        navegacionFragment(3, idMascota, mascota)
    }
}