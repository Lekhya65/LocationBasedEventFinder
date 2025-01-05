package com.example.locationbasedeventfinder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.locationbasedeventfinder.data.repository.GeocoderRepository
import com.example.locationbasedeventfinder.domain.usecase.FetchEventsUseCase

class EventFinderViewModelFactory(
    private val fetchEventsUseCase: FetchEventsUseCase,
    private val geocoderRepository: GeocoderRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(EventFinderViewModel::class.java)) {
            EventFinderViewModel(fetchEventsUseCase, geocoderRepository) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
