package com.example.vetycare.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.vetycare.R
import com.example.vetycare.databinding.RecyclerClinicaBinding
import com.example.vetycare.model.entities.Clinica

class ClinicaAdapter(
    var lista: ArrayList<Clinica>,
    var contexto: Context,
    private val listener: OnClinicaListener
) : RecyclerView.Adapter<ClinicaAdapter.ClinicaHolder>() {

    interface OnClinicaListener{
        fun onClinicaClick(clinica: Clinica)
    }

    inner class ClinicaHolder(val binding: RecyclerClinicaBinding):
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClinicaHolder {
        val binding = RecyclerClinicaBinding.inflate(
            LayoutInflater.from(contexto), parent, false)
        return ClinicaHolder(binding)
    }

    override fun onBindViewHolder(holder: ClinicaAdapter.ClinicaHolder, position: Int) {
        val item: Clinica = lista[position]

        holder.binding.tvNombreClinica.text = item.nombre
        holder.binding.tvProvincia.text = item.provincia
        holder.binding.tvDireccion.text = item.direccion
        holder.binding.tvTelefono.text = item.telefono.toString()

        // Cargar imagen de la clínica con Glide
        Glide.with(contexto)
            .load(item.url) // No carga nada porque no tenemos foto de clinica
            .placeholder(R.mipmap.logo_vetycare) // Por defecto carga el logo de vetycare
            .into(holder.binding.ivLogoClinica)
    }

    override fun getItemCount(): Int {
        return lista.size
    }

}