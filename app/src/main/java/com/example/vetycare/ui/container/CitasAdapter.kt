package com.example.vetycare.ui.container

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.vetycare.databinding.RecyclerCalendarioCitaBinding
import com.example.vetycare.model.Cita
import com.example.vetycare.model.TipoCita

class CitasAdapter : ListAdapter<Cita, CitasAdapter.CitaViewHolder>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Cita>() {
            override fun areItemsTheSame(old: Cita, new: Cita) = old.id == new.id
            override fun areContentsTheSame(old: Cita, new: Cita) = old == new
        }
    }

    inner class CitaViewHolder(val binding: RecyclerCalendarioCitaBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CitaViewHolder {
        val binding = RecyclerCalendarioCitaBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CitaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CitaViewHolder, position: Int) {
        val cita = getItem(position)
        with(holder.binding) {

            // Mascota e IDs
            tvMascota.text = cita.idMascota
            tvClinica.text = cita.idClinica
            tvTipo.text = cita.tipoCita
            tvMotivo.text = cita.motivoConsulta
            tvFecha.text = cita.fechaHoraInicio
                .replace("T", "  ")  // "2025-01-20  10:00:00"
            tvRecordatorio.text = cita.estadoCita

            // Colorea el fondo de tvTipo según el tipo de cita
            val colorRes = when (cita.tipoCita.lowercase()) {
                "vacunacion", "vacunación" -> TipoCita.VACUNACION.colorRes
                "revision", "revisión"    -> TipoCita.REVISION.colorRes
                "consulta"                -> TipoCita.CONSULTA.colorRes
                "pruebas"                 -> TipoCita.PRUEBAS.colorRes
                "medicamentos"            -> TipoCita.MEDICAMENTOS.colorRes
                else                      -> null
            }
            colorRes?.let {
                tvTipo.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(root.context, it)
                )
            }
        }
    }
}