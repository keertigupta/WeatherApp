package com.example.weatherapp.view

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.weatherapp.R
import com.example.weatherapp.viewModel.MainActivityViewModel
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_weather_additional_info.*
import kotlinx.android.synthetic.main.layout_weather_basic_info.*
import java.util.*


class MainActivity : AppCompatActivity() , GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private val PERMISSION_ID  = 1
    private lateinit var locationManager: LocationManager
    private lateinit var viewModel: MainActivityViewModel
    private  var cityName:String = "unknown"
    private var googleApiClient: GoogleApiClient? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)
        locationManager = getSystemService(Context.LOCATION_SERVICE)as LocationManager
        if(!checkLocationPermission()){
                requestPermission()
        }
        googleApiClient = GoogleApiClient.Builder(this, this, this).addApi(LocationServices.API).build()
    }


    private fun getCityName(MyLat:Double,MyLong :Double):String{
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses = geocoder.getFromLocation(MyLat, MyLong, 1)
        val cityName = addresses[0].locality
        return cityName
    }

    private fun checkLocationPermission() :Boolean{
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)
            ==PackageManager.PERMISSION_GRANTED &&ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            return true

        }
        return false
    }


    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION),PERMISSION_ID)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
      if(requestCode==PERMISSION_ID){
          if(grantResults.isNotEmpty()&& grantResults[0]==PackageManager.PERMISSION_GRANTED){
              // Granted. Start getting the location information

          }else{
              Toast.makeText(this, "Need your location!", Toast.LENGTH_SHORT).show();
          }
      }

    }
    override fun onStart() {
        super.onStart()
        if (googleApiClient != null) {
            googleApiClient!!.connect()
        }
    }

    override fun onStop() {
        googleApiClient!!.disconnect()
        super.onStop()
    }
    override fun onConnected(p0: Bundle?) {
        if(checkLocationPermission()){
            val lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient)
            val lat = lastLocation.latitude
            val lon = lastLocation.longitude
            cityName = getCityName(lat,lon)
            Log.d(">>>>>",cityName)
            viewModel!!.getWeatherData(cityName)
            viewModel!!.refreshData().observe(this, Observer {
                tv_temperature?.text = it.temperature
                tv_city_country?.text = it.cityAndCountry
                tv_humidity_value?.text = it.humidity
                tv_pressure_value?.text = it.pressure
                tv_visibility_value?.text = it.visibility
            })
            viewModel!!.progressBarLiveData.observe(this, Observer { isShowLoader ->
                if (isShowLoader)
                    progressBar.visibility = View.VISIBLE
                else
                    progressBar.visibility = View.GONE
            })

        }

    }

    override fun onConnectionSuspended(p0: Int) {
    }

    override fun onConnectionFailed(p0: ConnectionResult?) {
        Log.i(
            MainActivity::class.java.simpleName,
            "Can't connect to Google Play Services!"
        )
    }


}
