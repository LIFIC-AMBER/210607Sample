package com.amber.sample.ui

import androidx.lifecycle.*
import com.amber.sample.R
import com.amber.sample.model.WeatherRow
import com.amber.sample.repository.WeatherRepository
import com.amber.sample.utils.Event
import com.amber.sample.utils.Resource
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.map

//private const val TAG = "MainViewModel"

class WeatherViewModel(
    private val weatherRepository: WeatherRepository
) : ViewModel() {
    private val _refreshEvent = MutableLiveData<Event<State>>()
    val refreshEvent: LiveData<Event<State>> = _refreshEvent

    private val _weatherListLiveData = MutableLiveData<List<WeatherRow>>()
    val weatherListLiveData: LiveData<List<WeatherRow>> = weatherRepository.getWeather().map {
        when (it) {
            is Resource.Loading -> {
                _refreshEvent.postValue(Event(State.Loading))
                listOf<WeatherRow>()
            }
            is Resource.Error -> {
                _refreshEvent.postValue(Event(State.Failed(R.string.refresh_error)))
                listOf<WeatherRow>()
            }
            is Resource.Success -> {
                if (it.data == null) {
                    _refreshEvent.postValue(Event(State.Failed(R.string.refresh_error)))
                    listOf<WeatherRow>()
                } else {
                    _refreshEvent.postValue(Event(State.Success))
                    it.data.map { dto ->
                        WeatherRow(
                            dto.title,
                            dto.weatherList[0],
                            dto.weatherList[1]
                        )
                    }
                }
            }
        }
    }.asLiveData(Dispatchers.IO)

    fun refreshWeather() {
        if (_weatherListLiveData.value != null) _weatherListLiveData.value = mutableListOf()
        viewModelScope.launch {
            val weatherList = mutableListOf<WeatherRow>()
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