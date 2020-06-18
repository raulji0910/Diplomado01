package com.diplomado.coronaalert

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_inicio_sesion.*

class Inicio_sesion : AppCompatActivity() {
    private lateinit var txtUser: EditText
    private lateinit var txtPassword: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio_sesion)
        txtUser=findViewById(R.id.editEmail)
        txtPassword=findViewById(R.id.editPassword)

        progressBar= findViewById(R.id.progressBar2)
        auth=FirebaseAuth.getInstance()

        //getSupportActionBar()?.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM)
        //getSupportActionBar()?.setCustomView(R.layout.abs_layout)

    }

    fun forgotPassword(view: View){

    }
    fun register(view:View){
        startActivity(Intent(this,RegistroUsuario::class.java))
    }
    fun login(view:View){
        loginUser()

    }
    private fun loginUser(){

        val user:String= editEmail.text.toString().trim()
        val password:String= editPassword.text.toString().trim()
        if(!TextUtils.isEmpty(user) && !TextUtils.isEmpty(password)){
            progressBar.visibility=View.VISIBLE
            Log.e("usuario",user.toString())
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
    private fun action(){
        startActivity(Intent(this,MainActivity::class.java))
    }
}