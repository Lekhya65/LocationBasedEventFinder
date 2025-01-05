package com.example.locationbasedeventfinder

import android.Manifest
import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.location.Location
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.locationbasedeventfinder.data.model.Event
import com.example.locationbasedeventfinder.domain.usecase.FetchEventsUseCase
import com.example.locationbasedeventfinder.data.repository.GeocoderRepository
import com.google.android.gms.location.LocationServices
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun EventFinderScreen(
    fetchEventsUseCase: FetchEventsUseCase,
    viewModel: EventFinderViewModel = viewModel(factory = EventFinderViewModelFactory(fetchEventsUseCase, GeocoderRepository()))
) {
    val context = LocalContext.current
    val events by viewModel.events.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // Location permission launcher


    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    location?.let {
                        viewModel.fetchEvents(it.latitude, it.longitude)
                    } ?: Toast.makeText(context, "Unable to fetch location", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(context, "Location permission denied", Toast.LENGTH_LONG).show()
            }
        }
    )

    // Check and request permissions
    LaunchedEffect(Unit) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    viewModel.fetchEvents(it.latitude, it.longitude)
                } ?: Toast.makeText(context, "Unable to fetch location", Toast.LENGTH_LONG).show()
            }
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text(
                text = "Find Local Events",
                style = MaterialTheme.typography.headlineMedium.copy(fontSize = 24.sp),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Filters Section
            Filters(viewModel)

            Spacer(modifier = Modifier.height(16.dp))

            // Display events or loading/error states
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else if (!errorMessage.isNullOrEmpty()) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                EventList(events)
            }
        }
    }
}

@Composable
fun Filters(viewModel: EventFinderViewModel) {
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        DatePickerField("Start Date", startDate) { selectedDate ->
            startDate = selectedDate
        }

        DatePickerField("End Date", endDate) { selectedDate ->
            endDate = selectedDate
        }

        Button(
            onClick = {
                viewModel.applyFilters(startDate, endDate)
            },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) {
            Text("Apply Filters")
        }
    }
}

@Composable
fun DatePickerField(label: String, value: String, onDateSelected: (String) -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        LaunchedEffect(Unit) {
            DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        .format(calendar.apply { set(year, month, dayOfMonth) }.time)
                    onDateSelected(formattedDate)
                    showDatePicker = false
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    OutlinedTextField(
        value = value,
        onValueChange = {},
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        readOnly = true,
        trailingIcon = {
            IconButton(onClick = { showDatePicker = true }) {
                Icon(imageVector = Icons.Filled.DateRange, contentDescription = "Pick Date")
            }
        }
    )
}

@Composable
fun EventList(events: List<Event>) {
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        events.forEach { event ->
            EventCard(event)
        }
    }
}

@Composable
fun EventCard(event: Event) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = event.title, style = MaterialTheme.typography.titleMedium)
            Text(text = "Date: ${event.date}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Location: ${event.location}", style = MaterialTheme.typography.bodyMedium)
            Text(
                text = event.description,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
