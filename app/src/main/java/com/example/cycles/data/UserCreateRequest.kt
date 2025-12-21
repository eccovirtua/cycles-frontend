package com.example.cycles.data

import com.google.gson.annotations.SerializedName

data class UserCreateRequest (
    val username: String,
    val age: Int,
    val email: String,
    @SerializedName("firebaseUid")
    val firebaseUid: String
)