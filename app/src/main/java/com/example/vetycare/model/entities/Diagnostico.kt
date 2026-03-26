package com.example.vetycare.model.entities

data class Diagnostico(
    var id: String? = null, // Se usa con 'var' para poder asignarlo al leer
    val estado: String? = null,
    val fechaCreacion: Long? = null,
    val fechaDiagnostico: String? = null,
    val fechaResolucion: String? = null,
    val gradoClinico: String? = null,
    val idCita: String? = null,
    val idMascota: String? = null,
    val idPatologia: String? = null,
    // Aquí está el objeto anidado, busca un objeto llamado patologia y mapea sus campos automaticamente dentro de esta clase.
    val patologia: Patologia? = Patologia()
) {
    constructor() : this(
        "","", 0L, "",
        "", "", "", "",
        "", Patologia()
    )
}
