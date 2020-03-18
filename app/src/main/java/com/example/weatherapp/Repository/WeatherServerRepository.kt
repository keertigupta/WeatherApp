package com.example.weatherapp.Repository

import android.util.Log
import com.example.weatherapp.Model.dataClasses.WeatherInfoResponse
import com.example.weatherapp.service.RetrofitClient
import com.example.weatherapp.service.RequestCompleteListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherServerRepository{


     /*fun getWeatherData(cityName:String) : MutableLiveData<WeatherInfoResponse> {
      //   RetrofitClient.service.callApiForWeatherInfo(cityName).url().toString()
         var call: Call<WeatherInfoResponse> =
             RetrofitClient.service.callApiForWeatherInfo(cityName)
         call.enqueue(object : Callback<WeatherInfoResponse> {
             override fun onFailure(call: Call<WeatherInfoResponse>, t: Throwable) {

             }

             override fun onResponse(
                 call: Call<WeatherInfoResponse>,
                 response: Response<WeatherInfoResponse>
             ) {
                 Log.d(">>>URL",call.request().url().toString())
                 var body = response.body()
                  mutableData.postValue(body)

             }

         })
         return mutableData
     }*/
      fun getWeatherInfo(cityName: String, callback: RequestCompleteListener<WeatherInfoResponse>) {

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
     }

}