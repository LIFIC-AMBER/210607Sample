package com.amber.sample.repository

import com.amber.sample.model.WeatherRow
import com.amber.sample.model.toWeatherRow
import com.amber.sample.utils.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

class WeatherUseCase(
    private val weatherRepository: WeatherRepository,
    private val dispatcher: CoroutineDispatcher
) {
    suspend fun getWeatherList(): Resource<List<WeatherRow>> = withContext(dispatcher) {
        val weatherList = mutableListOf<WeatherRow>()

        when (val localList = weatherRepository.getLocalList()) {
            is Resource.Success -> {
                if (localList.data == null) {
                    return@withContext Resource.Error<List<WeatherRow>>("Empty local list")
                }

                localList.data
                    .map { async { weatherRepository.getWeather(it) } }
                    .awaitAll()
                    .forEach {
                        when (it) {
                            is Resource.Success -> {
                                it.data?.let {
                                    weatherList.add(it.toWeatherRow())
                                }

                            }
                            else -> return@withContext Resource.Error<List<WeatherRow>>(
                                it.message ?: ""
                            )
                        }
                    }
            }
            is Resource.Error -> return@withContext Resource.Error<List<WeatherRow>>(
                localList.message ?: ""
            )
        }

        return@withContext Resource.Success(weatherList.toList())
    }
}