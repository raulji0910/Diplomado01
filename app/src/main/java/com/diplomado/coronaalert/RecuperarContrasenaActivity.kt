package com.diplomado.coronaalert

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

//Clase recuperar contraseña
//Realizado por: Diego Castañeda
//               Mario Barrera
//               Raul Jimenez
//               Yeferson Daza
//Año: 2020
class RecuperarContrasenaActivity : AppCompatActivity() {

    //---------Se declaran la variables globales que se iniciaran posteriormente-----------------------------------
    //---------Variables de autenticación
    private lateinit var auth: FirebaseAuth

    //---------Variables del layout
    private lateinit var txtEmail: EditText

    //---------Creacion de la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recuperar_contrasena)

        //-----Creando instancia de la base de datos
        auth=FirebaseAuth.getInstance()

        //-----Llenar variables con datos del layout
        txtEmail=findViewById(R.id.editEmailRecuperar)
    }

    //------Metodo que recupera contraseña
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

    //------Metodo que cancela operación
    fun cancelar(view:View){
        startActivity(Intent(this,InicioSesionActivity::class.java))
    }
}