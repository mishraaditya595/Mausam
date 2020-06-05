package com.e.mausam

import org.json.JSONException
import org.json.JSONObject

class WeatherDataModel {
    // Member variables that hold our relevant weather inforomation.
    private var mTemperature: String? = null
    var city: String? = null
        private set
    var iconName: String? = null
        private set
    private var mCondition = 0

    // Getter methods for temperature, city, and icon name:
    val temperature: String
        get() = "$mTemperature°"

    companion object {
        // Create a WeatherDataModel from a JSON.
        // We will call this instead of the standard constructor.
        fun fromJson(jsonObject: JSONObject): WeatherDataModel? {

            // JSON parsing is risky business. Need to surround the parsing code with a try-catch block.
            return try {
                val weatherData = WeatherDataModel()
                weatherData.city = jsonObject.getString("name")
                weatherData.mCondition =
                    jsonObject.getJSONArray("weather").getJSONObject(0).getInt("id")
                weatherData.iconName =
                    updateWeatherIcon(weatherData.mCondition)
                val tempResult =
                    jsonObject.getJSONObject("main").getDouble("temp") - 273.15
                val roundedValue = Math.rint(tempResult).toInt()
                weatherData.mTemperature = Integer.toString(roundedValue)
                weatherData
            } catch (e: JSONException) {
                e.printStackTrace()
                null
            }
        }

        // Get the weather image name from OpenWeatherMap's condition (marked by a number code)
        private fun updateWeatherIcon(condition: Int): String {
            if (condition >= 0 && condition < 300) {
                return "tstorm1"
            } else if (condition >= 300 && condition < 500) {
                return "light_rain"
            } else if (condition >= 500 && condition < 600) {
                return "shower3"
            } else if (condition >= 600 && condition <= 700) {
                return "snow4"
            } else if (condition >= 701 && condition <= 771) {
                return "fog"
            } else if (condition >= 772 && condition < 800) {
                return "tstorm3"
            } else if (condition == 800) {
                return "sunny"
            } else if (condition >= 801 && condition <= 804) {
                return "cloudy2"
            } else if (condition >= 900 && condition <= 902) {
                return "tstorm3"
            } else if (condition == 903) {
                return "snow5"
            } else if (condition == 904) {
                return "sunny"
            } else if (condition >= 905 && condition <= 1000) {
                return "tstorm3"
            }
            return "dunno"
        }
    }
}