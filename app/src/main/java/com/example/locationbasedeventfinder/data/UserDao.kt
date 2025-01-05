package com.example.locationbasedeventfinder.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM users WHERE username = :username AND password = :password")
    suspend fun authenticateUser(username: String, password: String): User?

    @Query("SELECT * FROM users WHERE username = :username")
    suspend fun checkUserExists(username: String): User?
}
