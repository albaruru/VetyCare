package com.example.vetycare.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.vetycare.databinding.RecyclerCalendarioCitaBinding
import com.example.vetycare.model.entities.Cita
import com.example.vetycare.model.enums.TipoCita

class CitaAdapter(
    var lista: ArrayList<Cita>,
    var contexto: Context
    ) : RecyclerView.Adapter<CitaAdapter.CitaHolder>() {

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
            holder.binding.tvVeterinario.text = item.veterinario?.nombre ?: "Asignando..."
            holder.binding.tvFecha.text = "${item.fechaHoraInicio}"

            // Configurar el color de la franja según el tipo de cita
            val color = when (item.tipoCita?.uppercase()) {
                "VACUNACION" -> TipoCita.VACUNACION.colorRes
                "REVISION" -> TipoCita.REVISION.colorRes
                "CONSULTA" -> TipoCita.CONSULTA.colorRes
                "PRUEBAS" -> TipoCita.PRUEBAS.colorRes
                "MEDICAMENTOS" -> TipoCita.MEDICAMENTOS.colorRes
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