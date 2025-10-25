package com.example.cycles.data

import com.google.gson.annotations.SerializedName

data class UserUsageStatus(
    @SerializedName("daily_limit") val dailyLimit: Int,
    @SerializedName("sessions_today") val sessionsToday: Int,
    @SerializedName("remaining_today") val remainingToday: Int
)