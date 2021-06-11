package com.amber.sample.model

import com.google.gson.annotations.SerializedName

data class WeatherRow(
    val local: String,
    val today: Weather,
    val tomorrow: Weather
)

data class Weather(
    @SerializedName("weather_state_name") val stateName: String,
    @SerializedName("weather_state_abbr") val stateAbbr: String,
    @SerializedName("the_temp") val temp: Float,
    val humidity: Int
)

data class WeatherDTO(
    @SerializedName("consolidated_weather") val weatherList: List<Weather>,
    val title: String
)

data class Local(
    val title: String,
    val woeid: Long
)


fun WeatherDTO.toWeatherRow() = WeatherRow(title, weatherList[0], weatherList[1])