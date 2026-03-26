package com.example.vetycare.ui.container

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetycare.model.Cita
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Clase que agrupa la cita con los nombres resueltos
data class CitaConNombres(
    val cita: Cita,
    val nombreMascota: String,
    val nombreClinica: String
)

class CitasViewModel : ViewModel() {

    private val repository = CitasRepository()

    private val _todasLasCitas = MutableStateFlow<List<Cita>>(emptyList())
    val todasLasCitas: StateFlow<List<Cita>> = _todasLasCitas

    private val _citasDelDia = MutableStateFlow<List<CitaConNombres>>(emptyList())
    val citasDelDia: StateFlow<List<CitaConNombres>> = _citasDelDia

    init {
        cargarCitas()
    }

    private fun cargarCitas() {
        viewModelScope.launch {
            _todasLasCitas.value = repository.getCitasDelUsuario()
        }
    }

    fun seleccionarDia(fecha: String) {
        viewModelScope.launch {
            val citasFiltradas = _todasLasCitas.value.filter {
                it.fechaHoraInicio.startsWith(fecha)
            }
            // Resuelve los nombres de cada cita
            val citasConNombres = citasFiltradas.map { cita ->
                CitaConNombres(
                    cita = cita,
                    nombreMascota = repository.getNombreMascota(cita.idMascota),
                    nombreClinica = repository.getNombreClinica(cita.idClinica)
                )
            }
            _citasDelDia.value = citasConNombres
        }
    }
}