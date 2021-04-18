package com.wotosts.recruit_backpacker.model

data class WeatherRow(
    val local: String,
    val today: Weather,
    val tomorrow: Weather
)

data class Weather(
    val stateName: String,
    val stateAbbr: String,
    val temp: Float,
    val humidity: Int
)

data class Location(
    val title: String,
    val woeid: Long
)