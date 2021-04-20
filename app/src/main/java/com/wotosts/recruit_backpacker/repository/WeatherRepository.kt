package com.wotosts.recruit_backpacker.repository

import com.wotosts.recruit_backpacker.model.Local
import com.wotosts.recruit_backpacker.service.WeatherService
import com.wotosts.recruit_backpacker.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call

class WeatherRepository(private val weatherService: WeatherService) {
    suspend fun getLocalList() =
        withContext(Dispatchers.IO) { getResponse(weatherService.getLocalList("se")) }

    suspend fun getWeather(local: Local) = withContext(Dispatchers.IO) {
        getResponse(weatherService.getWeatherAt(local.woeid))
    }

    /**
     * https://narendrasinhdodiya.medium.com/android-architecture-mvvm-with-coroutines-retrofit-hilt-kotlin-flow-room-48e67ca3b2c8
     * */
    private suspend fun <T> getResponse(
        request: Call<T>,
        defaultErrorMessage: String = ""
    ): Resource<T> {
        return try {
            val result = request.execute()
            if (result.isSuccessful) {
                return Resource.Success(result.body()!!)
            } else {
                Resource.Error(result.errorBody().toString())
            }
        } catch (e: Throwable) {
            Resource.Error("Unknown Error")
        }
    }
}