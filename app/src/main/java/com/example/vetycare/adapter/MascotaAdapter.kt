package com.example.vetycare.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.vetycare.R
import com.example.vetycare.databinding.RecyclerMascotaBinding
import com.example.vetycare.model.entities.Mascota

class MascotaAdapter(
    var lista: ArrayList<Pair<String, Mascota>>,
    var contexto: Context,
    private val listener: OnMascotaListener
    ) : RecyclerView.Adapter<MascotaAdapter.MascotaHolder>() {

    interface OnMascotaListener {
        fun onMascotaClick(idMascota: String, mascota: Mascota)
    }

    inner class MascotaHolder(val binding: RecyclerMascotaBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MascotaHolder {
        val binding = RecyclerMascotaBinding.inflate(
            LayoutInflater.from(contexto), parent, false
        )
        return MascotaHolder(binding)
    }

    override fun onBindViewHolder(holder: MascotaHolder, position: Int) {
        val item = lista[position]
        val idMascota = item.first
        val mascota = item.second

        holder.binding.btnMascota.text = mascota.nombre

        val urlImagen = mascota.urlFotoMasc

        if (!urlImagen.isNullOrEmpty()) {
            Glide.with(contexto)
                .load(urlImagen)
                .placeholder(R.drawable.img_mascotas)
                .error(R.drawable.img_mascotas)
                .into(holder.binding.ivFotoMascota)
        }
        else {
            holder.binding.ivFotoMascota.setImageResource(R.drawable.img_mascotas)
        }

        holder.binding.btnMascota.setOnClickListener {
            listener.onMascotaClick(idMascota, mascota)
        }
    }

    override fun getItemCount(): Int = lista.size

    fun actualizarLista(nuevaLista: List<Pair<String, Mascota>>) {
        lista.clear()
        lista.addAll(nuevaLista)
        notifyDataSetChanged()
    }
}