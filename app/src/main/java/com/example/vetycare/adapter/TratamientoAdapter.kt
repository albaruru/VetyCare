package com.example.vetycare.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.vetycare.databinding.RecyclerTratamientoBinding
import com.example.vetycare.model.entities.Tratamiento

/* EXPLICACIÓN DE LA CLASE <TratamientoAdapter()> : despliega para leer...
    Clase adaptador encargada de gestionar la visualización de los tratamientos médicos en un listado.
    Vicula los datos de medicación y frecuencia con su representación visual en la interfaz, permitiendo
    al usuario consultar de forma rápida las pautas sanitarias de sus mascotas.
 */
class TratamientoAdapter(
    var lista: ArrayList<Tratamiento>,
    var contexto: Context,
    private val listener: OnTratamientoListener
    ) : RecyclerView.Adapter<TratamientoAdapter.TratamientoHolder>() {

    /* EXPLICACIÓN DE LA CLASE <TratamientoHolder()> : despliega para leer...
        Clase interna que actúa como contenedor de las referencias visuales de cada tarjeta de tratamiento.
        Utiliza el objeto binding para acceder a los componentes del XML de manera directa y eficiente,
        evitando búsquedas repetitivas en la jerarquía de vistas.
    */
    inner class TratamientoHolder(val binding: RecyclerTratamientoBinding) :
        RecyclerView.ViewHolder(binding.root)

    /* EXPLICACIÓN DEL METODO <onCreateViewHolder()> : despliega para leer...
        Este metodo se encarga de inflar el diseño XML de cada tarjeta de tratamiento individual.
        Crea y devuelve una instancia de TratamientoHolder con la vista preparada, estableciendo
        la base necesaria para la posterior vinculación de los datos médicos.
    */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TratamientoHolder {
        val binding = RecyclerTratamientoBinding.inflate(
            LayoutInflater.from(contexto), parent, false
        )
        return TratamientoHolder(binding)
    }

    /* EXPLICACIÓN DEL METODO <onBindViewHolder()> : despliega para leer...
        Vinvula los atributos del tratamiento (ID, medicación y frecuencia) con los campos de texto del layout.
        Configura dinámicamente el contenido de la tarjeta y establece el listener en el botón
        de acción para permitir la navegación hacia el detalle completo del tratamiento.
    */
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

    /* EXPLICACIÓN DEL METODO <getItemCount()> : despliega para leer...
        Retorna la cantidad total de tratamientos contenidos en la lista proporcionada al adaptador.
        Este valor es esencial para que el componente RecyclerView conozca el número de elementos
        que debe renderizar y gestionar en la pantalla del dispositivo.
    */
    override fun getItemCount(): Int{
        return lista.size
    }

    /* EXPLICACIÓN DE LA INTERFAZ <OnTratamientoListener()> : despliega para leer...
        Interfaz de comunicación diseñada para capturar la interacción del usuario con un tratamiento.
        Define el metodo necesario para notificar al fragmento qué tratamiento ha sido seleccionado
        para abrir su vista de información detallada.
     */
    interface OnTratamientoListener {
        fun onTratamientoClick(tratamiento: Tratamiento)
    }
}