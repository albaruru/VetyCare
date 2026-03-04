package com.example.vetycare.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.example.vetycare.R

class ConfirmacionDialog : DialogFragment () {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder : AlertDialog.Builder = AlertDialog.Builder(requireContext())

        builder.setTitle("CONFIRMACIÓN")
        builder.setMessage("¿Estás seguro que deseas continuar?")
        builder.setPositiveButton("Aceptar") {_,_ ->
            findNavController().navigate(R.id.action_confirmacionDialog_to_InicioFragment)
        }
        builder.setNegativeButton("Cancelar",null)

        return builder.create()
    }
}