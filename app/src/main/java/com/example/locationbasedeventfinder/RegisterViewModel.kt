package com.example.locationbasedeventfinder

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.locationbasedeventfinder.data.AppDatabase
import com.example.locationbasedeventfinder.data.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val userDao = AppDatabase.getDatabase(application).userDao()
    var message by mutableStateOf("")

    fun registerUser(username: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val userExists = userDao.checkUserExists(username) != null
            if (userExists) {
                message = "User already exists"
            } else {
                userDao.insertUser(User(username, password))
                message = "Registration successful!"
            }
        }
    }

    fun showMessage(msg: String) {
        message = msg
    }
}
