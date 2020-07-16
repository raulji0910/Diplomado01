package com.diplomado.coronaalert

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*


//Clase menu principal
//Realizado por: Diego Castañeda
//               Mario Barrera
//               Raul Jimenez
//               Yeferson Daza
//Año: 2020
class MainActivity : AppCompatActivity() {

    //---------Se declaran la variables globales que se iniciaran posteriormente-----------------------------------
    //---------Variables de autenticación
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser

    //---------Variables de Base de datos
    private lateinit var database: FirebaseDatabase

    //---------Variables del layout
    private lateinit var txtNombre: TextView

    //---------Creacion de la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val men : String = intent.getStringExtra("mensaje")
        if(men != null){
            Toast.makeText(this,men, Toast.LENGTH_LONG).show()
        }
        //-----Creando instancia de la base de datos
        database= FirebaseDatabase.getInstance()
        auth=FirebaseAuth.getInstance()

        //-----Llenar variables con datos del layout
        txtNombre=findViewById(R.id.textViewNombreMA)

        //-----Obtener usuario logueado
        user = auth.currentUser!!
        val id:String= user.uid


        //-----Realizar consulta para traer nombre de usuario autenticado
        val query: Query = database.reference.child("User").orderByKey().equalTo(id)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (usuario in dataSnapshot.children) {
                        val primerNombre: String = usuario.child("Name").value.toString()
                        val primerApellido: String = usuario.child("lastName").value.toString()

                        txtNombre.text = TextUtils.concat("Bienvenido: ",primerNombre.replaceAfter(' ', "") ," ",primerApellido.replaceAfter(' ', ""))

                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    //------Metodo que carga layout Informe Geolocalización
    fun onClickInforme(view: View){
        val miIntent = Intent(this, InformeGeolocalizacionActivity::class.java)
        startActivity(miIntent)
    }

    //------Metodo que carga layout Registro Diario Covid 19
    fun onClickRegistro_Diario(view: View){
        val miIntent = Intent(this, RegistroDiarioCovid19Activity::class.java)
        startActivity(miIntent)
    }

    //------Metodo que carga layout Noticias
    fun onClickRNoticias(view: View){
        val miIntent = Intent(this, MainActivity::class.java)
        startActivity(miIntent)
    }

    //------Metodo que cierra sesión
    fun onClickCerrar(view: View){

        imageViewCerrar.setOnClickListener{
            auth.signOut()
            val miIntent = Intent(this, InicioSesionActivity::class.java)
            miIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            miIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(miIntent)
            finish()
        }
    }


}



