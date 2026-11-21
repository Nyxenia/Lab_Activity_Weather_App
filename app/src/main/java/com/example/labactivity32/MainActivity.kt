package com.example.labactivity32

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.labactivity32.model.WeatherResponse
import com.example.labactivity32.network.WeatherApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var tvCity: TextView
    private lateinit var tvMain: TextView
    private lateinit var tvDescription: TextView
    private lateinit var ivWeatherIcon: ImageView

    companion object {
        private const val TAG = "MainActivity"
        private const val BASE_URL = "https://api.openweathermap.org/"
        // OpenWeatherMap API key
        private const val API_KEY = "090a70b64fa43010beef0a43e8922feb"
        private const val CITY_NAME = "Cebu City"
    }

    private val weatherApiService: WeatherApiService by lazy {
        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(WeatherApiService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvCity = findViewById(R.id.tvCity)
        tvMain = findViewById(R.id.tvMain)
        tvDescription = findViewById(R.id.tvDescription)
        ivWeatherIcon = findViewById(R.id.ivWeatherIcon)

        // Set initial icon placeholder
        ivWeatherIcon.setImageResource(R.drawable.ic_weather_placeholder)

        // Fetch current weather data from OpenWeatherMap API
        fetchWeatherData()
    }

    private fun fetchWeatherData() {
        if (API_KEY.isBlank()) {
            Log.w(TAG, "API_KEY is empty. Please provide a valid OpenWeatherMap API key.")
            return
        }

        weatherApiService.getWeather(CITY_NAME, API_KEY).enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                if (response.isSuccessful) {
                    val weatherResponse = response.body()
                    if (weatherResponse != null) {
                        handleSuccess(weatherResponse)
                    } else {
                        handleError("Response body is empty")
                    }
                } else {
                    handleError("Error ${response.code()}: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                handleError("Network failure: ${t.localizedMessage}")
            }
        })
    }

    private fun handleSuccess(weatherResponse: WeatherResponse) {
        val city = weatherResponse.name ?: CITY_NAME
        val weatherInfo = weatherResponse.weather?.firstOrNull()

        tvCity.text = city
        tvMain.text = weatherInfo?.main ?: "Clouds"
        tvDescription.text = weatherInfo?.description ?: "broken clouds"

        val iconCode = weatherInfo?.icon
        if (!iconCode.isNullOrEmpty()) {
            val iconUrl = "https://openweathermap.org/img/w/$iconCode.png"
            Glide.with(this@MainActivity)
                .load(iconUrl)
                .placeholder(R.drawable.ic_weather_placeholder)
                .error(R.drawable.ic_weather_placeholder)
                .into(ivWeatherIcon)
        }
    }

    private fun handleError(errorMessage: String) {
        Log.e(TAG, errorMessage)
        Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
    }
}
