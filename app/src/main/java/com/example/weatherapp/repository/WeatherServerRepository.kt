package com.example.weatherapp.repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.weatherapp.Model.dataClasses.WeatherData
import com.example.weatherapp.Model.dataClasses.WeatherInfoResponse
import com.example.weatherapp.Model.dataClasses.database.WeatherDatabaseRepository
import com.example.weatherapp.service.RetrofitClient
import com.example.weatherapp.utils.MySharePref
import com.example.weatherapp.utils.kelvinToCelsius
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherServerRepository(application: Application){

    private val repo = WeatherDatabaseRepository(application)
    private val mutableData:MutableLiveData<WeatherData> = MutableLiveData()
    private  val sharePref = MySharePref(application)
    private val refreshTime = 1*30*1000*1000*1000L
    private  val updateTime = sharePref.getUpdateTime()
    private  lateinit  var _city:String

    fun getWeatherData(cityName:String) : LiveData<WeatherData> {
        _city = cityName
        if(System.nanoTime()-updateTime>refreshTime) {
            Log.d(">>server","oooooooooooooo")
            val call: Call<WeatherInfoResponse> =
                RetrofitClient.service.callApiForWeatherInfo(cityName)
                call.enqueue(object : Callback<WeatherInfoResponse> {
                override fun onFailure(call: Call<WeatherInfoResponse>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<WeatherInfoResponse>,
                    response: Response<WeatherInfoResponse>
                ) {
                    lateinit var weatherData: WeatherData
                    Log.d(">>>URL", call.request().url().toString())
                    val data = response.body()
                    Log.d(">>>tem",data?.main?.temp.toString())

                    if (data != null) {
                        weatherData = WeatherData(

                            temperature = data.main.temp.kelvinToCelsius().toString(),
                            cityAndCountry = "${data.name}, ${data.sys.country}",
                            humidity = "${data.main.humidity}%",
                            pressure = "${data.main.pressure} mBar",
                            visibility = "${data.visibility / 1000.0} KM"

                        )
                    }

                    insert(weatherData)
                    mutableData.postValue(weatherData)

                }

            })
        }
        else{
            Log.d(">>db","pppppppppppppp")
           return  repo.getWeatherData()
        }
         return mutableData
     }
    fun insert( weatherData: WeatherData) {
        //repo.delete(_city)
        repo.insert(weatherData)
        sharePref.updateRefreshTime(System.nanoTime())
      //  weatherRetrieved(weatherData)
    }
      /*fun getWeatherInfo(cityName: String, callback: RequestCompleteListener<WeatherInfoResponse>) {

         var call: Call<WeatherInfoResponse> =
             RetrofitClient.service.callApiForWeatherInfo(cityName)

             call.enqueue(object : Callback<WeatherInfoResponse> {

             // if retrofit network call success, this method will be triggered
             override fun onResponse(call: Call<WeatherInfoResponse>, response: Response<WeatherInfoResponse>) {

              //   Log.d(">>>URL",call.request().url().toString())
                 if (response.body() != null)
                     callback.onRequestSuccess(response.body()!!) //let viewModel know the weather information data
                 else
                     callback.onRequestFailed(response.message()) //let viewModel know about failure
             }

             // this method will be triggered if network call failed
             override fun onFailure(call: Call<WeatherInfoResponse>, t: Throwable) {
                 callback.onRequestFailed(t.localizedMessage!!) //let viewModel know about failure
             }
         })
     }*/

}