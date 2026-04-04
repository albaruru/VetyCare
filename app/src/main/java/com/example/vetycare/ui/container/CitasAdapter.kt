/*package com.example.vetycare.ui.container

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.vetycare.databinding.RecyclerCalendarioCitaBinding
<<<<<<< HEAD
import com.example.vetycare.model.entities.Cita
import com.example.vetycare.model.enums.TipoCita
=======
import com.example.vetycare.model.TipoCita
>>>>>>> feature/visual

class CitasAdapter : ListAdapter<CitaConNombres, CitasAdapter.CitaViewHolder>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<CitaConNombres>() {
            override fun areItemsTheSame(old: CitaConNombres, new: CitaConNombres) =
                old.cita.id == new.cita.id
            override fun areContentsTheSame(old: CitaConNombres, new: CitaConNombres) =
                old == new
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
        val item = getItem(position)
        with(holder.binding) {
            // Nombres reales en lugar de IDs
            tvMascota.text = item.nombreMascota
            tvClinica.text = item.nombreClinica
            tvTipo.text = item.cita.tipoCita
            tvMotivo.text = item.cita.motivoConsulta
            tvFecha.text = item.cita.fechaHoraInicio.replace("T", "  ")
            tvRecordatorio.text = item.cita.estadoCita

            // Color según tipo de cita
            val colorRes = when (item.cita.tipoCita.lowercase()) {
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
}*/