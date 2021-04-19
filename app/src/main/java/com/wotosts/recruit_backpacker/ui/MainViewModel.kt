package com.wotosts.recruit_backpacker.ui

import androidx.lifecycle.*
import com.wotosts.recruit_backpacker.model.WeatherRow
import com.wotosts.recruit_backpacker.repository.WeatherRepository
import com.wotosts.recruit_backpacker.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

private const val TAG = "MainViewModel"

class MainViewModel(private val weatherRepository: WeatherRepository) : ViewModel() {
    private val _weatherListLiveData = MutableLiveData<List<WeatherRow>?>()
    val weatherListLiveData = _weatherListLiveData as LiveData<List<WeatherRow>?>

    init {
        refreshWeather()
    }

    fun refreshWeather() {
        _weatherListLiveData.value = mutableListOf()
        viewModelScope.launch(Dispatchers.IO) {
            when (val localList = weatherRepository.getLocalList()) {
                is Resource.Success -> {
                    val weatherList = mutableListOf<WeatherRow>()
                    localList.data?.let { list ->
                        list.map { async { weatherRepository.getWeather(it) } }
                            .awaitAll()
                            .forEach { resource ->
                                resource.data?.let { weatherDTO ->
                                    //Log.d(TAG, "weatherRow created")
                                    weatherList.add(
                                        WeatherRow(
                                            weatherDTO.title,
                                            weatherDTO.weatherList[0],
                                            weatherDTO.weatherList[1]
                                        )
                                    )
                                }
                            }

                        _weatherListLiveData.postValue(if (weatherList.size == 0) null else weatherList)
                    }
                }
                is Resource.Error -> {
                    _weatherListLiveData.postValue(null)
                }
            }
        }
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