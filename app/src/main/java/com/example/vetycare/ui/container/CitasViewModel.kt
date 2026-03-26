/*package com.example.vetycare.ui.container

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetycare.model.entities.Cita
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CitasViewModel : ViewModel() {

    private val repository = CitasRepository()

    private val _todasLasCitas = MutableStateFlow<List<Cita>>(emptyList())
    val todasLasCitas: StateFlow<List<Cita>> = _todasLasCitas

    private val _citasDelDia = MutableStateFlow<List<Cita>>(emptyList())
    val citasDelDia: StateFlow<List<Cita>> = _citasDelDia

    init {
        cargarCitas()
    }

    private fun cargarCitas() {
        viewModelScope.launch {
            _todasLasCitas.value = repository.getCitasDelUsuario()
        }
    }

    fun seleccionarDia(fecha: String) {
        _citasDelDia.value = _todasLasCitas.value.filter {
            it.fechaHoraInicio.startsWith(fecha)
        }
    }
}*/