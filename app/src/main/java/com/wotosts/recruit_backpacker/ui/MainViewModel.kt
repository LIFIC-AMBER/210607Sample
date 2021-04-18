package com.wotosts.recruit_backpacker.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wotosts.recruit_backpacker.model.Weather
import com.wotosts.recruit_backpacker.model.WeatherRow

class MainViewModel : ViewModel() {
    private val _weatherLiveData = MutableLiveData<List<Any>>()
    val weatherLiveData = _weatherLiveData as LiveData<List<Any>>

    init {
        val testList = mutableListOf<Any>().apply {
            add(WeatherRow(
                "Seoul",
                        Weather("Heavy Rain", "", 4f, 65),
                        Weather("Light Cloud", "", 6f, 40)
            ))
            add(WeatherRow(
                "San Jose",
                Weather("Heavy Rain", "", 4f, 65),
                Weather("Light Cloud", "", 6f, 40)
            ))
        }
        _weatherLiveData.value = testList
    }
}