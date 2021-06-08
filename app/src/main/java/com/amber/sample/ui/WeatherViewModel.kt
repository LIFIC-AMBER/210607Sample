package com.amber.sample.ui

import android.util.Log
import androidx.lifecycle.*
import com.amber.sample.R
import com.amber.sample.model.Local
import com.amber.sample.model.WeatherRow
import com.amber.sample.model.toWeatherRow
import com.amber.sample.repository.WeatherRepository
import com.amber.sample.utils.Event
import com.amber.sample.utils.Resource
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.system.measureTimeMillis

//private const val TAG = "MainViewModel"

class WeatherViewModel(
    private val weatherRepository: WeatherRepository
) : ViewModel() {
    private val _weatherListLiveData = MutableStateFlow<List<WeatherRow>>(listOf())
    val weatherListLiveData: StateFlow<List<WeatherRow>> = _weatherListLiveData

    private val _refreshEvent = MutableStateFlow<Event<State>>(Event(State.Loading))
    val refreshEvent: StateFlow<Event<State>> = _refreshEvent

    private val _clickedWeather = MutableStateFlow<Event<WeatherRow?>>(Event(null))
    val clickedWeather: StateFlow<Event<WeatherRow?>> = _clickedWeather

    @FlowPreview
    fun refreshWeather() {
        viewModelScope.launch {
            val time = measureTimeMillis {
                _refreshEvent.value = Event(State.Loading)
                val weatherList = mutableListOf<WeatherRow>()
                weatherRepository.getLocalList()
                    .collect {
                        when (it) {
                            is Resource.Success -> {
                                it.data?.let { localList ->
                                    localList.asFlow()
                                        .flatMapMerge(concurrency = localList.size) { local ->
                                            weatherRepository.getWeather(local)
                                        }.collect { resource ->
                                            resource.data?.let { weatherDTO ->
                                                //Log.d(TAG, "weatherRow created")
                                                weatherList.add(weatherDTO.toWeatherRow())
                                            }
                                        }

                                    _refreshEvent.value = getWeatherEvent(localList, weatherList)
                                    //_refreshEvent.postValue(getWeatherEvent(localList, weatherList))
                                }
                            }
                            is Resource.Error -> {
                                _refreshEvent.value = Event(State.Failed(R.string.refresh_error))
                            }
                        }
                    }

                _weatherListLiveData.value = weatherList
            }

            Log.d("time", "${time / 1000f}")
        }
    }

    private fun getWeatherEvent(localList: List<Local>, weatherRowList: List<WeatherRow>) = when {
        localList.isEmpty() || weatherRowList.isEmpty() -> Event(
            State.Failed(
                R.string.refresh_error
            )
        )
        localList.size > weatherRowList.size -> Event(
            State.Failed(
                R.string.refresh_some_error
            )
        )
        else -> Event(State.Success)
    }

    fun onClickWeather(weatherRow: WeatherRow) {
        _clickedWeather.value = Event(weatherRow)
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