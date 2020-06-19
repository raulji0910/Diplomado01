package com.diplomado.coronaalert

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import com.diplomado.coronaalert.`interface`.IFirebaseLoadDone
import com.diplomado.coronaalert.`interface`.IFirebaseLoadDoneGenero
import com.diplomado.coronaalert.model.Genero
import com.diplomado.coronaalert.model.TipoIdentificacion
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_registro_usuario.*
import java.lang.ref.PhantomReference

class RegistroUsuario : AppCompatActivity(), IFirebaseLoadDone, IFirebaseLoadDoneGenero {

    private lateinit var txtName:EditText
    private lateinit var txtLastName:EditText
    private lateinit var txtEmail:EditText
    private lateinit var txtPassword:EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var dbReference: DatabaseReference
    private lateinit var dbReferenceTipoIden: DatabaseReference
    private lateinit var dbReferenceGenero: DatabaseReference
    private lateinit var database:FirebaseDatabase
    private lateinit var auth:FirebaseAuth

    //Spinner - Inicio
    private lateinit var mSpinner: Spinner
    private lateinit var mSpinner2: Spinner
    private lateinit var iFirebaseLoadDone: IFirebaseLoadDone
    private lateinit var iFirebaseLoadDoneGenero: IFirebaseLoadDoneGenero
    //Spinner - Fin


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_usuario)
        txtName=findViewById(R.id.editNombres)
        txtLastName=findViewById(R.id.editApellidos)
        txtEmail=findViewById(R.id.editCorreo)
        txtPassword=findViewById(R.id.editContrasena)
        progressBar= findViewById(R.id.progressBar)
        database= FirebaseDatabase.getInstance()
        auth=FirebaseAuth.getInstance()
        dbReference=database.reference.child("User")

        //Spinner - Inicio
        dbReferenceTipoIden= database.reference.child("TipoIdentificacion")
        iFirebaseLoadDone = this
        mSpinner=findViewById(R.id.editTipoIdentificacion)
        mSpinner2=findViewById(R.id.editGenero)





        //Spinner - Inicio
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
        //Spinner - Fin
        //Spinner - Inicio
        dbReferenceGenero= database.reference.child("Genero")
        iFirebaseLoadDoneGenero = this

        dbReferenceGenero.addValueEventListener(object: ValueEventListener{
            var tipogeneroList:MutableList<Genero> = ArrayList<Genero>()
            override fun onCancelled(p0: DatabaseError) {
                iFirebaseLoadDoneGenero.onFirebaseLoadFailed(p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                for(GeneroSnapShot in p0.children)
                    tipogeneroList.add(GeneroSnapShot.getValue<Genero>(Genero::class.java)!!)
                iFirebaseLoadDoneGenero.onFirebaseLoadSucess(tipogeneroList)
            }

        })

    }


    fun register(view: View) {
        createNewAccount()
    }
    private fun createNewAccount(){
        val name:String=editNombres.text.toString()
        val lastName:String=editApellidos.text.toString()
        val email:String=editCorreo.text.toString()
        val password:String=editContrasena.text.toString()

        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(lastName) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
            progressBar.visibility=View.VISIBLE
            auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this){
                    task ->
                    if(task.isComplete){
                        val user:FirebaseUser?=auth.currentUser
                        verifyEmail(user)

                        //val userBD= user?.uid?.let { dbReference.child(it) }

                        val userBD= dbReference.child(user?.uid.toString())
                     //val userBD= user?.uid?.let { dbReference.child(it) }

                        userBD?.child("Name")?.setValue(name)
                        userBD?.child("lastName")?.setValue(lastName)
                        action()


                    }
                }

        }
    }
    private fun action(){
        startActivity(Intent(this,Inicio_sesion::class.java))

    }
    private fun verifyEmail(user:FirebaseUser?){
        user?.sendEmailVerification()
            ?.addOnCompleteListener(this){
                task ->
                if(task.isComplete){
                    Toast.makeText(this,"Email enviado",Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(this,"Error al enviar el email",Toast.LENGTH_LONG).show()
                }
            }
    }

   //Spinner - Inicio
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

    override fun onFirebaseLoadSucess(tipogeneroList: List<Genero>) {

    }

    override fun onFirebaseLoadFailed(message: String) {

    }
    //Spinner - Fin
}


