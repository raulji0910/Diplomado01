package com.diplomado.coronaalert

import android.Manifest
import android.app.DatePickerDialog
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
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
    private lateinit var dbReferenceUsuario: DatabaseReference
    private lateinit var database: FirebaseDatabase
    private var latiLong: LatLng? = null
    private var lati: Double? = null
    private var longi: Double? = null

    //---------Variables de localizacion
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location

    //---------Variable de clase
    companion object{
        //-----Variable para validar si se tiene permiso para obtener la localizacion
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val REQUEST_CHECK_SETTINGS = 0x1
        private var REGISTRO_DIARIO = 0
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



        obtenerLocalizacion()
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

        //-----Realizar consulta para validar registro diario
        val query2: Query = database.reference.child("RegistroDiario").orderByChild("userId").equalTo(id)
        query2.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (registro in dataSnapshot.children) {
                        val fechaRegistro: String = registro.child("fechaRegistro").value.toString()
                        val fechaHoy: String = LocalDateTime.now().toString()
                        val fechaRegistroTratada: String = fechaRegistro.replaceAfter('T', "")
                        val fechaHoyTratada: String = fechaHoy.replaceAfter('T', "")

                            if (fechaRegistroTratada == fechaHoyTratada){

                                REGISTRO_DIARIO = 1
                            }

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
            dpd.datePicker.maxDate = System.currentTimeMillis() + 1000
            c.add(Calendar.MONTH,-6)
            dpd.datePicker.minDate = c.timeInMillis
            dpd.show()
        }

    }

    fun obtenerLocalizacion(){//: LatLng? {

        //var latilong : LatLng = LatLng(0.1200, 0.1200)

        val locationRequest: LocationRequest
        val locationCallback: LocationCallback

        locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY;
        locationRequest.interval = 20 * 1000;

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                if (locationResult == null) {
                    return
                }
                for (location in locationResult.locations) {
                    if (location != null) {
                        var wayLatitude = location.latitude
                        var wayLongitude = location.longitude


                    }
                }
            }
        }
        val builder = LocationSettingsRequest.Builder()

        // ...


        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener { locationSettingsResponse ->
            // All location settings are satisfied. The client can initialize
            // location requests here.
            // ...
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException){
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    exception.startResolutionForResult(this,
                        REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }



        if(ActivityCompat.checkSelfPermission(this , Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.locationAvailability.addOnSuccessListener(this) { locationAvailability ->

                    if (locationAvailability.isLocationAvailable) {
                        if (ActivityCompat.checkSelfPermission(this,
                                Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                            fusedLocationProviderClient.lastLocation.addOnCompleteListener(this){ location ->
                                 lastLocation = location.result!!
                                lati = lastLocation.latitude
                                longi = lastLocation.longitude

                            }

                        }else{

                            if(ActivityCompat.checkSelfPermission(this , Manifest.permission.ACCESS_FINE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED){
                                ActivityCompat.requestPermissions(this,
                                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                    LOCATION_PERMISSION_REQUEST_CODE
                                )
                                return@addOnSuccessListener
                            }
                        }

                    }else{
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback,null)
                    }

                }


        }else{
            if(ActivityCompat.checkSelfPermission(this , Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE
                )
                return
            }
        }

    }



    fun validarCampos():Boolean{
        obtenerLocalizacion()
        return lati !=null  || longi != null
    }

    fun ejecutarRegistro(view: View){
        if (REGISTRO_DIARIO == 0){
        if (validarCampos()){
            cargarDatos()
        }else{
            val miIntent = Intent(this, MainActivity::class.java)
            miIntent.putExtra("mensaje","La ubicación no se logró obtener por favor intentar nuevamente")
            startActivity(miIntent)

        }
        }else{
           val miIntent = Intent(this, MainActivity::class.java)
           miIntent.putExtra("mensaje","Ya registraste hoy, vuelve a intentar mañana.")
            startActivity(miIntent)
        }
    }
    //------Metodo que carga los datos del layout a la base de datos
    fun cargarDatos() {


        var lat1: Double = 0.1200
        var lng1: Double = 0.1200
        //--Validar permiso de Localizacion



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

        if (switchPreguntaDos.isChecked){

            //--Ubicar en tabla User
            dbReferenceUsuario = database.reference.child("User")
            val usuarioEstado= dbReferenceUsuario.child(idUser)
            usuarioEstado.child("estado").setValue(1)
        }else{
            //--Ubicar en tabla User
            dbReferenceUsuario = database.reference.child("User")
            val usuarioEstado= dbReferenceUsuario.child(idUser)
            usuarioEstado.child("estado").setValue(0)
        }

        //--Se guarda fecha actual
        registroId2.child("fechaRegistro").setValue(LocalDateTime.now().toString())

        //--Se guarda fecha covid
        registroId2.child("fechaCovid").setValue(editTextFechaCovid.text.toString())


            //--Obtener localizacion latitud y longitud

        obtenerLocalizacion()

                    registroId.child("longitud").setValue(longi)
                    registroId.child("latitud").setValue(lati)

                    registroId2.child("longitud").setValue(longi)
                    registroId2.child("latitud").setValue(lati)

       //--Volver al menu despues de realizar registro diario
        val miIntent = Intent(this, MainActivity::class.java)
        miIntent.putExtra("mensaje","Registro satisfactorio")
        startActivity(miIntent)

    }

}




