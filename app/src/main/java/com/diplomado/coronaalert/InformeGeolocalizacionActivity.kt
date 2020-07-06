package com.diplomado.coronaalert


import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.database.*
import com.google.maps.android.heatmaps.Gradient
import com.google.maps.android.heatmaps.HeatmapTileProvider
import kotlinx.android.synthetic.main.activity_informe_geolocalizacion.*
import org.json.JSONArray
import org.json.JSONException
import java.util.*


//Clase informe geoposición
//Realizado por: Diego Castañeda
//               Mario Barrera
//               Raul Jimenez
//               Yeferson Daza
//Año: 2020
class InformeGeolocalizacionActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener{

    //---------Se declaran la variables globales que se iniciaran posteriormente-----------------------------------
    //---------Variables de localización
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    private lateinit var mMap: GoogleMap

    //---------Variables de Base de datos
    private lateinit var dbReference: DatabaseReference
    private lateinit var database: FirebaseDatabase

    //---------Variables de clase
    companion object{

        //-----Variable para validar si se tiene permiso para obtener la localizacion
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    //-------HeatMap
    //------Variables HeatMap
    //------El tamaño del desenfoque gaussiano aplicado al mapa de calor, expresado en píxeles. El valor predeterminado es 20. Debe estar entre 10 y 50
    private val ALT_HEATMAP_RADIUS = 10
    //------Es la opacidad de toda la capa de mapa de calor, y varía de 0 a 1
    private val ALT_HEATMAP_OPACITY = 0.4
    //------una gama de colores que el mapa de calor utiliza para generar su mapa de colores, que va de menor a mayor intensidad. Se crea un gradiente
    // utilizando dos matrices: una matriz entera que contiene los colores y una matriz flotante que indica el punto de partida para cada color, dado
    // como un porcentaje de la intensidad máxima, y ​​expresado como una fracción de 0 a 1. Debe especificar solo un color para un degradado de un
    // solo color, o un mínimo de dos colores para un degradado de varios colores. El mapa de colores se genera mediante la interpolación entre esos
    // colores.
    private val ALT_HEATMAP_GRADIENT_COLORS =
        intArrayOf(

            Color.argb(0, 255, 0, 255),
            Color.argb(255 / 3 * 2, 255, 0, 255),
            Color.rgb(125, 191, 0),
            Color.rgb(185, 71, 0),
            Color.rgb(255, 0, 0)
        )

    val ALT_HEATMAP_GRADIENT_START_POINTS =
        floatArrayOf(
            0.0f,
            0.10f, 0.20f, 0.60f, 1.0f

    )

    val ALT_HEATMAP_GRADIENT: Gradient = Gradient(
        ALT_HEATMAP_GRADIENT_COLORS,
        ALT_HEATMAP_GRADIENT_START_POINTS
    )

    private var mProvider: HeatmapTileProvider? = null
    private var mOverlay: TileOverlay? = null

    private var mDefaultGradient = true
    private var mDefaultRadius = true
    private var mDefaultOpacity = true

    //--------Variable para guardar longitudes y latitudes
    private var mLists: HashMap<String?, DataSet?>? =
        HashMap()
    private lateinit var ubicaciones:ArrayList<LatLng>

    //---------Se adicional un hilo
    val thread = Thread(Runnable {
        //code to do the HTTP request
        ubicaciones =  readItemsDataBase()
    })



    //---------Creacion de la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_informe_geolocalizacion)
        buttonGradiente.isClickable = false
        buttonOpacidad.isClickable = false
        buttonRadio.isClickable = false
        //-----Creando instancia de la base de datos
        database= FirebaseDatabase.getInstance()

        //-----Se inicia hilo
        thread.start()
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //-----Localización - punto de entrada principal
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    }





    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMarkerClickListener(this)
        mMap.uiSettings.isZoomControlsEnabled = true

        setUpMap()

    }

    private fun placeMarket(location: LatLng){
        val markerOptions = MarkerOptions().position(location).title("Mi ubicación.")
        mMap.addMarker(markerOptions)

    }

    private fun setUpMap(){
        if(ActivityCompat.checkSelfPermission(this , android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
        mMap.isMyLocationEnabled = true
        fusedLocationProviderClient.lastLocation.addOnSuccessListener(this) { location ->

            if(location != null){
                lastLocation = location
                val currentLatLong = LatLng(location.latitude,location.longitude)
                placeMarket(currentLatLong)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong, 15f))
            }
        }
    }

    override fun onMarkerClick(p0: Marker?) = false


     //------Metodo para mostrar mapa de calor
     fun startDemo(view: View?) {

         try {
             mLists!![getString(R.string.police_stations)] = DataSet(
                 ubicaciones!!,
                 getString(R.string.police_stations_url)
             )

         } catch (e: Exception) {
             Toast.makeText(this, "Problem reading list of markers.", Toast.LENGTH_LONG).show()
         }

         // Check if need to instantiate (avoid setData etc twice)

            mProvider = HeatmapTileProvider.Builder().data(
                 mLists!![getString(R.string.police_stations)]?.data
             ).build()
             mOverlay = mMap.addTileOverlay(TileOverlayOptions().tileProvider(mProvider))
             // Render links


         buttonGradiente.isClickable = true
         buttonOpacidad.isClickable = true
         buttonRadio.isClickable = true
        // Make the handler deal with the map
        // Input: list of WeightedLatLngs, minimum and maximum zoom levels to calculate custom
        // intensity from, and the map to draw the heatmap on
        // radius, gradient and opacity not specified, so default are used
    }


    private fun readItemsDataBase(): ArrayList<LatLng> {
        //-----Realizar consulta para traer nombre de usuario autenticado
        val list = ArrayList<LatLng>()
        val lat1: Double = 4.7692757
        val lng1: Double = -74.029453

        list.add(LatLng(lat1, lng1))
        val query: Query = database.reference.child("RegistroDiario").orderByChild("preguntaId").equalTo("1")

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (registroDiario in dataSnapshot.children) {
                        val estado: String = registroDiario.child("preguntaEstado").value.toString()
                        if (estado == "SI"){
                        val lat: Double = registroDiario.child("latitud").value as Double
                        val lng: Double = registroDiario.child("longitud").value as Double
                        Log.v("latitud",lat.toString())
                        Log.v("longitud",lng.toString())
                        list.add(LatLng(lat, lng))}


                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

        return list
    }

    fun changeRadius(view: View?) {
        if (mDefaultRadius) {
            mProvider!!.setRadius(ALT_HEATMAP_RADIUS)
        } else {
            mProvider!!.setRadius(HeatmapTileProvider.DEFAULT_RADIUS)
        }
        mOverlay!!.clearTileCache()
        mDefaultRadius = !mDefaultRadius
    }

    fun changeGradient(view: View?) {
        if (mDefaultGradient) {
            mProvider!!.setGradient(ALT_HEATMAP_GRADIENT)
        } else {
            mProvider!!.setGradient(HeatmapTileProvider.DEFAULT_GRADIENT)
        }
        mOverlay!!.clearTileCache()
        mDefaultGradient = !mDefaultGradient
    }

    fun changeOpacity(view: View?) {
        if (mDefaultOpacity) {
            mProvider!!.setOpacity(ALT_HEATMAP_OPACITY)
        } else {
            mProvider!!.setOpacity(HeatmapTileProvider.DEFAULT_OPACITY)
        }
        mOverlay!!.clearTileCache()
        mDefaultOpacity = !mDefaultOpacity
    }

    /**
     * Helper class - stores data sets and sources.
     */
    private class DataSet(val data: ArrayList<LatLng>, val url: String)

    //fin

}
