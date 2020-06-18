package com.diplomado.coronaalert

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


    }

    fun onClick(view: View){
        val miIntent = Intent(this, InformeGeolocalizacionActivity::class.java);
        startActivity(miIntent);

    }

    fun onClickRegistro_Diario(view: View){
        val miIntent = Intent(this, Registro_Diario_Covid_19::class.java);
        startActivity(miIntent);
    }


}



