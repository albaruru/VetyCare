package com.example.vetycare.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.vetycare.R
import com.example.vetycare.databinding.RecyclerMascotaBinding
import com.example.vetycare.model.entities.Mascota

/* EXPLICACIÓN DE LA CLASE <MascotaAdapter()> : despliega para leer...
    Clase adaptador encargada de gestionar la lista de mascotas del propietario en un RecyclerView.
    Permite visualizar el nombre y la fotografía de cada animal, facilitando el acceso a su perfil
    detallado mediante la gestión eficiente de una colección de pares ID-Objeto.
 */
class MascotaAdapter(
    var lista: ArrayList<Pair<String, Mascota>>,
    var contexto: Context,
    private val listener: OnMascotaListener
    ) : RecyclerView.Adapter<MascotaAdapter.MascotaHolder>() {

    /* EXPLICACIÓN DE LA INTERFAZ <OnMascotaListener()> : despliega para leer...
        Interfaz de comunicación que captura la selección de una mascota específica por parte del usuario.
        Envía tanto el identificador único como el objeto completo hacia el fragmento para procesar
        correctamente la navegación al contenedor de la mascota seleccionada.
    */
    interface OnMascotaListener {
        fun onMascotaClick(idMascota: String, mascota: Mascota)
    }

    /* EXPLICACIÓN DE LA CLASE <MascotaHolder()> : despliega para leer...
        Clase interna que sostiene las referencias de los componentes visuales de cada elemento de la lista.
        Utiliza ViewBinding para optimizar el acceso a las vistas de la tarjeta de mascota,
        mejorando el rendimiento general durante el desplazamiento por el listado.
     */
    inner class MascotaHolder(val binding: RecyclerMascotaBinding) :
        RecyclerView.ViewHolder(binding.root)

    /* EXPLICACIÓN DEL METODO <onCreateViewHolder()> : despliega para leer...
        Este metodo infla el diseño XML de la tarjeta de mascota y crea una instancia del Holder.
        Prepara el contenedor visual necesario para que los datos de los animales puedan ser
        renderizados correctamente dentro del flujo del RecyclerView.
    */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MascotaHolder {
        val binding = RecyclerMascotaBinding.inflate(
            LayoutInflater.from(contexto), parent, false
        )
        return MascotaHolder(binding)
    }

    /* EXPLICACIÓN DEL METODO <onBindViewHolder()> : despliega para leer...
        Vincula la información de la mascota con los elementos de la interfaz en cada posición.
        Utiliza la librería Glide para la carga eficiente de imágenes desde URL y configura el
        evento de clic en el botón para delegar la navegación al fragmento mediante el listener.
    */
    override fun onBindViewHolder(holder: MascotaHolder, position: Int) {
        val item = lista[position]
        val idMascota = item.first
        val mascota = item.second

        // Asignación del nombre de la mascota al botón principal
        holder.binding.btnMascota.text = mascota.nombre

        val urlImagen = mascota.urlFotoMasc

        // Lógica de carga de imagen con Glide
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

    /* EXPLICACIÓN DEL METODO <getItemCount()> : despliega para leer...
        Retorna la cantidad total de elementos que contiene la lista de mascotas actual.
        Este valor permite al RecyclerView calcular el espacio necesario y gestionar la carga
        y reciclaje de los elementos en la pantalla del dispositivo.
    */
    override fun getItemCount(): Int = lista.size

    /* EXPLICACIÓN DEL METODO <actualizarLista()> : despliega para leer...
        Sustituye los datos del adaptador por una nueva colección de mascotas actualizada.
        Limpia la lista antigua, añade los nuevos registros y notifica el cambio para que
        la interfaz se refresque visualmente de manera inmediata.
    */
    fun actualizarLista(nuevaLista: List<Pair<String, Mascota>>) {
        lista.clear()
        lista.addAll(nuevaLista)
        notifyDataSetChanged()
    }
}