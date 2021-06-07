package com.amber.sample.repository

import com.amber.sample.R
import com.amber.sample.model.Local
import com.amber.sample.api.WeatherService
import com.amber.sample.model.WeatherDTO
import com.amber.sample.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import retrofit2.Call

class WeatherRepository(private val weatherService: WeatherService) {
    fun getWeather(): Flow<Resource<List<WeatherDTO>>> = flow {
        emit(Resource.Loading())

        when(val resource = getResponse(weatherService.getLocalList("se"))) {
            is Resource.Error -> {
                emit(Resource.Error("error"))
            }
            is Resource.Success -> {
                with(resource.data!!) {
                    val weatherResources = mutableListOf<WeatherDTO>()
                    map {
                        asFlow().flatMapMerge(concurrency = 20) {
                            flow { emit(getResponse(weatherService.getWeatherAt(it.woeid))) }
                        }
                            .collect {
                                when(it) {
                                    is Resource.Error -> {}
                                    is Resource.Success -> { it.data?.let { data -> weatherResources.add(data) } }
                                }
                            }
                    }
                    emit(Resource.Success(weatherResources))
                }
            }
        }
    }

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