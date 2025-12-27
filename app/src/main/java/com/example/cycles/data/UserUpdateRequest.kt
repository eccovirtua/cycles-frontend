package com.example.cycles.data

data class UserUpdateRequest(
    val country: String? = null,
    val show_age: Boolean? = null,
    val profile_picture: String? = null,
    val cover_image: String? = null
)