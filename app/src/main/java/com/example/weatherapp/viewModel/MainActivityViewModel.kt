package com.example.weatherapp.viewModel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.weatherapp.Model.dataClasses.WeatherData
import com.example.weatherapp.Model.dataClasses.WeatherInfoResponse
import com.example.weatherapp.Model.dataClasses.database.WeatherDatabaseRepository
import com.example.weatherapp.Repository.WeatherServerRepository
import com.example.weatherapp.service.RequestCompleteListener
import com.example.weatherapp.utils.MySharePref

class MainActivityViewModel (application:Application): AndroidViewModel(application) {


    private  var weatherShowData = MutableLiveData<WeatherData>()
    val progressBarLiveData = MutableLiveData<Boolean>()
    // here we can also inject WeatherRepo by Dagger in constructor of MainActivityViewModel
    private val weatherRepo: WeatherServerRepository = WeatherServerRepository()
    private var weatherDbRepo: WeatherDatabaseRepository = WeatherDatabaseRepository(application)

    private  val sharePref = MySharePref(application)
    private val refreshTime = 2*60*1000*1000*1000L
    private  val updateTime = sharePref.getUpdateTime()

    fun insert( weatherData: WeatherData) {
        weatherDbRepo.insert(weatherData)
        sharePref.updateRefreshTime(System.nanoTime())
        weatherRetrieved(weatherData)
    }

    fun deleteAllNotes() {
        weatherDbRepo.delete()
    }

    fun getWeatherFromDb(): LiveData<WeatherData> {
        Log.d(">>>>i db","From DB")

        return weatherDbRepo.getWeatherData()
    }
    

    fun getWeatherData(city: String) {
        if(System.nanoTime()-updateTime>refreshTime){
            Log.d(">>>>in server","From server")
            progressBarLiveData.value = true
            weatherRepo.getWeatherInfo(city, object :
                RequestCompleteListener<WeatherInfoResponse> {
                override fun onRequestSuccess(data: WeatherInfoResponse) {
                    val weatherData = WeatherData(
                        temperature = data.main.temp.kelvinToCelsius().toString(),
                        cityAndCountry = "${data.name}, ${data.sys.country}",
                        humidity = "${data.main.humidity}%",
                        pressure = "${data.main.pressure} mBar",
                        visibility = "${data.visibility / 1000.0} KM"

                    )
                    insert(weatherData)

                }

                override fun onRequestFailed(errorMessage: String) {
                    progressBarLiveData.postValue(false)

                }

            })
        }else{

            // Here get data from db
              // weatherShowData = getWeatherFromDb()
        }

    }
    private fun weatherRetrieved(weatherData: WeatherData){

        progressBarLiveData.value = false
        weatherShowData.postValue(weatherData)
    }
   fun  refreshData() : LiveData<WeatherData>{

       return weatherShowData
   }
    fun Double.kelvinToCelsius() : Int {

        return  (this - 273.15).toInt()
    }


}