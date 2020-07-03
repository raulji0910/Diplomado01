package com.diplomado.coronaalert

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_inicio_sesion.*

//Clase Inicio sesión
//Realizado por: Diego Castañeda
//               Mario Barrera
//               Raul Jimenez
//               Yeferson Daza
//Año: 2020
class Inicio_sesion : AppCompatActivity() {

    //---------Se declaran la variables globales que se iniciaran posteriormente-----------------------------------
    //---------Variables de autenticación
    private lateinit var auth: FirebaseAuth

    //---------Variables del layout
    private lateinit var txtUser: EditText
    private lateinit var txtPassword: EditText
    private lateinit var progressBar: ProgressBar

    //---------Creacion de la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio_sesion)

        //-----Llenar variables con datos del layout
        txtUser=findViewById(R.id.editTextEmail)
        txtPassword=findViewById(R.id.editTextPassword)
        progressBar= findViewById(R.id.progressBarInicioSesion)

        //-----Creando instancia de la base de datos
        auth=FirebaseAuth.getInstance()

    }

    //--Metodo recuperar contraseña
    fun forgotPassword(view: View){
        startActivity(Intent(this,RecuperarContrasenaActivity::class.java))
    }

    //--Metodo registrar
    fun register(view:View){
        startActivity(Intent(this,RegistroUsuario::class.java))
    }

    //--Metodo Ingresar
    fun login(view:View){
        loginUser()
    }

    //--Metodo para autenticar usuario
    private fun loginUser(){

        //--Se capturan variables del layout
        val user:String= editTextEmail.text.toString().trim()
        val password:String= editTextPassword.text.toString().trim()

        //--Se valida que los campos no esten vacios
        if(!TextUtils.isEmpty(user) && !TextUtils.isEmpty(password)){

            //--Se carga barra de progreso
            progressBar.visibility=View.VISIBLE

            //--Se realiza autenticación contra firebase
            auth.signInWithEmailAndPassword(user.trim(),password)
                .addOnCompleteListener(this){
                        task ->

                    if(task.isSuccessful){
                        action()
                    }else{
                        progressBar.visibility=View.INVISIBLE
                        Toast.makeText(this,"Error en el usuario y/o contraseña",Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    //--Metodo para ingresar al menú principal si la autenticación es correcta
    private fun action(){
        startActivity(Intent(this,MainActivity::class.java))
    }
}