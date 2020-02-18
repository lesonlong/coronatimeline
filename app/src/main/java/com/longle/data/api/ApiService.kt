package com.longle.data.api

import com.longle.data.model.User
import retrofit2.Response
import retrofit2.http.GET

/**
 * REST API access points
 */
interface ApiService {

    companion object {
        const val ENDPOINT = "https://api.github.com/"
        const val GITHUB_USER = "google"
    }

    @GET("users/$GITHUB_USER")
    suspend fun getUser(): Response<User>
}
