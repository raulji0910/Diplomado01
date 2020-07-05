package com.diplomado.coronaalert


import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import com.diplomado.coronaalert.`interface`.IFirebaseLoadDone
import com.diplomado.coronaalert.`interface`.IFirebaseLoadDoneGenero
import com.diplomado.coronaalert.`interface`.IFirebaseLoadDoneTipoSangre
import com.diplomado.coronaalert.model.Genero
import com.diplomado.coronaalert.model.TipoIdentificacion
import com.diplomado.coronaalert.model.TipoSangre
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_registro__diario__covid_19.*
import kotlinx.android.synthetic.main.activity_registro_usuario.*
import java.util.*
import kotlin.collections.ArrayList

//Clase registro usuario
//Realizado por: Diego Castañeda
//               Mario Barrera
//               Raul Jimenez
//               Yeferson Daza
//Año: 2020
class RegistroUsuarioActivity : AppCompatActivity(), IFirebaseLoadDone, IFirebaseLoadDoneGenero,
    IFirebaseLoadDoneTipoSangre {

    //---------Se declaran la variables globales que se iniciaran posteriormente-----------------------------------
    //---------Variables de autenticación
    private lateinit var database:FirebaseDatabase
    private lateinit var auth:FirebaseAuth
    private lateinit var dbReference: DatabaseReference
    private lateinit var dbReferenceTipoIden: DatabaseReference
    private lateinit var dbReferenceGenero: DatabaseReference
    private lateinit var dbReferenceTipoSangre: DatabaseReference

    //---------Variables del layout
    private lateinit var txtName:EditText
    private lateinit var txtnumeroIden:EditText
    private lateinit var txtLastName:EditText
    private lateinit var txtEmail:EditText
    private lateinit var txtPassword:EditText
    private lateinit var txtNumPer:EditText
    private lateinit var txtConfirmeContra:EditText
    private lateinit var progressBar: ProgressBar

    //---------Variables Spinner
    private lateinit var mSpinner: Spinner
    private lateinit var mSpinner2: Spinner
    private lateinit var mSpinner3: Spinner

    //---------Variables Interfaces
    private lateinit var iFirebaseLoadDone: IFirebaseLoadDone
    private lateinit var iFirebaseLoadDoneGenero: IFirebaseLoadDoneGenero
    private lateinit var iFirebaseLoadDoneTipoSangre: IFirebaseLoadDoneTipoSangre

    //---------Creacion de la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_usuario)

        //-----Creando instancia de la base de datos
        database= FirebaseDatabase.getInstance()
        auth=FirebaseAuth.getInstance()
        dbReferenceTipoIden= database.reference.child("TipoIdentificacion")
        dbReference=database.reference.child("User")

        //-----Creando instancia interfaz
        iFirebaseLoadDone = this

        //-----Llenar variables con datos del layout
        txtnumeroIden=findViewById(R.id.editNumeroIdentificacion)
        txtName=findViewById(R.id.editNombres)
        txtLastName=findViewById(R.id.editApellidos)
        txtEmail=findViewById(R.id.editCorreo)
        txtPassword=findViewById(R.id.editContrasena)
        txtNumPer=findViewById(R.id.editPersonasVive)
        txtConfirmeContra=findViewById(R.id.editConfirmeContrasena)
        progressBar= findViewById(R.id.progressBar)
        mSpinner=findViewById(R.id.editTipoIdentificacion)
        mSpinner2=findViewById(R.id.editGenero)
        mSpinner3=findViewById(R.id.editTipoSangre)

        //-----Mostrar DatePicker para capturar fecha covid
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        editFechaNacimiento.setOnClickListener{

            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener{ _, mYear, mMonth, mDay ->
                val formatoFecha: String =  TextUtils.concat("",mYear.toString(),"-",(mMonth + 1).toString(),
                    "-$mDay"
                ).toString()
                editFechaNacimiento.setText(formatoFecha)}, year,month,day)

            dpd.datePicker.maxDate =  System.currentTimeMillis() + 1000
            c.add(Calendar.YEAR,-100)
            dpd.datePicker.minDate = c.timeInMillis
            dpd.show()
        }

        //-----Spinner Tipo Identificación
        dbReferenceTipoIden.addValueEventListener(object : ValueEventListener{
            var tipoIdenList:MutableList<TipoIdentificacion> = ArrayList<TipoIdentificacion>()
            override fun onCancelled(p0: DatabaseError) {
                iFirebaseLoadDone.onFirebaseLoadFailed(p0.message)
            }
            override fun onDataChange(p0: DataSnapshot) {
                for(tipoIdenSnapShot in p0.children)
                    tipoIdenList.add(tipoIdenSnapShot.getValue<TipoIdentificacion>(TipoIdentificacion::class.java)!!)
                iFirebaseLoadDone.onFirebaseLoadSucess(tipoIdenList)
            }
        })

        //-----Spinner Genero
        dbReferenceGenero= database.reference.child("Genero")
        iFirebaseLoadDoneGenero = this
        dbReferenceGenero.addValueEventListener(object: ValueEventListener{
            var tipogeneroList:MutableList<Genero> = ArrayList<Genero>()
            override fun onCancelled(p0: DatabaseError) {
                iFirebaseLoadDoneGenero.onFirebaseLoadFailedGenero(p0.message)
            }
            override fun onDataChange(p0: DataSnapshot) {
                for(GeneroSnapShot in p0.children)
                    tipogeneroList.add(GeneroSnapShot.getValue<Genero>(Genero::class.java)!!)
                iFirebaseLoadDoneGenero.onFirebaseLoadSucessGenero(tipogeneroList)
            }
        })

        //-----Spinner Tipo Sangre
        dbReferenceTipoSangre= database.reference.child("TipoSangre")
        iFirebaseLoadDoneTipoSangre = this
        dbReferenceTipoSangre.addValueEventListener(object: ValueEventListener{
            var tipoSangreList:MutableList<TipoSangre> = ArrayList<TipoSangre>()
            override fun onCancelled(p0: DatabaseError) {
                iFirebaseLoadDoneTipoSangre.onFirebaseLoadFailedTipoSangre(p0.message)
            }
            override fun onDataChange(p0: DataSnapshot) {
                for(TipoSangreSnapShot in p0.children)
                    tipoSangreList.add(TipoSangreSnapShot.getValue<TipoSangre>(TipoSangre::class.java)!!)
                iFirebaseLoadDoneTipoSangre.onFirebaseLoadSucessTipoSangre(tipoSangreList)
            }
        })
    }

    //------Metodo registrar
    fun register(view: View) {
        createNewAccount()
    }

    //------Metodo para insertar datos en la base de datos
    private fun createNewAccount(){
        val name:String=editNombres.text.toString()
        val lastName:String=editApellidos.text.toString()
        val email:String=editCorreo.text.toString()
        val password:String=editContrasena.text.toString()
        val tipoIden:String=editTipoIdentificacion.selectedItem.toString()
        val numIden:String=editNumeroIdentificacion.text.toString()
        val fechaNam:String=editFechaNacimiento.text.toString()
        val generoTipo:String=editGenero.selectedItem.toString()
        val numPer:String=editPersonasVive.text.toString()
        val tipoSang:String=editTipoSangre.selectedItem.toString()
        val confContra:String=editConfirmeContrasena.text.toString()
        val condiciones:Boolean=radioButtonCondiciones.isChecked()


        //------Validar que los campos no esten vacios
        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(lastName) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(tipoIden) && !TextUtils.isEmpty(numIden) && !TextUtils.isEmpty(generoTipo) && !TextUtils.isEmpty(numPer) && !TextUtils.isEmpty(tipoSang) && !TextUtils.isEmpty(confContra)){
            if(password==confContra){
                if(condiciones==true) {

                    progressBar.visibility = View.VISIBLE

                    //------Crear usuario con mail y pass
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this) { task ->
                            if (task.isComplete) {
                                val user: FirebaseUser? = auth.currentUser

                                //------Enviar verificación mail
                                verifyEmail(user)

                                //------Grabar datos
                                val userBD = dbReference.child(user?.uid.toString())
                                userBD?.child("Name")?.setValue(name)
                                userBD?.child("lastName")?.setValue(lastName)
                                userBD?.child("TipoIdentificacion")?.setValue(tipoIden)
                                userBD?.child("NumeroIden")?.setValue(numIden)
                                userBD?.child("FechaNacimiento")?.setValue(fechaNam)
                                userBD?.child("GeneroTipo")?.setValue(generoTipo)
                                userBD?.child("NumeroPersonas")?.setValue(numPer)
                                userBD?.child("TipoSangre")?.setValue(tipoSang)

                                action()
                            }
                        }
                }else{
                    Toast.makeText(this,"Por favor acepte lor términos y condiciones de la aplicacición",Toast.LENGTH_LONG).show()
                }
                }else{
                    Toast.makeText(this,"La contraseña ingresada es diferente a la contraseña confirmada",Toast.LENGTH_LONG).show()
                }
            }
        }

        //------Metodo para volver al inicio
        private fun action(){
            startActivity(Intent(this,InicioSesionActivity::class.java))
        }

        //------Metodo para verificar mail
        private fun verifyEmail(user:FirebaseUser?){
            user?.sendEmailVerification()
                ?.addOnCompleteListener(this){
                        task ->
                    if(task.isComplete){
                        Toast.makeText(this,"Registro realizado satisfactoriamente",Toast.LENGTH_LONG).show()
                    }else{
                        Toast.makeText(this,"Error al enviar el email",Toast.LENGTH_LONG).show()
                    }
                }
        }

        //-------Spinner Tipo identificación
        override fun onFirebaseLoadSucess(tipoIdentificacionList: List<TipoIdentificacion>) {
            val tipo_iden_descripcion = getTipoIdentificacionDescripcionList(tipoIdentificacionList)
            val adapter = ArrayAdapter<String>(this,R.layout.my_text_view,tipo_iden_descripcion)
            mSpinner.adapter = adapter
        }
        private fun getTipoIdentificacionDescripcionList(tipoIdentificacionList: List<TipoIdentificacion>): List<String> {
            val result = ArrayList<String>()
            for(tipoIdentificacion in tipoIdentificacionList )
                result.add(tipoIdentificacion.tipoDescripcion!!)
            return result
        }
        override fun onFirebaseLoadFailed(message: String) {
        }

        //-------Spinner Genero
        override fun onFirebaseLoadSucessGenero(tipogeneroList: List<Genero>) {
            val tipo_genero_descripcion = getGeneroList(tipogeneroList)
            val adapterGenero = ArrayAdapter<String>(this ,R.layout.my_text_view,tipo_genero_descripcion)
            mSpinner2.adapter = adapterGenero
        }
        private fun getGeneroList(tipogeneroList: List<Genero>): List<String> {
            val result2 = ArrayList<String>()
            for (Genero in tipogeneroList)
                result2.add(Genero.generoDescripcion!!)
            return result2
        }
        override fun onFirebaseLoadFailedGenero(message: String) {

        }

        //-------Spinner Tipo Sangre
        override fun onFirebaseLoadSucessTipoSangre(tipoSangreList: List<TipoSangre>) {
            val tipo_sangre_descripcion = getTipoSangreList(tipoSangreList)
            val adapterTipoSangre = ArrayAdapter<String>(this ,R.layout.my_text_view,tipo_sangre_descripcion)
            mSpinner3.adapter = adapterTipoSangre
        }
        private fun getTipoSangreList(tipoSangreList: List<TipoSangre>): List<String> {
            val result3 = ArrayList<String>()
            for (TipoSangre in tipoSangreList)
                result3.add(TipoSangre.tipoSangreDescripcion!!)
            return result3
        }
        override fun onFirebaseLoadFailedTipoSangre(message: String) {

        }

    }