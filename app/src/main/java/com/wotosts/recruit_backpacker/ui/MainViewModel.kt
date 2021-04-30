package com.wotosts.recruit_backpacker.ui

import android.app.Application
import androidx.lifecycle.*
import com.wotosts.recruit_backpacker.R
import com.wotosts.recruit_backpacker.model.WeatherRow
import com.wotosts.recruit_backpacker.repository.WeatherRepository
import com.wotosts.recruit_backpacker.utils.Event
import com.wotosts.recruit_backpacker.utils.Resource
import kotlinx.coroutines.*

//private const val TAG = "MainViewModel"

class MainViewModel(
    application: Application,
    private val weatherRepository: WeatherRepository
) : AndroidViewModel(application) {
    private val _weatherListLiveData = MutableLiveData<List<WeatherRow>>()
    val weatherListLiveData: LiveData<List<WeatherRow>> = _weatherListLiveData

    private val _refreshEvent = MutableLiveData<Event<String>>()
    val refreshEvent: LiveData<Event<String>> = _refreshEvent

    fun refreshWeather() {
        if (_weatherListLiveData.value != null) _weatherListLiveData.value = mutableListOf()
        viewModelScope.launch {
            val weatherList = mutableListOf<WeatherRow>()
            when (val localList = weatherRepository.getLocalList()) {
                is Resource.Success ->
                    with(localList.data!!) {
                        map { async { weatherRepository.getWeather(it) } }
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

                        val errorString = when {
                            isEmpty() || (isNotEmpty() && weatherList.size == 0) -> getApplication<Application>().getString(R.string.refresh_error)
                            size > weatherList.size -> getApplication<Application>().getString(R.string.refresh_some_error)
                            else -> ""
                        }
                        _refreshEvent.postValue(Event(errorString))
                    }
                is Resource.Error -> _refreshEvent.postValue(
                    Event(
                        getApplication<Application>().getString(
                            R.string.refresh_error
                        )
                    )
                )
            }
            _weatherListLiveData.postValue(weatherList)
        }
    }
}

class MainViewModelFactory(
    private val application: Application,
    private val weatherRepository: WeatherRepository
) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> MainViewModel(
                application,
                weatherRepository
            ) as T
            else -> super.create(modelClass)
        }
    }
}