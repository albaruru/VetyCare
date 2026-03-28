package com.example.vetycare.ui.fragment.inicio

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.vetycare.R
import com.example.vetycare.databinding.FragmentInicioPrincipalBinding
import com.example.vetycare.navigation.NavigatorInicio
import com.example.vetycare.navigation.NavigatorRoot
import com.example.vetycare.utils.FirebaseUtils
import com.example.vetycare.utils.mostrarSnackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class InicioPrincipalFragment : Fragment() {
    private lateinit var binding : FragmentInicioPrincipalBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase

    override fun onAttach(context: Context) {
        super.onAttach(context)
        auth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance(FirebaseUtils.URL_RTDB)
    }

    override fun onCreateView (inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View? {
        binding = FragmentInicioPrincipalBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        /* Acciones de los botones del fragment:
        * - Boton entrar => Recoge nombre del usuario y contraseña. Verifica con la FireBase y entra en caso afirmativo.
        * -
        *  */
        binding.btnEntrar.setOnClickListener {
            comprobarInicioSesion()
        }
        binding.tvLinkRegistrate.setOnClickListener{ navegacionFragment(2) }
        binding.tvOlvideContrasenha.setOnClickListener { navegacionFragment(3) }
    }

    /* NAVEGACION ENTRE FRAGMENTS
    Metodo para navegar en las diferentes pantallas de nuestro fragment perteneciente al container inicio
    * */
    fun navegacionFragment(num : Int) {
        when (num) {
            1 -> NavigatorRoot.Inicio_to_Usuario(this) // Navega al Container Usuario
            2 -> NavigatorInicio.InicioPrincipal_to_InicioRegistro(this) // Navega al Fragment Inicio Registro Ususario
            3 -> NavigatorInicio.InicioPrincipal_to_InicioRecPass(this) // Navega al Fragment Inicio Recuperacion Contraseña
        }
    }

    /* FUNCION PARA COMPROBAR INICIO DE SESION
    * En este metodo realizamos la comprobación de la existencia de una cuenta en nuestra base de datos
    * */
    fun comprobarInicioSesion() {
        auth
            .signInWithEmailAndPassword(
                binding.etCorreo.text.toString(),
                binding.etContrasenha.text.toString()
            ).addOnCompleteListener {
                if (it.isSuccessful) {
                    mostrarSnackbar("Bienvenido a VetyCare!")
                    navegacionFragment(1)
                }
                else {
                    mostrarSnackbar("Correo o contraseña incorrectos")
                }
            }
    }
}