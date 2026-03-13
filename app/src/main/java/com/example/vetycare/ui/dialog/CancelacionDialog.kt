package com.example.vetycare.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.example.vetycare.R
import com.example.vetycare.navigation.NavigatorInicio

class CancelacionDialog : DialogFragment () {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder : AlertDialog.Builder = AlertDialog.Builder(requireContext())

        builder.setTitle("CANCELAR")
        builder.setMessage("¿Esta seguro de querer cancelar?")
        builder.setPositiveButton("Sí") {_,_ ->
            NavigatorInicio.DialogCancelacionToInicioPrincipal(this)
            // findNavController().navigate(R.id.action_cancelacionDialog_to_InicioFragment)
        }
        builder.setNegativeButton("No",null)

        return builder.create()
    }
}