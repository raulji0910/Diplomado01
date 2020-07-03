package com.diplomado.coronaalert

import android.app.DatePickerDialog
import android.content.Intent
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_registro__diario__covid_19.*
import java.time.LocalDateTime
import java.util.*

//Clase registro diario covid 19
//Realizado por: Diego Castañeda
//               Mario Barrera
//               Raul Jimenez
//               Yeferson Daza
//Año: 2020
class RegistroDiarioCovid19Activity : AppCompatActivity() {

    //---------Se declaran la variables globales que se iniciaran posteriormente-----------------------------------
    //---------Variables de autenticación
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser

    //---------Variables del layout
    private lateinit var txtNombre: TextView
    private lateinit var txtFecha: EditText

    //---------Variables de Base de datos
    private lateinit var dbReference: DatabaseReference
    private lateinit var database: FirebaseDatabase

    //---------Variables de localizacion
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location

    //---------Variable de clase
    companion object{
        //-----Variable para validar si se tiene permiso para obtener la localizacion
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    //---------Creacion de la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro__diario__covid_19)

        //-----Creando instancia de la base de datos
        database= FirebaseDatabase.getInstance()
        auth= FirebaseAuth.getInstance()

        //-----Llenar variables con datos del layout
        txtNombre=findViewById(R.id.textViewNombre)
        txtFecha=findViewById(R.id.editTextFechaCovid)

        //-----Obtener usuario logueado
        user = auth.currentUser!!

        //-----Obtener Id de Usuario
        val id:String= user.uid

        //-----Ocultar texto y campo de la fecha covid
        textViewSeleccionFecha.visibility= View.INVISIBLE
        editTextFechaCovid.visibility=View.INVISIBLE

        //-----Localización - punto de entrada principal
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        //-----Mostrar campos fecha cuando es positivo COVID
        switchPreguntaDos.setOnCheckedChangeListener{ _, onSwitch ->
            if (onSwitch){
                textViewSeleccionFecha.visibility= View.VISIBLE
                editTextFechaCovid.visibility=View.VISIBLE}
            else{
                textViewSeleccionFecha.visibility= View.INVISIBLE
                editTextFechaCovid.visibility=View.INVISIBLE}
        }

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

        //-----Mostrar DatePicker para capturar fecha covid
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        editTextFechaCovid.setOnClickListener{

            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener{ _, mYear, mMonth, mDay ->
                val formatoFecha: String =  TextUtils.concat("",mYear.toString(),"-",(mMonth + 1).toString(),
                    "-$mDay"
                ).toString()
                editTextFechaCovid.setText(formatoFecha)}, year,month,day)
            dpd.show()
        }

    }


    //------Metodo que carga los datos del layout a la base de datos
    fun cargarDatos(view: View) {

        //--Ubicar en tabla RegistroDiario
        dbReference=database.reference.child("RegistroDiario")

        //--Se obtiene id de usuario
        auth= FirebaseAuth.getInstance()
        user = auth.currentUser!!
        val idUser:String= user.uid

        //-----------------------Pregunta 1---------------------------------------------------------------

        //--Se obtiene id de registro
        val registroId= dbReference.child(UUID.randomUUID().toString())

        //--Se guarda id de usuario en la base de datos
        registroId.child("userId").setValue(idUser)

        //--Se guarda id de pregunta
        registroId.child("preguntaId").setValue("0")

        //--Se guarda valor de switch pregunta 1
        registroId.child("preguntaEstado").setValue(if (switchPreguntaUno.isChecked) "SI" else "NO")

        //--Se guarda fecha actual
        registroId.child("fechaRegistro").setValue(LocalDateTime.now().toString())

        //--Se guarda fecha covid
        registroId.child("fechaCovid").setValue(editTextFechaCovid.text.toString())


        //-----------------------Pregunta 2---------------------------------------------------------------

        //--Se obtiene id de registro
        val registroId2= dbReference.child(UUID.randomUUID().toString())

        //--Se guarda id de usuario en la base de datos
        registroId2.child("userId").setValue(idUser)

        //--Se guarda valor de switch pregunta 2
        registroId2.child("preguntaId").setValue("1")

        //--Se guarda valor de switch pregunta 2
        registroId2.child("preguntaEstado").setValue(if (switchPreguntaDos.isChecked) "SI" else "NO")

        //--Se guarda fecha actual
        registroId2.child("fechaRegistro").setValue(LocalDateTime.now().toString())

        //--Se guarda fecha covid
        registroId2.child("fechaCovid").setValue(editTextFechaCovid.text.toString())


        //--Validar permiso de Localizacion

        if(ActivityCompat.checkSelfPermission(this , android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        //--Obtener localizacion latitud y longitud
        fusedLocationProviderClient.lastLocation.addOnSuccessListener(this) { location ->

            if(location != null){
                lastLocation = location

                registroId.child("longitud").setValue(location.longitude.toString())
                registroId.child("latitud").setValue(location.latitude.toString())

                registroId2.child("longitud").setValue(location.longitude.toString())
                registroId2.child("latitud").setValue(location.latitude.toString())
            }

        }

       //--Volver al menu despues de realizar registro diario
        val miIntent = Intent(this, MainActivity::class.java)
        startActivity(miIntent)
    }

}




