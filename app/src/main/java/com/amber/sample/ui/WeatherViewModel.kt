package com.amber.sample.ui

import androidx.lifecycle.*
import com.amber.sample.R
import com.amber.sample.model.WeatherRow
import com.amber.sample.repository.WeatherUseCase
import com.amber.sample.utils.Event
import com.amber.sample.utils.Resource
import kotlinx.coroutines.*

//private const val TAG = "MainViewModel"

class WeatherViewModel(
    private val weatherUseCase: WeatherUseCase
) : ViewModel() {
    private val _weatherListLiveData = MutableLiveData<List<WeatherRow>>()
    val weatherListLiveData: LiveData<List<WeatherRow>> = _weatherListLiveData

    private val _refreshEvent = MutableLiveData<Event<State>>()
    val refreshEvent: LiveData<Event<State>> = _refreshEvent

    private val _clickedWeather = MutableLiveData<Event<WeatherRow>>()
    val clickedWeather: LiveData<Event<WeatherRow>> = _clickedWeather

    fun refreshWeather() {
        viewModelScope.launch {
            _refreshEvent.value = Event(State.Loading)
            val weatherList = weatherUseCase.getWeatherList()

            when (weatherList) {
                is Resource.Success -> {
                    _refreshEvent.value = Event(State.Success)
                    weatherList.data?.let { _weatherListLiveData.value = it }
                }
                is Resource.Error -> {
                    _refreshEvent.value = Event(State.Failed(R.string.refresh_error))
                }
            }
        }
    }

    fun onClickWeather(weatherRow: WeatherRow) {
        _clickedWeather.value = Event(weatherRow)
    }
}

class MainViewModelFactory(
    private val weatherUseCase: WeatherUseCase
) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(WeatherViewModel::class.java) -> WeatherViewModel(
                weatherUseCase
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