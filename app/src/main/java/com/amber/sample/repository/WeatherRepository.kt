package com.amber.sample.repository

import com.amber.sample.model.Local
import com.amber.sample.api.WeatherService
import com.amber.sample.utils.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call

class WeatherRepository(
    private val weatherService: WeatherService
) {
    suspend fun getLocalList() = getResponse(weatherService.getLocalList("se"))

    suspend fun getWeather(local: Local) = getResponse(weatherService.getWeatherAt(local.woeid))

    /**
     * https://narendrasinhdodiya.medium.com/android-architecture-mvvm-with-coroutines-retrofit-hilt-kotlin-flow-room-48e67ca3b2c8
     * */
    private fun <T> getResponse(
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