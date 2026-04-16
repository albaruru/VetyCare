package com.example.vetycare.utils

import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

    /**
     * Muestra un Snackbar desde cualquier Fragment
     * @param mensaje El texto a mostrar
     * @param duracion Duración del mensaje (por defecto LENGTH_SHORT)
     */

    fun Fragment.mostrarSnackbar(mensaje: String, duracion: Int = Snackbar.LENGTH_SHORT) {
        view?.let {
            Snackbar.make(
                it,
                mensaje,
                duracion)
                .show()
        }
    }

    /**
     * También he creado una para View por si queremos usarla fuera de Fragments
     */
    fun View.mostrarSnackbar(mensaje: String, duracion: Int = Snackbar.LENGTH_SHORT) {
        Snackbar.make(
            this,
            mensaje,
            duracion)
            .show()
    }
