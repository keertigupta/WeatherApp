package com.example.weatherapp.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.weatherapp.Model.dataClasses.WeatherData
import com.example.weatherapp.repository.WeatherServerRepository

class MainActivityViewModel (application:Application): AndroidViewModel(application) {


    private lateinit var weatherShowData:LiveData<WeatherData>
    val progressBarLiveData = MutableLiveData<Boolean>()
    // here we can also inject WeatherRepo by Dagger in constructor of MainActivityViewModel
    private val weatherRepo: WeatherServerRepository = WeatherServerRepository(application)

    fun getWeatherData(city:String):LiveData<WeatherData>{
        weatherShowData= weatherRepo.getWeatherData(city)
        return weatherShowData
    }



}