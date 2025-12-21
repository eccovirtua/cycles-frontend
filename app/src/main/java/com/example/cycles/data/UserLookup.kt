package com.example.cycles.data

import com.google.gson.annotations.SerializedName


data class UserLookupResponse(
    @SerializedName("email") val email: String?
)