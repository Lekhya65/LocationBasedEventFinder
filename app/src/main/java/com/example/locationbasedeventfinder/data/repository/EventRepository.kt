package com.example.locationbasedeventfinder.data.repository

import android.util.Log
import com.example.locationbasedeventfinder.data.model.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.net.URLEncoder

class EventRepository(private val apiKey: String) {

    private val client = OkHttpClient()

    suspend fun fetchEvents(
        query: String,
        lat: Double,
        lng: Double?,
        startDate: String? = null,
        endDate: String? = null
    ): List<Event> {
        val url = buildQuery(query, lat, lng, startDate, endDate)
        Log.d("EventRepository", "URL: $url")

        val request = Request.Builder()
            .url(url)
            .build()

        val response = withContext(Dispatchers.IO) {
            client.newCall(request).execute()
        }

        val responseBody = response.body?.string() ?: return emptyList()
        Log.d("EventRepository", "Response: $responseBody")

        val json = JSONObject(responseBody)
        if (!json.has("events_results")) {
            Log.w("EventRepository", "No events found in the response")
            return emptyList()
        }

        val eventsArray = json.getJSONArray("events_results")
        val events = mutableListOf<Event>()
        for (i in 0 until eventsArray.length()) {
            val eventJson = eventsArray.getJSONObject(i)
            val address = eventJson.getJSONArray("address").join(", ")
            val date = eventJson.getJSONObject("date").getString("when")
            events.add(
                Event(
                    title = eventJson.getString("title"),
                    date = date,
                    location = address,
                    description = eventJson.optString("description", "No description available")
                )
            )
        }

        return events
    }

    private fun buildQuery(
        query: String,
        lat: Double,
        lng: Double?,
        startDate: String?,
        endDate: String?
    ): String {
        val baseUrl = "https://serpapi.com/search.json"
        val encodedQuery = URLEncoder.encode(query, "UTF-8")
        val queryParams = mutableListOf(
            "engine=google_events",
            "q=$encodedQuery",
            "api_key=$apiKey"
        )
        startDate?.let { queryParams.add("startDate=$it") }
        endDate?.let { queryParams.add("endDate=$it") }
        return "$baseUrl?${queryParams.joinToString("&")}"
    }
}