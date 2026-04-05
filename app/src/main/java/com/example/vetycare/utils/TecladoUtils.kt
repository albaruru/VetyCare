package com.example.vetycare.utils

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment

fun Fragment.ocultarTeclado() {
    val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    val token = requireActivity().currentFocus?.windowToken ?: view?.windowToken
    token?.let {
        imm.hideSoftInputFromWindow(it,0)
    }
}