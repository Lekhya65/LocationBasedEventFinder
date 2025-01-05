package com.example.locationbasedeventfinder.data.repository

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class GeocoderRepository() {

    private val client = OkHttpClient()

    suspend fun getCountry(lat: Double, lng: Double): String? {
        val url = "https://api.opencagedata.com/geocode/v1/json?q=$lat,$lng&key=d759dda13a254dd8a9ebf6b6f839fbb3"
        Log.d("GeocoderRepository", "URL: $url")

        val request = Request.Builder()
            .url(url)
            .build()

        val response = withContext(Dispatchers.IO) {
            client.newCall(request).execute()
        }

        val responseBody = response.body?.string() ?: return null
        Log.d("GeocoderRepository", "Response: $responseBody")

        val json = JSONObject(responseBody)
        val results = json.getJSONArray("results")

        if (results.length() == 0) return null

        val components = results.getJSONObject(0).getJSONObject("components")
        val country = components.optString("country", null)
        Log.d("GeocoderRepository", "Country: $country")
        return country
    }
}