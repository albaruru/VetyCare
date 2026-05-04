package com.example.vetycare.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.vetycare.R
import com.example.vetycare.databinding.RecyclerClinicaBinding
import com.example.vetycare.model.entities.Clinica

/* EXPLICACIÓN DE LA CLASE <ClinicaAdapter()> : despliega para leer...
    Clase adaptador encargada de vincular la lista de clínicas veterinarias con su representación visual.
    Permite mostrar la información de contacto y ubicación de cada centro, gestionando la carga
    de imágenes y los eventos de selección por parte del usuario.
 */
class ClinicaAdapter(
    var lista: ArrayList<Clinica>,
    var contexto: Context,
    private val listener: OnClinicaListener
) : RecyclerView.Adapter<ClinicaAdapter.ClinicaHolder>() {

    /* EXPLICACIÓN DE LA INTERFAZ <OnClinicaListener()> : despliega para leer...
        Interfaz de comunicación que define el evento de clic sobre una clínica de la lista.
        Permite enviar el objeto Clinica seleccionado de vuelta al fragmento para realizar
        acciones como llamadas telefónicas o navegación en el mapa.
     */
    interface OnClinicaListener{
        fun onClinicaClick(clinica: Clinica)
    }

    /* EXPLICACIÓN DE LA CLASE <ClinicaHolder()> : despliega para leer...
        Contenedor de vistas que enlaza los componentes del layout XML mediante ViewBinding.
        Mantiene las referencias de los elementos visuales para optimizar el rendimiento del
        listado al evitar llamadas repetitivas al metodo de búsqueda de vistas.
     */
    inner class ClinicaHolder(val binding: RecyclerClinicaBinding):
        RecyclerView.ViewHolder(binding.root)

    /* EXPLICACIÓN DEL METODO <onCreateViewHolder()> : despliega para leer...
        Se encarga de inflar el diseño XML específico para cada tarjeta de clínica.
        Crea y devuelve una instancia del ClinicaHolder con la vista inflada, preparando el
        contenedor que será utilizado para mostrar los datos de los centros veterinarios.
    */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClinicaHolder {
        val binding = RecyclerClinicaBinding.inflate(
            LayoutInflater.from(contexto), parent, false)
        return ClinicaHolder(binding)
    }

    /* EXPLICACIÓN DEL METODO <onBindViewHolder()> : despliega para leer...
        Vincula los datos técnicos de una clínica (nombre, dirección, etc.) con sus vistas.
        Gestiona la carga de la imagen corporativa mediante Glide, establece el texto de los
        campos y activa el listener para detectar la interacción del usuario con la tarjeta.
    */
    override fun onBindViewHolder(holder: ClinicaAdapter.ClinicaHolder, position: Int) {
        val item: Clinica = lista[position]

        // Asignación de textos informativos a la interfaz
        holder.binding.tvNombreClinica.text = item.nombre
        holder.binding.tvProvincia.text = item.provincia
        holder.binding.tvDireccion.text = item.direccion
        holder.binding.tvTelefono.text = item.telefono.toString()

        // Cargar imagen de la clínica con Glide
        Glide.with(contexto)
            .load(item.url)
            .placeholder(R.mipmap.logo_vetycare) // Por defecto carga el logo de vetycare
            .into(holder.binding.ivLogoClinica)

        holder.itemView.setOnClickListener {
            listener.onClinicaClick(item)
        }
    }

    /* EXPLICACIÓN DEL METODO <getItemCount()> : despliega para leer...
        Devuelve el número total de clínicas disponibles en la colección que maneja el adaptador.
        Informa al RecyclerView sobre la cantidad de elementos que debe renderizar en la pantalla
        para gestionar correctamente el desplazamiento y la memoria.
    */
    override fun getItemCount(): Int {
        return lista.size
    }

}