package com.example.vetycare.model.entities

// TODO: ESTE SERÍA NUESTRO INFORME EN XML
data class Diagnostico(
    var id: String? = null, // Se usa con 'var' para poder asignarlo al leer
    val estado: String? = null,
    val fechaDiagnostico: String? = null,
    val fechaResolucion: String? = null,
    val gradoClinico: String? = null,
    val idCita: String? = null,
    val idMascota: String? = null,
    val idPatologia: String? = null,
    val importeTotal: Double? = null,
    val valoracion: String? = null, // Este recoge el informe redactado por el veterinario para mostrarlo al usuario.
    // Aquí está el objeto anidado, busca un objeto llamado patologia y mapea sus campos automaticamente dentro de esta clase.
    val patologia: Patologia? = Patologia(),
    val cita: Cita? = Cita(),
    val tratamiento: Tratamiento? = Tratamiento(),
    val medicamento: Medicamento? = Medicamento()
) {
    constructor() : this(
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        0.0,
        "",
        Patologia(),
        Cita(),
        Tratamiento(),
        Medicamento()
    )
}
