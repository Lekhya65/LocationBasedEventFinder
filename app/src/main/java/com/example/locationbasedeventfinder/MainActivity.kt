package com.example.locationbasedeventfinder

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.locationbasedeventfinder.domain.usecase.FetchEventsUseCase
import com.example.locationbasedeventfinder.data.repository.EventRepository

class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
        } else {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
        }

        setContent {
            val navController = rememberNavController()
            val repository = EventRepository("0df18c24d8d0a905ab58e4db803151427253ff7401fddedc72cde8d7f9da643d")
            val fetchEventsUseCase = FetchEventsUseCase(repository)
            AppNavigation(navController, fetchEventsUseCase)
        }
    }
}

@Composable
fun AppNavigation(navController: NavHostController, fetchEventsUseCase: FetchEventsUseCase) {
    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("eventFinder") { EventFinderScreen(fetchEventsUseCase) }
    }
}