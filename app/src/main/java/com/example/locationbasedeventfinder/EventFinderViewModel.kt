package com.example.locationbasedeventfinder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.locationbasedeventfinder.data.model.Event
import com.example.locationbasedeventfinder.data.repository.GeocoderRepository
import com.example.locationbasedeventfinder.domain.usecase.FetchEventsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EventFinderViewModel(
    private val fetchEventsUseCase: FetchEventsUseCase,
    private val geocoderRepository: GeocoderRepository
) : ViewModel() {

    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun fetchEvents(lat: Double, lng: Double, startDate: String? = null, endDate: String? = null) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val country = geocoderRepository.getCountry(lat, lng)
                val events = fetchEventsUseCase.execute(lat, lng, startDate, endDate,
                    country.toString()
                )
                _events.value = events
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun applyFilters(startDate: String?, endDate: String?) {
        // Handle filter application logic here
    }
}
