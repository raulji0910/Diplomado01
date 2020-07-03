package com.diplomado.coronaalert

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class RecuperarContrasenaActivity : AppCompatActivity() {

    private lateinit var txtEmail: EditText
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recuperar_contrasena)

        txtEmail=findViewById(R.id.editEmailRecuperar)
        auth=FirebaseAuth.getInstance()
    }

    fun enviar(view:View){
        val email= txtEmail.text.toString()
        if (!TextUtils.isEmpty(email)){
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(this){
                    task ->

                        if (task.isSuccessful){
                            startActivity(Intent(this,InicioSesionActivity::class.java))
                        }else{

                            Toast.makeText(this,"Error al enviar correo", Toast.LENGTH_LONG).show()
                        }
                }
        }
    }
    fun cancelar(view:View){

        startActivity(Intent(this,InicioSesionActivity::class.java))

    }
}