package com.example.vetycare.ui.fragment.usuario

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

class UsuarioMascotaFragment: Fragment(), MascotaAdapter.OnMascotaListener {
    private lateinit var binding : FragmentUsuarioMascotaBinding
    private lateinit var adapterMascota: MascotaAdapter
    private lateinit var listaMascotas: ArrayList<Pair<String, Mascota>>
    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var propietarioRepository: PropietarioRepository
    private lateinit var mascotaRepository: MascotaRepository

    override fun onAttach(context: Context) {
        super.onAttach(context)
        auth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance(FirebaseUtils.URL_RTDB)
        databaseReference = firebaseDatabase.reference
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View? {
        binding = FragmentUsuarioMascotaBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val remotePropietario = PropietarioRemote(databaseReference)
        propietarioRepository = PropietarioRepository(remotePropietario)

        val remoteMascota = MascotaRemote(databaseReference)
        mascotaRepository = MascotaRepository(remoteMascota)

        instancias()
        // Configuramos el RecyclerView
        configurarRecycler()
        // Llenamos la lista con las mascotas del propietario
        cargarMascotasUsuario()
    }

    private fun instancias() {
        listaMascotas = ArrayList()
        // Pasamos 'this' como tercer parámetro (el listener)
        adapterMascota = MascotaAdapter(listaMascotas, requireContext(), this)
    }

    private fun configurarRecycler(){
        // Asignamos el LayoutManager y el Adapter al XML
        binding.recyclerMascotas.layoutManager = LinearLayoutManager(context)
        binding.recyclerMascotas.adapter = adapterMascota
    }

    private fun cargarMascotasUsuario() {
        val auth = auth.currentUser?.uid

        if (auth.isNullOrEmpty()) {
            mostrarSnackbar("No se ha podido encontrar su identificador")
            return
        }

        /* Obtenemos el id del usuario para buscar sus mascotas:...
        */
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

    override fun onMascotaClick(idMascota: String, mascota: Mascota){
        // Pasamos la mascota seleccionada a la navegación para ver su perfil
        navegacionFragment(3, idMascota, mascota)
    }

    override fun onResume() {
        super.onResume()

        /* Acciones de los botones del fragment:
        -
        */
        binding.btnMascota.setOnClickListener {
            navegacionFragment(1)
        }
        binding.tvTitulo.setOnClickListener {
            navegacionFragment(2)
        }
    }

    /* NAVEGACION ENTRE FRAGMENTS

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
        }
    }
}