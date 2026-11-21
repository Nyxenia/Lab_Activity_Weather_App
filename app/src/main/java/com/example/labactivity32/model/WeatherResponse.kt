package com.example.labactivity32.model

import com.squareup.moshi.Json

data class WeatherResponse(
    @field:Json(name = "name") val name: String?,
    @field:Json(name = "weather") val weather: List<WeatherDescription>?
)

data class WeatherDescription(
    @field:Json(name = "main") val main: String?,
    @field:Json(name = "description") val description: String?,
    @field:Json(name = "icon") val icon: String?
)
