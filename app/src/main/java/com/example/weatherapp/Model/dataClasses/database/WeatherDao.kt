package com.example.weatherapp.Model.dataClasses.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.weatherapp.Model.dataClasses.WeatherData

@Dao
interface WeatherDao {

    @Insert
    fun insertWeatherData(weatherData:WeatherData)

    @Query("SELECT * FROM weather_data")
    fun getWeatherData(): LiveData<WeatherData>

    @Query("DELETE FROM weather_data")
    fun deleteAll()
}