package com.diplomado.coronaalert

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_registro_usuario_firebase.*

class RegistroUsuarioFirebase : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_usuario_firebase)

        // Setup
        setup()

    }
    private fun setup(){

        title = "Auntenticación"
        registrarbutton3.setOnClickListener {
            if (emaileditText.text.isNotEmpty() && passwordeditText.text.isNotEmpty()){
                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(emaileditText.text.toString(),
                passwordeditText.text.toString()).addOnCompleteListener {
                        if (it.isSuccessful){

                        }else{
                         // showAlert()
                        }

                    }

            }
        }

    }
    //private fun showAlert() {
    //    val builder = AlertDialog.Builder(context:this)
      //  builder.setTitle("Error")
       // builder.setMessage("se ha producido un error autenticando al usuario")
       // builder.setPositiveButton(text:"Aceptar",listener:null)
        //val dialog: AlertDialog = builder.create()
        //dialog.show()

    //}
   //private fun ShowHomeRegistro(email: String , proveedor: ProviderType){
     //   val homeIntent = Intent

   // }

}
