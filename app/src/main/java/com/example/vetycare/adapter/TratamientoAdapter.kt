package com.example.vetycare.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.vetycare.databinding.RecyclerTratamientoBinding
import com.example.vetycare.model.entities.Tratamiento

class TratamientoAdapter(
    var lista: ArrayList<Tratamiento>,
    var contexto: Context,
    private val listener: OnTratamientoListener
    ) : RecyclerView.Adapter<TratamientoAdapter.TratamientoHolder>() {


    inner class TratamientoHolder(val binding: RecyclerTratamientoBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TratamientoHolder {
        val binding = RecyclerTratamientoBinding.inflate(
            LayoutInflater.from(contexto), parent, false
        )
        return TratamientoHolder(binding)
    }

    override fun onBindViewHolder(holder: TratamientoHolder, position: Int) {
        val item = lista[position]

        // Mapeo de datos al diseño del item
        holder.binding.tvTitulo.text = "Tratamiento #${item.id?.uppercase()}"
        holder.binding.tvMedicacion.text = item.tipoTratamiento ?: "No especificado"
        holder.binding.tvFrecuencia.text = item.detallesMedicacion?.frecuencia ?: "Frecuencia no definida"

        holder.binding.btnVerMas.setOnClickListener {
            listener.onTratamientoClick(item)
        }
    }

    override fun getItemCount(): Int{
        return lista.size
    }

    interface OnTratamientoListener {
        fun onTratamientoClick(tratamiento: Tratamiento)
    }
}