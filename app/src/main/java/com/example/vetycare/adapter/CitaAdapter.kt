package com.example.vetycare.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.vetycare.databinding.RecyclerCalendarioCitaBinding
import com.example.vetycare.model.entities.Cita
import com.example.vetycare.model.enums.TipoCita
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/* EXPLICACIÓN DE LA CLASE <CitaAdapter()> : despliega para leer...
    Clase adaptador encargada de vincular los datos de las citas médicas con la interfaz del RecyclerView.
 */
class CitaAdapter(
    var lista: ArrayList<Cita>,
    var contexto: Context,
    private val listener: OnCitaListener
    ) : RecyclerView.Adapter<CitaAdapter.CitaHolder>() {

    /* EXPLICACIÓN DE LA INTERFAZ <OnCitaListener()> : despliega para leer...
        Interfaz para comunicar el evento de cancelación al fragmento.
     */
    interface OnCitaListener {
        fun onCancelarClick(idCita: String)
    }

    /* EXPLICACIÓN DE LA CLASE <CitaHolder()> : despliega para leer...
        Contenedor de vistas que enlaza el layout de la tarjeta de cita.
     */
    inner class CitaHolder(val binding: RecyclerCalendarioCitaBinding) :
        RecyclerView.ViewHolder(binding.root)

    /* EXPLICACIÓN DEL METODO <onCreateViewHolder()> : despliega para leer...
        Este metodo se encarga de inflar el diseño XML de cada tarjeta individual utilizando ViewBinding.
        Crea y devuelve una instancia de CitaHolder, la cual almacenará las referencias de las vistas
        para que el sistema no tenga que buscarlas repetidamente, mejorando la fluidez del listado.
    */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CitaHolder {
        val binding = RecyclerCalendarioCitaBinding.inflate(
            LayoutInflater.from(contexto), parent, false
        )
        return CitaHolder(binding)
    }

    /* EXPLICACIÓN DEL METODO <onBindViewHolder()> : despliega para leer...
        Vinvula los datos de una cita específica con los elementos visuales de la tarjeta correspondiente.
        Realiza el formateo de la hora, concatena los datos del veterinario, configura el listener del
        botón de cancelación y aplica colores dinámicos según el tipo de consulta médica detectado.
    */
    override fun onBindViewHolder(holder: CitaHolder, position: Int) {
        val item = lista[position]

        // Mapeo de datos básicos
        holder.binding.tvMascota.text = item.mascota?.nombre ?: "Sin nombre"
        holder.binding.tvClinica.text = item.clinica?.nombre ?: "VetyCare Central"
        holder.binding.tvTipo.text = item.tipoCita
        holder.binding.etMotivo.setText(item.motivoConsulta)

        // Para que en el campo de veterinario aparezca su nombre y apellidos
        val nombreVet = item.veterinario?.nombre ?: ""
        val apellidoVet = item.veterinario?.apellido ?: ""
        val nombreCompleto = "$nombreVet $apellidoVet".trim()
        holder.binding.tvVeterinario.text = if (nombreCompleto.isNotEmpty()) nombreCompleto else "Asignando..."

        // Para que en el campo de Hora de la cita, solo aparezca la hora en este formato (HH:mm)
        val horaFormateada =
            LocalDateTime.parse(item.fechaHoraInicio)
                .format(DateTimeFormatter.ofPattern("HH:mm"))
        holder.binding.tvFecha.text = horaFormateada

        // Configuración del botón de cancelación
        holder.binding.btnCancelar.setOnClickListener {
            item.id?.let { id ->
                listener.onCancelarClick(id)
            }
        }

        // Selección de color para la franja lateral según la categoría de la cita
        val color = when (item.tipoCita?.uppercase()) {
            "REVISIÓN", "REVISION" -> TipoCita.REVISION.colorRes
            "VACUNACIÓN", "VACUNACION" -> TipoCita.VACUNACION.colorRes
            "CONSULTA GENERAL", "CONSULTA" -> TipoCita.CONSULTA.colorRes
            "PRUEBAS" -> TipoCita.PRUEBAS.colorRes
            "COMPRAR MÁS MEDICAMENTOS", "MEDICAMENTOS" -> TipoCita.MEDICAMENTOS.colorRes
            else -> com.example.vetycare.R.color.botones // Color por defecto
        }
        holder.binding.vTipoCita.setBackgroundColor(ContextCompat.getColor(contexto, color))
    }

    /* EXPLICACIÓN DEL METODO <getItemCount()> : despliega para leer...
        Devuelve el tamaño total de la lista de citas que el adaptador tiene actualmente en memoria.
        Este dato es fundamental para que el RecyclerView sepa cuántas tarjetas debe renderizar
        y pueda gestionar correctamente el scroll y la memoria interna de la lista.
    */
    override fun getItemCount(): Int{
        return lista.size
    }

    /* EXPLICACIÓN DEL METODO <actualizarLista()> : despliega para leer...
        Sustituye la lista de datos actual por una nueva colección proporcionada desde el fragmento.
        Limpia el contenedor principal, añade todos los elementos nuevos y notifica al adaptador
        que los datos han cambiado para que la interfaz se refresque visualmente al instante.
    */
    fun actualizarLista(newList: List<Cita>) {
        lista.clear()
        lista.addAll(newList)
        notifyDataSetChanged()
    }
    }