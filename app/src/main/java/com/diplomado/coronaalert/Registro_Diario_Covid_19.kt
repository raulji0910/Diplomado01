package com.diplomado.coronaalert

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_registro__diario__covid_19.*
import java.util.*

class Registro_Diario_Covid_19 : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var txtNombre: TextView
    private lateinit var txtFecha: EditText
    private lateinit var dbReference: DatabaseReference
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro__diario__covid_19)

        database= FirebaseDatabase.getInstance()
        txtNombre=findViewById(R.id.textNombre)
        txtFecha=findViewById(R.id.etPlannedDate)
        auth= FirebaseAuth.getInstance()
        user = auth.currentUser!!
        val id:String= user.uid
        text_seleccion_texto.visibility= View.INVISIBLE
        etPlannedDate.visibility=View.INVISIBLE

        SW1.setOnCheckedChangeListener{compoundButton, onSwitch ->
            if (onSwitch){
                text_seleccion_texto.visibility= View.VISIBLE
                etPlannedDate.visibility=View.VISIBLE}
            else{
                text_seleccion_texto.visibility= View.INVISIBLE
                etPlannedDate.visibility=View.INVISIBLE}
        }

        val query: Query = database.reference.child("User").orderByKey().equalTo(id)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (usuario in dataSnapshot.children) {
                        txtNombre.text = TextUtils.concat("Bienvenido: ",usuario.child("Name").value.toString()," ",usuario.child("lastName").value.toString())

                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

        //Calendar
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        etPlannedDate.setOnClickListener{
            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener{ view,mYear,mMonth,mDay ->
        etPlannedDate.setText(""+mDay+"/"+mMonth+"/"+mYear)}, year,month,day)
            dpd.show()
        }}




    }







