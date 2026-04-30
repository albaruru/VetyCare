package com.example.vetycare.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.vetycare.databinding.RecyclerInformeBinding
import com.example.vetycare.model.entities.Diagnostico

/* EXPLICACIÓN DE LA CLASE <InformeAdapter()> : despliega para leer...
    Clase adaptador encargada de gestionar la visualización de los informes médicos en un listado.
    Vicula los datos de los diagnósticos con su representación visual en la interfaz, permitiendo
    al usuario consultar de forma rápida los resultados de las consultas de sus mascotas.
 */
class InformeAdapter(
    var lista: ArrayList<Diagnostico>,
    var contexto: Context,
    private val listener: OnInformeListener
) : RecyclerView.Adapter<InformeAdapter.InformeHolder>() {

    /* EXPLICACIÓN DE LA INTERFAZ <OnInformeListener()> : despliega para leer...
        Interfaz de comunicación diseñada para capturar la interacción del usuario con un informe.
        Define el metodo necesario para notificar al fragmento qué diagnóstico ha sido seleccionado
        para abrir su vista de información detallada.
     */
    interface OnInformeListener {
        fun onInformeClick(informe: Diagnostico)
    }

    /* EXPLICACIÓN DE LA CLASE <InformeHolder()> : despliega para leer...
        Clase interna que actúa como contenedor de las referencias visuales de cada tarjeta de informe.
        Utiliza el objeto binding para acceder a los componentes del XML de manera directa y eficiente,
        evitando búsquedas repetitivas en la jerarquía de vistas.
     */
    inner class InformeHolder(val binding: RecyclerInformeBinding) :
        RecyclerView.ViewHolder(binding.root)

    /* EXPLICACIÓN DEL METODO <onCreateViewHolder()> : despliega para leer...
        Se encarga de inflar el archivo de diseño XML para cada elemento individual de la lista.
        Crea y devuelve una instancia de InformeHolder con la vista preparada, estableciendo
        la base necesaria para la posterior vinculación de los datos médicos.
    */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InformeHolder {
        val binding = RecyclerInformeBinding.inflate(
            LayoutInflater.from(contexto), parent, false
        )
        return InformeHolder(binding)
    }

    /* EXPLICACIÓN DEL METODO <onBindViewHolder()> : despliega para leer...
        Vinvula los atributos del diagnóstico (ID, patología y fecha) con los campos de texto del layout.
        Configura dinámicamente el contenido de la tarjeta y establece el listener en el botón
        de acción para permitir la navegación hacia el detalle completo del informe.
    */
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: InformeHolder, position: Int) {
        val item = lista[position]

        // Asignación de datos del diagnóstico a la vista
        holder.binding.tvTitulo.text = "Informe #${item.id?.uppercase()}"
        holder.binding.tvConcepto.text = item.patologia?.nombre ?: "Sin concepto"
        holder.binding.tvFrecuencia.text = item.fechaDiagnostico // Se usa el campo de fecha

        // Configuración del evento de clic para ver el detalle
        holder.binding.btnVerMas.setOnClickListener {
            listener.onInformeClick(item)
        }
    }

    /* EXPLICACIÓN DEL METODO <getItemCount()> : despliega para leer...
        Retorna la cantidad total de diagnósticos contenidos en la lista proporcionada al adaptador.
        Este valor es esencial para que el componente RecyclerView conozca el número de elementos
        que debe renderizar y gestionar en la pantalla del dispositivo.
    */
    override fun getItemCount(): Int{
        return lista.size
    }
}