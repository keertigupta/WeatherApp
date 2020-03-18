package com.example.weatherapp.utils

import android.content.Context
import android.content.SharedPreferences

class MySharePref( context: Context) {

    private var PRIVATE_MODE = 0
    private val PREF_NAME = "weather_time"

    val sharedPref: SharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)

    fun updateRefreshTime(time:Long){
        val editor = sharedPref.edit()
        editor.putLong(PREF_NAME,time)
        editor.apply()

    }

    fun getUpdateTime():Long{
        return sharedPref.getLong(PREF_NAME,0L)
    }
}