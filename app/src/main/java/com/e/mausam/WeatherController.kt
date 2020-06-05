package com.e.mausam

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import org.json.JSONObject

class WeatherController : AppCompatActivity() {
    val REQUEST_CODE = 123
    val NEW_CITY_CODE = 456
    val WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather"

    // App ID to use OpenWeather data
    val APP_ID = "e72ca729af228beabd5d20e3b7749713"

    // Time between location updates (5000 milliseconds or 5 seconds)
    val MIN_TIME: Long = 5000

    // Distance between location updates (1000m or 1km)
    val MIN_DISTANCE = 1000f

    // Don't want to type 'Clima' in all the logs, so putting this in a constant here.
    val LOGCAT_TAG = "Clima"

    // Set LOCATION_PROVIDER here. Using GPS_Provider for Fine Location (good for emulator):
    // Recommend using LocationManager.NETWORK_PROVIDER on physical devices (reliable & fast!)
    val LOCATION_PROVIDER = LocationManager.GPS_PROVIDER

    // Member Variables:
    var mUseLocation = true
    var mCityLabel: TextView? = null
    var mWeatherImage: ImageView? = null
    var mTemperatureLabel: TextView? = null

    // Declaring a LocationManager and a LocationListener here:
    var mLocationManager: LocationManager? = null
    var mLocationListener: LocationListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather_controller)

        Toast.makeText(this,"It might take a few moments to retrieve the data.",Toast.LENGTH_LONG).show()

        // Linking the elements in the layout to Java code.
        // API 26 and above does not require casting anymore.
        // Can write: mCityLabel = findViewById(R.id.locationTV);
        // Instead of: mCityLabel = (TextView) findViewById(R.id.locationTV);
        mCityLabel = findViewById(R.id.locationTV)
        mWeatherImage = findViewById(R.id.weatherSymbolIV)
        mTemperatureLabel = findViewById(R.id.tempTV)
        val changeCityButton = findViewById<Button>(R.id.changeCityButton)

        // Add an OnClickListener to the changeCityButton here:
        changeCityButton.setOnClickListener {
            val myIntent = Intent(this@WeatherController, ChangeCityController::class.java)

            // Using startActivityForResult since we just get back the city name.
            // Providing an arbitrary request code to check against later.
            startActivityForResult(myIntent, NEW_CITY_CODE)
        }
    }

    // onResume() lifecycle callback:
    override fun onResume() {
        super.onResume()
        Log.d(LOGCAT_TAG, "onResume() called")
        if (mUseLocation) weatherForCurrentLocation
    }

    // Callback received when a new city name is entered on the second screen.
    // Checking request code and if result is OK before making the API call.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(LOGCAT_TAG, "onActivityResult() called")
        if (requestCode == NEW_CITY_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val city = data?.getStringExtra("City")
                Log.d(LOGCAT_TAG, "New city is $city")
                mUseLocation = false
                if (city != null) {
                    getWeatherForNewCity(city)
                }
            }
        }
    }

    // Configuring the parameters when a new city has been entered:
    private fun getWeatherForNewCity(city: String) {
        Log.d(LOGCAT_TAG, "Getting weather for new city")
        val params = RequestParams()
        params.put("q", city)
        params.put("appid", APP_ID)
        letsDoSomeNetworking(params)
    }// TODO: Consider calling
    //    ActivityCompat#requestPermissions
    // here to request the missing permissions, and then overriding
    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
    //                                          int[] grantResults)
    // to handle the case where the user grants the permission. See the documentation
    // for ActivityCompat#requestPermissions for more details.


//         Speed up update on screen by using last known location.
    //        String longitude = String.valueOf(lastLocation.getLongitude());
//        String latitude = String.valueOf(lastLocation.getLatitude());
//        RequestParams params = new RequestParams();
//        params.put("lat", latitude);
//        params.put("lon", longitude);
//        params.put("appid", APP_ID);
//        letsDoSomeNetworking(params);

    // Some additional log statements to help you debug
// Log statements to help you debug your app.

    // This is the permission check to access (fine) location.
// Providing 'lat' and 'lon' (spelling: Not 'long') parameter values

    // Location Listener callbacks here, when the location has changed.
    private val weatherForCurrentLocation: Unit
        private get() {
            Log.d(LOGCAT_TAG, "Getting weather for current location")
            mLocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            mLocationListener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    Log.d(LOGCAT_TAG, "onLocationChanged() callback received")
                    val longitude = location.longitude.toString()
                    val latitude = location.latitude.toString()
                    Log.d(LOGCAT_TAG, "longitude is: $longitude")
                    Log.d(LOGCAT_TAG, "latitude is: $latitude")

                    // Providing 'lat' and 'lon' (spelling: Not 'long') parameter values
                    val params = RequestParams()
                    params.put("lat", latitude)
                    params.put("lon", longitude)
                    params.put("appid", APP_ID)
                    letsDoSomeNetworking(params)
                }

                override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
                    // Log statements to help you debug your app.
                    Log.d(LOGCAT_TAG, "onStatusChanged() callback received. Status: $status")
                    Log.d(LOGCAT_TAG, "2 means AVAILABLE, 1: TEMPORARILY_UNAVAILABLE, 0: OUT_OF_SERVICE")
                }

                override fun onProviderEnabled(provider: String) {
                    Log.d(LOGCAT_TAG, "onProviderEnabled() callback received. Provider: $provider")
                }

                override fun onProviderDisabled(provider: String) {
                    Log.d(LOGCAT_TAG, "onProviderDisabled() callback received. Provider: $provider")
                }
            }

            // This is the permission check to access (fine) location.
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE)
                return
            }


//         Speed up update on screen by using last known location.
            val lastLocation = mLocationManager!!.getLastKnownLocation(LOCATION_PROVIDER)
            //        String longitude = String.valueOf(lastLocation.getLongitude());
//        String latitude = String.valueOf(lastLocation.getLatitude());
//        RequestParams params = new RequestParams();
//        params.put("lat", latitude);
//        params.put("lon", longitude);
//        params.put("appid", APP_ID);
//        letsDoSomeNetworking(params);

            // Some additional log statements to help you debug
            Log.d(LOGCAT_TAG, "Location Provider used: "
                    + mLocationManager!!.getProvider(LOCATION_PROVIDER).name)
            Log.d(LOGCAT_TAG, "Location Provider is enabled: "
                    + mLocationManager!!.isProviderEnabled(LOCATION_PROVIDER))
            Log.d(LOGCAT_TAG, "Last known location (if any): "
                    + mLocationManager!!.getLastKnownLocation(LOCATION_PROVIDER))
            Log.d(LOGCAT_TAG, "Requesting location updates")
            mLocationManager!!.requestLocationUpdates(LOCATION_PROVIDER, MIN_TIME, MIN_DISTANCE, mLocationListener)
        }

    // This is the callback that's received when the permission is granted (or denied)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Checking against the request code we specified earlier.
        if (requestCode == REQUEST_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(LOGCAT_TAG, "onRequestPermissionsResult(): Permission granted!")

                // Getting weather only if we were granted permission.
                weatherForCurrentLocation
            } else {
                Log.d(LOGCAT_TAG, "Permission denied =( ")
            }
        }
    }

    // This is the actual networking code. Parameters are already configured.
    private fun letsDoSomeNetworking(params: RequestParams) {

        // AsyncHttpClient belongs to the loopj dependency.
        val client = AsyncHttpClient()

        // Making an HTTP GET request by providing a URL and the parameters.
        client[WEATHER_URL, params, object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, response: JSONObject) {
                Log.d(LOGCAT_TAG, "Success! JSON: $response")
                val weatherData = WeatherDataModel.fromJson(response)
                if (weatherData != null) {
                    updateUI(weatherData)
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>, e: Throwable, response: JSONObject) {
                Log.e(LOGCAT_TAG, "Fail $e")
                Toast.makeText(this@WeatherController, "Request Failed", Toast.LENGTH_SHORT).show()
                Log.d(LOGCAT_TAG, "Status code $statusCode")
                Log.d(LOGCAT_TAG, "Here's what we got instead $response")
            }
        }]
    }

    // Updates the information shown on screen.
    private fun updateUI(weather: WeatherDataModel) {
        mTemperatureLabel!!.text = weather.temperature + "C"
        mCityLabel!!.text = weather.city

        // Update the icon based on the resource id of the image in the drawable folder.
        val resourceID = resources.getIdentifier(weather.iconName, "drawable", packageName)
        mWeatherImage!!.setImageResource(resourceID)
    }

    // Freeing up resources when the app enters the paused state.
    override fun onPause() {
        super.onPause()
        if (mLocationManager != null) mLocationManager!!.removeUpdates(mLocationListener)
    }
}











