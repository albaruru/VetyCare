package com.example.vetycare.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.vetycare.databinding.RecyclerCalendarioCitaBinding
import com.example.vetycare.model.entities.Cita
import com.example.vetycare.model.enums.TipoCita
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class CitaAdapter(
    var lista: ArrayList<Cita>,
    var contexto: Context,
    private val listener: OnCitaListener
    ) : RecyclerView.Adapter<CitaAdapter.CitaHolder>() {

    interface OnCitaListener {
        fun onCancelarClick(idCita: String)
    }

        inner class CitaHolder(val binding: RecyclerCalendarioCitaBinding) :
            RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CitaHolder {
            val binding = RecyclerCalendarioCitaBinding.inflate(
                LayoutInflater.from(contexto), parent, false
            )
            return CitaHolder(binding)
        }

        override fun onBindViewHolder(holder: CitaHolder, position: Int) {
            val item = lista[position]

            // Mapeo de datos básicos
            holder.binding.tvMascota.text = item.mascota?.nombre ?: "Sin nombre"
            holder.binding.tvClinica.text = item.clinica?.nombre ?: "VetyCare Central"
            holder.binding.tvTipo.text = item.tipoCita
            holder.binding.etMotivo.setText(item.motivoConsulta) // Usamos et_motivo del XML

            // Para que en el campo de veterinario aparezca su nombre y apellidos
            val nombreVet = item.veterinario?.nombre ?: ""
            val apellidoVet = item.veterinario?.apellido ?: ""
            val nombreCompleto = "$nombreVet $apellidoVet".trim()
            holder.binding.tvVeterinario.text = if (nombreCompleto.isNotEmpty()) nombreCompleto else "Asignando..."

            // Para que en el campo de Hora de la cita, solo aparezca la hora en este formato (HH:mm)
            val horaFormateada =
                LocalDateTime.parse(item.fechaHoraInicio)
                    .format(DateTimeFormatter.ofPattern("HH:mm"))
            holder.binding.tvFecha.text = horaFormateada

            // Lógica para el botón de cancelar cita
            holder.binding.btnCancelar.setOnClickListener {
                item.id?.let { id ->
                    listener.onCancelarClick(id)
                }
            }

            // Configurar el color de la franja según el tipo de cita
            val color = when (item.tipoCita?.uppercase()) {
                "REVISIÓN", "REVISION" -> TipoCita.REVISION.colorRes
                "VACUNACIÓN", "VACUNACION" -> TipoCita.VACUNACION.colorRes
                "CONSULTA GENERAL", "CONSULTA" -> TipoCita.CONSULTA.colorRes
                "PRUEBAS" -> TipoCita.PRUEBAS.colorRes
                "COMPRAR MÁS MEDICAMENTOS", "MEDICAMENTOS" -> TipoCita.MEDICAMENTOS.colorRes
                else -> com.example.vetycare.R.color.botones // Color por defecto
            }
            holder.binding.vTipoCita.setBackgroundColor(ContextCompat.getColor(contexto, color))
        }

        override fun getItemCount(): Int{
            return lista.size
        }

        fun actualizarLista(newList: List<Cita>) {
            lista.clear()
            lista.addAll(newList)
            notifyDataSetChanged()
        }
    }