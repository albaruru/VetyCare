package com.example.vetycare.ui.fragment.inicio

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.vetycare.R
import com.example.vetycare.databinding.FragmentInicioRecPassBinding
import com.example.vetycare.navigation.NavigatorInicio
import com.example.vetycare.ui.dialog.CancelacionDialog
import com.example.vetycare.ui.dialog.ConfirmacionDialog
import com.example.vetycare.utils.FirebaseUtils
import com.example.vetycare.utils.mostrarSnackbar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class InicioRecPassFragment : Fragment() {
    private lateinit var binding : FragmentInicioRecPassBinding
    private val keyCancelacion = "cancelacion_recuperacion"
    private lateinit var auth : FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase

    override fun onAttach(context: Context) {
        super.onAttach(context)
        auth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance(FirebaseUtils.URL_RTDB)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Dialog Cancelación
        parentFragmentManager.setFragmentResultListener(keyCancelacion, this) {_, bundle ->
            val cancelado = bundle.getBoolean(CancelacionDialog.KEY_CANCELADO)
            if (cancelado) {
                navegacionFragment(1)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View? {
        binding = FragmentInicioRecPassBinding.inflate(layoutInflater, container,false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        /* Acciones de los botones del fragment:
        - Botón Enviar => Recogeremos el correo y le mandaremos un email para la recuperación de contraseña
        - Botón Volver => Descarta cualquier información introducida en nuestros bloques de texto y volvemos a la pantalla login
        */
        binding.btnEnviar.setOnClickListener {
            // Solo si la validación es correcta, mostramos el diálogo de confirmación
            recuperacionPassFirebase()
        }
        binding.btnVolver.setOnClickListener {
            mensaje("cancelacion")
        }
    }

    /* NAVEGACION ENTRE FRAGMENTS
    * */
    fun navegacionFragment(num: Int) {
        when (num) {
            1 -> NavigatorInicio.InicioRecPass_to_InicioPrincipal(this)
        }
    }
    fun mensaje (tipo: String) {
        when (tipo) {
            "cancelacion" -> {
                /* Explicación del metodo CancelacionDialog.nuevoDialog(...)

                Aquí hacemos lo siguiente:
                1. Creamos una instancia del diálogo
                2. Le pasamos título, mensaje y clave. (Si no se rellenan, se pondrán los valores por defecto del Dialog)
                3. Mostramos en pantalla nuestra alerta.

                */
                CancelacionDialog.nuevoDialog(
                    "CANCELACION RECUPERAR CONTRASEÑA",
                    "¿Deseas cancelar el proceso? \nLos cambios no se guardarán.",
                    keyCancelacion
                ).show(parentFragmentManager,"CancelacionDialog")
            }
        }
    }

    // FUNCION PARA LA RECUPERACIÓN DE CONTRASEÑA
    fun recuperacionPassFirebase(): Boolean {
        val correo = binding.etCorreo.text.toString().trim()


        // Validación antes de llamar a Firebase
        if(correo.isEmpty()){
            mostrarSnackbar("Por favor, introduce tu correo electrónico.")
            return false
        }

        // Llamada al metodo específico de Auth para cambiar la contraseña
        auth.sendPasswordResetEmail(correo)
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    // Si Firebase encuentra el correo y manda el email
                    mostrarSnackbar("Correo de recuperación enviado a: $correo")
                    navegacionFragment(1) // Navegamos al InicioPrincipal
                } else {
                    // Si el correo no existe en el Auth o hay error de conexion
                    mostrarSnackbar("Error: No se ha podido enviar el correo. Verifica que existe")
            }
        }
        return true
    }
}