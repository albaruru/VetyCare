package com.example.vetycare.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.vetycare.R
import com.example.vetycare.databinding.RecyclerMascotaBinding
import com.example.vetycare.model.entities.Mascota

class MascotaAdapter(
    var lista: ArrayList<Mascota>,
    var contexto: Context,
    private val listener: OnMascotaListener
) : RecyclerView.Adapter<MascotaAdapter.MascotaHolder>() {

    interface OnMascotaListener {
        fun onMascotaClick(mascota: Mascota)
    }

    inner class MascotaHolder(val binding: RecyclerMascotaBinding):
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MascotaHolder {
        val binding:  RecyclerMascotaBinding = RecyclerMascotaBinding.inflate(
            LayoutInflater.from(contexto), parent, false)
        return MascotaHolder(binding)
    }

    override fun onBindViewHolder(
        holder: MascotaHolder,
        position: Int
    ) {
        val item: Mascota = lista[position]
        holder.binding.btnMascota.text = item.nombre

        // Cargar imagen de mascota con Glide
        val urlImagen = item.urlFotoMasc
        Glide.with(contexto)
            .load(urlImagen)
            .placeholder(R.mipmap.logo_vetycare) // TODO: PONER IMAGEN DE MASCOTA POR DEFECTO
            .into(holder.binding.ivFotoMascota)

        // Programamos el evento de clic del botón
        holder.binding.btnMascota.setOnClickListener {
            listener.onMascotaClick(item)
        }
    }

    override fun getItemCount(): Int{
        return lista.size
    }
}