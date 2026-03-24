package com.example.vetycare.ui.fragment.mascota

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.vetycare.databinding.FragmentMascotaPerfilBinding

class MascotaPerfilFragment : Fragment() {
    private lateinit var binding : FragmentMascotaPerfilBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMascotaPerfilBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        // TODO: BINDING PARA LOS BOTONES VOLVER Y ELIMINAR
        // FIXME: binding.etDatosMascota.isEnabled = false // Esto lo deshabilita por completo
        /* FIXME: <EditText -> HAY QUE PONERLO EN CADA UNO DE LOS ET
            android:id="@+id/et_datos_mascota"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:focusable="false"
            android:clickable="true"
            android:cursorVisible="false" /> */
    }
}