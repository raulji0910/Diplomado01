package com.diplomado.coronaalert

import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.core.app.ActivityCompat

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_registro__diario__covid_19.*
import java.util.*

class Registro_Diario_Covid_19 : AppCompatActivity() {
    //Variables globales
    //Autenticación
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    //Variables del layout
    private lateinit var txtNombre: TextView
    private lateinit var txtFecha: EditText
    //Base de datos
    private lateinit var dbReference: DatabaseReference
    private lateinit var database: FirebaseDatabase
    //Localizacion
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location

    //Variable de clase
    companion object{
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro__diario__covid_19)

        //Creando instancia de la base de datos
        database= FirebaseDatabase.getInstance()
        auth= FirebaseAuth.getInstance()
        //LLenar variables con datos del layout
        txtNombre=findViewById(R.id.textNombre)
        txtFecha=findViewById(R.id.etPlannedDate)
        //Obtener usuario logueado
        user = auth.currentUser!!
        //Obtener Id de Usuario
        val id:String= user.uid
        //Esconder texto y campo de la fecha
        text_seleccion_texto.visibility= View.INVISIBLE
        etPlannedDate.visibility=View.INVISIBLE

        //Localización
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        //Mostrar campos fecha cuando es positivo COVID
        SW1.setOnCheckedChangeListener{compoundButton, onSwitch ->
            if (onSwitch){
                text_seleccion_texto.visibility= View.VISIBLE
                etPlannedDate.visibility=View.VISIBLE}
            else{
                text_seleccion_texto.visibility= View.INVISIBLE
                etPlannedDate.visibility=View.INVISIBLE}
        }

        //Realizar consulta para traer nombre de usuario autenticado
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

        //Mostrar DatePicker
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        etPlannedDate.setOnClickListener{
            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener{ view,mYear,mMonth,mDay ->
        etPlannedDate.setText(""+mDay+"/"+mMonth+"/"+mYear)}, year,month,day)
            dpd.show()
        }

    }



    fun cargarDatos(view: View){
        //obtenerUbicacion()
        dbReference=database.reference.child("RegistroDiario")
        //1. Tarea implementar contador para id de registro diario
        //Para pruebas se quema el 0
        val registroId= dbReference.child("0")

        //Id Usuario
        auth= FirebaseAuth.getInstance()
        user = auth.currentUser!!
        val idUser:String= user.uid

        registroId.child("userId").setValue(idUser)
        //2. Tarea implementar para la pregunta 2
        registroId.child("preguntaId").setValue("0")
        //3. Tarea traer el dato del Switch Pregunta 1
        registroId.child("preguntaEstado").setValue("prueba")
        //4. Obtener fecha de registro (traer fecha actual)
        registroId.child("fechaRegistro").setValue("prueba")
        //5. Obtener fecha que selecciona el usuario
        registroId.child("fechaCovid").setValue("prueba")


        //Localizacion

        if(ActivityCompat.checkSelfPermission(this , android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        fusedLocationProviderClient.lastLocation.addOnSuccessListener(this) { location ->

            if(location != null){
                lastLocation = location

                registroId.child("longitud").setValue(location.longitude.toString())
                registroId.child("latitud").setValue(location.latitude.toString())
            }




    }


    /*private fun obtenerUbicacion(){
        if(ActivityCompat.checkSelfPermission(this , android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        fusedLocationProviderClient.lastLocation.addOnSuccessListener(this) { location ->

            if(location != null){
                lastLocation = location
                latitude=location.latitude.toString()
                var longitud:String=location.longitude.toString()

            }
        }*/
    }

    }







