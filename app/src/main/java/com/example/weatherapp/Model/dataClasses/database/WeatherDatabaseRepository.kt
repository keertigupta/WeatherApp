package com.example.weatherapp.Model.dataClasses.database

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import com.example.weatherapp.Model.dataClasses.WeatherData

class WeatherDatabaseRepository (application:Application) {
    private var weatherDao: WeatherDao
    private val allData: LiveData<WeatherData>

    init {
        val roomDatabase = WeatherRoomDatabase.getDataBase(application)
        weatherDao = roomDatabase.getWeatherDao()
        allData = weatherDao.getWeatherData()

    }

    fun insert(weatherData:WeatherData){
            InsertWeatherAsyTask(weatherDao).execute(weatherData)
    }
    fun delete(city:String){
            DeleteWeatherAsyTask(weatherDao).execute(city)
    }
    fun getWeatherData():LiveData<WeatherData>{
        return allData
    }

    private class InsertWeatherAsyTask(val weatherDao: WeatherDao): AsyncTask<WeatherData, Unit, Unit>(){
        override fun doInBackground(vararg params: WeatherData?) {
            weatherDao.insertWeatherData(params[0]!!)
        }

    }
    private class DeleteWeatherAsyTask(val weatherDao: WeatherDao): AsyncTask<String, Unit, Unit>(){

        override fun doInBackground(vararg params: String?) {
            weatherDao.delete(params[0]!!)
        }

    }


}