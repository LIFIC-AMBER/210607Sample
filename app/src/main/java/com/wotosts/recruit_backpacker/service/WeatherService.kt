package com.wotosts.recruit_backpacker.service

import com.wotosts.recruit_backpacker.model.Local
import com.wotosts.recruit_backpacker.model.WeatherDTO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WeatherService {
    @GET("location/search/")
    fun getLocalList(@Query("query") query: String): Call<List<Local>>

    @GET("location/{woeid}")
    fun getWeatherAt(@Path("woeid") woeid: Long): Call<WeatherDTO>
}