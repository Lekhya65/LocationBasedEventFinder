package com.example.locationbasedeventfinder

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.locationbasedeventfinder.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val userDao = AppDatabase.getDatabase(application).userDao()
    var message by mutableStateOf("")
    var isLoading by mutableStateOf(false)

    fun login(username: String, password: String, onResult: (Boolean) -> Unit) {
        if (username.isBlank() || password.isBlank()) {
            showMessage("Username and password cannot be empty.")
            onResult(false)
            return
        }

        isLoading = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val user = userDao.authenticateUser(username, password)
                withContext(Dispatchers.Main) {
                    isLoading = false
                    if (user != null) {
                        onResult(true)
                    } else {
                        showMessage("Invalid credentials.")
                        onResult(false)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    isLoading = false
                    showMessage("An error occurred. Please try again.")
                    onResult(false)
                }
            }
        }
    }

    fun showMessage(msg: String) {
        message = msg
    }
    suspend fun login(username: String, password: String): Boolean {
        return userDao.authenticateUser(username, password) != null
    }

}
