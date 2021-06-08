package com.amber.sample.ui

import androidx.lifecycle.*
import com.amber.sample.R
import com.amber.sample.model.WeatherRow
import com.amber.sample.repository.WeatherRepository
import com.amber.sample.utils.Event
import com.amber.sample.utils.Resource
import kotlinx.coroutines.*

//private const val TAG = "MainViewModel"

class WeatherViewModel(
    private val weatherRepository: WeatherRepository
) : ViewModel() {
    private val _weatherListLiveData = MutableLiveData<List<WeatherRow>>()
    val weatherListLiveData: LiveData<List<WeatherRow>> = _weatherListLiveData

    private val _refreshEvent = MutableLiveData<Event<State>>()
    val refreshEvent: LiveData<Event<State>> = _refreshEvent

    init {
        refreshWeather()
    }

    fun refreshWeather() {
        if (_weatherListLiveData.value != null) _weatherListLiveData.value = mutableListOf()
        viewModelScope.launch {
            _refreshEvent.value = Event(State.Loading)
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

                        _refreshEvent.value = when {
                            isEmpty() || (isNotEmpty() && weatherList.size == 0) -> Event(State.Failed(R.string.refresh_error))
                            size > weatherList.size -> Event(State.Failed(R.string.refresh_some_error))
                            else -> Event(State.Success)
                        }
                    }
                is Resource.Error -> _refreshEvent.value = Event(State.Failed(R.string.refresh_error))
            }
            _weatherListLiveData.postValue(weatherList)
        }
    }
}

class MainViewModelFactory(
    private val weatherRepository: WeatherRepository
) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(WeatherViewModel::class.java) -> WeatherViewModel(
                weatherRepository
            ) as T
            else -> super.create(modelClass)
        }
    }
}

sealed class State {
    object Loading : State()
    object Success : State()
    data class Failed(val errorMsg: Int) : State()
}