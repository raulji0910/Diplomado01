package com.diplomado.coronaalert

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
enum class ProviderType{
    BASIC
}

class HomeRegistro : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_registro)
    }
}
