package com.amber.sample.utils

import com.amber.sample.api.WeatherService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkUtil {
    private const val URL = "https://www.metaweather.com/"

    private val _weatherService = createWeatherService()
    val weatherService = _weatherService

    private fun createWeatherService(): WeatherService {
        return Retrofit.Builder()
            .baseUrl(URL + "api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherService::class.java)
    }

    fun getWeatherAbbrUri(abbr: String) = URL + "static/img/weather/$abbr.svg"
}

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    //class Loading<T>(data: T? = null) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
}