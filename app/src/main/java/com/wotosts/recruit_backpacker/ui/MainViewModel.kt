package com.wotosts.recruit_backpacker.ui

import android.util.Log
import androidx.lifecycle.*
import com.wotosts.recruit_backpacker.model.WeatherRow
import com.wotosts.recruit_backpacker.repository.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

private const val TAG = "MainViewModel"

class MainViewModel(private val weatherRepository: WeatherRepository) : ViewModel() {
    private val _weatherListLiveData = MutableLiveData<List<WeatherRow>>()
    val weatherListLiveData = _weatherListLiveData as LiveData<List<WeatherRow>>

    init {
        refreshWeather()
    }

    fun refreshWeather() {
        _weatherListLiveData.value = mutableListOf()
        viewModelScope.launch(Dispatchers.IO) {
            val weatherList = mutableListOf<WeatherRow>()
            val localList = weatherRepository.getLocalList()        // 따로 저장해두기..
            localList.data?.let {
                it.map {
                    async { weatherRepository.getWeathers(it) }
                }.awaitAll()
                    .forEach { resource ->
                        resource.data?.let { weatherDTO ->
                            Log.d(TAG, "weatherRow created")
                            weatherList.add(WeatherRow(
                                weatherDTO.title,
                                weatherDTO.weatherList[0],
                                weatherDTO.weatherList[1]
                            ))
                        }
                    }

                _weatherListLiveData.postValue(weatherList)
            }
        }

        Log.d(TAG, "refresh")
    }
}

class MainViewModelFactory(private val weatherRepository: WeatherRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> MainViewModel(
                weatherRepository
            ) as T
            else -> super.create(modelClass)
        }
    }
}