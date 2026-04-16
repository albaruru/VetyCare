package com.example.vetycare.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.vetycare.databinding.RecyclerInformeBinding
import com.example.vetycare.model.entities.Diagnostico

class InformeAdapter(
    var lista: ArrayList<Diagnostico>,
    var contexto: Context,
    private val listener: OnInformeListener
) : RecyclerView.Adapter<InformeAdapter.InformeHolder>() {

    interface OnInformeListener {
        fun onInformeClick(informe: Diagnostico)
    }

    inner class InformeHolder(val binding: RecyclerInformeBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InformeHolder {
        val binding = RecyclerInformeBinding.inflate(
            LayoutInflater.from(contexto), parent, false
        )
        return InformeHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: InformeHolder, position: Int) {
        val item = lista[position]

        holder.binding.tvTitulo.text = "Informe #${item.id?.uppercase()}"
        holder.binding.tvConcepto.text = item.patologia?.nombre ?: "Sin concepto"
        holder.binding.tvFrecuencia.text = item.fechaDiagnostico // Se usa el campo de fecha

        // Programar el clic en btnVerMas
        holder.binding.btnVerMas.setOnClickListener {
            listener.onInformeClick(item)
        }
    }

    override fun getItemCount(): Int{
        return lista.size
    }
}