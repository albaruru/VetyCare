package com.example.vetycare

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.example.vetycare.databinding.ActivityMainBinding

/* EXPLICACIÓN DE LA CLASE <MainActivity()> : despliega para leer...
    Punto de entrada principal de la aplicación que actúa como contenedor base para los fragmentos.
    Se encarga de inicializar el sistema de ViewBinding y de alojar el NavHostFragment
    que gestiona la navegación raíz entre los diferentes módulos del proyecto VetyCare.
 */
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    /* EXPLICACIÓN DEL METODO <onCreate()> : despliega para leer...
        Inicializa la actividad inflando el diseño principal mediante la vinculación de vistas.
        Establece el layout raíz como el contenido visual de la ventana, preparando
        el entorno para que el componente de navegación tome el control de la interfaz.
    */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Cargamos el layout raiz de nuestro proyecto
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    /* EXPLICACIÓN DEL METODO <onSupportNavigateUp()> : despliega para leer...
        Gestiona la acción de retroceso en la jerarquía de pantallas del gráfico de navegación.
        Busca el controlador de navegación raíz para procesar la subida de nivel o delega
        la acción a la superclase si el sistema de navegación no puede gestionarla.
    */
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_root)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}