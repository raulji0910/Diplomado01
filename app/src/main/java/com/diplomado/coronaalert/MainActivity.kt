package com.diplomado.coronaalert

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var txtNombre: TextView
    private lateinit var txtApellido: TextView
    private lateinit var dbReference: DatabaseReference
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        database= FirebaseDatabase.getInstance()
        txtNombre=findViewById(R.id.textNombre)

        auth=FirebaseAuth.getInstance()
        user = auth.currentUser!!
        val id:String= user.uid

        val query: Query = database.reference.child("User").orderByKey().equalTo(id)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (usuario in dataSnapshot.children) {
                        txtNombre.text = TextUtils.concat("Bienvenido: ",usuario.child("Name").value.toString()," ",usuario.child("LastName").value.toString())

                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

    }

    fun onClickInforme(view: View){
        val miIntent = Intent(this, InformeGeolocalizacionActivity::class.java);
        startActivity(miIntent);

    }

    fun onClickRegistro_Diario(view: View){
        val miIntent = Intent(this, Registro_Diario_Covid_19::class.java);
        startActivity(miIntent);
    }

    fun onClickRNoticias(view: View){
        val miIntent = Intent(this, Registro_Diario_Covid_19::class.java);
        startActivity(miIntent);
    }

    fun onClickCerrar(view: View){
        FirebaseAuth.getInstance().signOut();
        super.onDestroy();
        val miIntent = Intent(this, Inicio_sesion::class.java);
        startActivity(miIntent);
    }


}



