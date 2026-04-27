<div align="center">

<picture>
     <source media="(prefers-color-scheme: dark)" srcset="URL_LOGO_BLANCO">
     <img src="URL_LOGO_NEGRO" width="120" alt="VetyCare logo">
</picture>

# VetyCare

**Aplicación Android para la gestión integral del cuidado veterinario de mascotas**

![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=flat-square&logo=kotlin&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?style=flat-square&logo=android&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-FFCA28?style=flat-square&logo=firebase&logoColor=black)
![Estado](https://img.shields.io/badge/estado-en%20desarrollo-orange?style=flat-square)

</div>

---

## Descripción

VetyCare es una aplicación móvil nativa para Android desarrollada en Kotlin que permite a los propietarios de mascotas gestionar de forma centralizada toda la información relacionada con la salud y el cuidado de sus animales. Desde el registro de vacunaciones hasta la gestión de citas veterinarias, VetyCare ofrece una interfaz limpia y accesible pensada para el día a día.

---

## Funcionalidades principales

- **Autenticación** — registro, inicio de sesión y recuperación de contraseña por correo electrónico
- **Gestión de mascotas** — alta, edición, consulta y eliminación del perfil de cada mascota (CRUD completo)
- **Gestión de citas** — creación, modificación y seguimiento de citas veterinarias categorizadas por tipo: vacunación, revisión, consulta, pruebas y medicamentos
- **Historial de tratamientos e informes** — registro del historial médico y tratamientos asociados a cada mascota
- **Mapa de clínicas** — localización de clínicas veterinarias disponibles en toda España
- **Calendario interactivo** — visualización de citas por mascota y por tipo, con navegación mensual
- **Navegación fluida** — arquitectura de pantallas con transiciones y validaciones integradas

---

## Tecnologías

| Capa | Tecnología |
|---|---|
| Lenguaje | Kotlin |
| Plataforma | Android nativo |
| Build system | Gradle con Kotlin DSL (`build.gradle.kts`) |
| Base de datos | Firebase Realtime Database |
| Autenticación | Firebase Authentication |
| Control de versiones | Git + GitHub |
| Gestión de proyecto | GitHub Projects |

---

## Estructura del proyecto

```
VetyCare/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/           # Código fuente Kotlin
│   │   │   ├── res/
│   │   │   │   ├── layout/     # Pantallas XML
│   │   │   │   ├── drawable/   # Estilos de botones, formularios y fondos
│   │   │   │   ├── values/     # Colores, strings, estilos tipográficos
│   │   │   │   └── font/       # Familia tipográfica Tommy Soft
│   │   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

---

## Base de datos

VetyCare utiliza **Firebase Realtime Database** como backend de datos y **Firebase Authentication** para la gestión de identidades. La base de datos sigue una estructura plana con índices de relación entre colecciones para optimizar las consultas en tiempo real.

### Colecciones principales

| Colección | Descripción |
|---|---|
| `propietarios` | Datos del usuario registrado, vinculado a su `authUid` de Firebase Auth |
| `mascotas` | Perfil de cada mascota asociada a un propietario |
| `citas` | Citas veterinarias con tipo, estado, fechas, clínica y veterinario |
| `diagnosticos` | Diagnósticos clínicos vinculados a citas y mascotas |
| `tratamientos` | Tratamientos asociados a diagnósticos, con fechas y tipo terapéutico |
| `medicamentos` | Catálogo de medicamentos utilizados en los tratamientos |
| `patologias` | Catálogo de patologías con categoría, gravedad y clasificación clínica |
| `clinicas` | Clínicas veterinarias con coordenadas, dirección y datos de contacto |
| `veterinarios` | Veterinarios con especialidad, número de colegiado y clínica asignada |

### Índices de relación

Firebase Realtime Database no soporta joins nativos. Para mantener consultas eficientes, se utilizan índices planos de relación:

| Índice | Descripción |
|---|---|
| `citasPorMascota` | Citas indexadas por `idMascota` |
| `citasPorVeterinario` | Citas indexadas por `idVeterinario` |
| `tratamientosPorMascota` | Tratamientos indexados por `idMascota` |
| `propietariosPorAuthUid` | Resolución de `authUid` al identificador interno del propietario |
| `veterinariosPorClinica` | Veterinarios agrupados por clínica |
| `veterinariosPorColegio` | Veterinarios agrupados por colegio profesional |

---

## Diseño

La interfaz de VetyCare sigue un sistema de diseño propio con paleta cromática en tonos beige y morado, diseñado para transmitir calma y confianza.

**Paleta de colores principal**

| Nombre | Hex | Uso |
|---|---|---|
| `content_main` | `#F5F1E8` | Fondo principal |
| `menu` | `#A598DC` | Barra de navegación |
| `botones` | `#B3A7E3` | Botones primarios |
| `botones_claros` | `#DEDEDE` | Botones secundarios |
| `formulario` | `#ECECEC` | Campos de entrada |
| `Vacunacion` | `#4FC3F7` | Etiqueta tipo cita |
| `Revision` | `#A5D6A7` | Etiqueta tipo cita |
| `Consulta` | `#FFF176` | Etiqueta tipo cita |
| `Pruebas` | `#FFCC80` | Etiqueta tipo cita |
| `Medicamentos` | `#F48FB1` | Etiqueta tipo cita |

**Tipografía**

La aplicación usa la familia **Tommy Soft** en tres pesos: Light, Regular y Medium, aplicada mediante estilos globales definidos en `res/values/styles.xml`.

---

## Instalación y ejecución

### Requisitos previos

- Android Studio Hedgehog o superior
- JDK 17
- Android SDK (API mínima recomendada: 26)
- Cuenta de Firebase con proyecto configurado y archivo `google-services.json` en `app/`

### Pasos

```bash
# 1. Clona el repositorio
git clone https://github.com/albaruru/VetyCare.git
cd VetyCare

# 2. Coloca el archivo google-services.json en la carpeta app/
# Descárgalo desde la consola de Firebase:
# Project Settings > Tu app Android > Descargar google-services.json

# 3. Abre el proyecto en Android Studio
# File > Open > selecciona la carpeta raíz del proyecto

# 4. Sincroniza Gradle y ejecuta la app
# Run > Run 'app'
```

---

## Planificación del desarrollo

El desarrollo se ha estructurado en 9 fases distribuidas a lo largo de aproximadamente 4 meses, con un total estimado de 175 a 206 horas de trabajo, considerando una media de 5 horas diarias de dedicación por parte del equipo.

| Fase | Descripción | Complejidad | Horas est. | Días aprox. |
|---|---|---|---|---|
| 0 | Idea y organización inicial | Baja | 10 h | 2 |
| 1 | Preparación del entorno | Baja–Media | 10–12 h | 2–3 |
| 2 | Revisión y análisis | Media | 18–20 h | 4 |
| 3 | Backend y base de datos | Alta | 50–55 h | 10–11 |
| 4 | Desarrollo frontend | Alta | 45–50 h | 9–10 |
| 5 | Integración backend + frontend | Alta | 30–35 h | 6–7 |
| 6 | Pruebas | Media | 14–18 h | 3–4 |
| 7 | Mejoras y refactorización | Media–Alta | 10–15 h | 2–3 |
| 8 | Documentación y entrega | Media | 12–18 h | 2–4 |

---

## Equipo

| Nombre | Rol principal |
|---|---|
| **Alba** | Frontend, diseño UI/UX, documentación, infraestructura GitHub, gestión del proyecto |
| **Sergio** | Base de datos, backend, integración, arquitectura del software, gestión del proyecto |
| **Carlos** | Base de datos, backend, integración, arquitectura del software, gestión del proyecto |

---

## Normas de contribución

Este proyecto sigue un flujo de trabajo basado en ramas por funcionalidad. Las ramas principales son `feature/visual`, `feature/backend`, `feature/database` y `develop`. Los merges a `main` requieren Pull Request revisado con los tres integrantes presentes.

**Formato de commits según la rama de trabajo**

| Prefijo | Significado |
|---|---|
| `[V]` | Trabajo en `feature/visual` |
| `[B]` | Trabajo en `feature/backend` |
| `[D]` | Trabajo en `feature/database` |
| `[DEV]` | Trabajo en `develop` |

**Formato de commits para merges y Pull Requests**

| Prefijo | Significado |
|---|---|
| `[V->B]` | Merge de `feature/visual` a `feature/backend` |
| `[B->D]` | Merge de `feature/backend` a `feature/database` |
| `[D->DEV]` | Merge de `feature/database` a `develop` |
| `[PR]` | Pull request de `develop` a `main` |

---

## Licencia

Este proyecto ha sido desarrollado con fines educativos. Todos los derechos reservados a sus autores.

---

<div align="center">
Desarrollado con dedicación por el equipo de VetyCare
</div>
