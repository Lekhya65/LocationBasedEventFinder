package com.example.locationbasedeventfinder.domain.usecase

import com.example.locationbasedeventfinder.data.model.Event
import com.example.locationbasedeventfinder.data.repository.EventRepository
import com.example.locationbasedeventfinder.data.repository.GeocoderRepository

class FetchEventsUseCase(private val repository: EventRepository) {

    suspend fun execute(lat: Double, lng: Double, startDate: String?, endDate: String?, country: String): List<Event> {
        val query = "Events in $country"
        return repository.fetchEvents(query, lat, lng, startDate, endDate)
    }
}

