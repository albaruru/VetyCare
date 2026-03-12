package com.example.vetycare

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import com.example.vetycare.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Cargamos el layout raiz de nuestro proyecto
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializa el host raiz de nuestro proyecto
        val navController = findNavController(R.id.nav_host_root)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_root)
        return navController.navigateUp(appBarConfiguration)|| super.onSupportNavigateUp()
    }
}