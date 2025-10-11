package com.example.cycles.data

interface SessionCacheContract {
    fun saveProfileMetadata(name: String, bio: String)
    fun getLocalName(): String?
    fun getLocalBio(): String?

}