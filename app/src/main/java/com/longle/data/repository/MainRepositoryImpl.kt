package com.longle.data.repository

import androidx.lifecycle.distinctUntilChanged
import com.longle.data.api.ApiService
import com.longle.data.db.AppDatabase
import com.longle.data.resultLiveData
import com.longle.di.ActivityScope
import com.longle.util.RateLimiter
import com.longle.util.toResult
import javax.inject.Inject

/**
 * Repository module for handling data operations.
 */
@ActivityScope
class MainRepositoryImpl
@Inject constructor(
    private val rateLimiter: RateLimiter,
    private val db: AppDatabase,
    private val service: ApiService
) : MainRepository {

    override fun getUser() = resultLiveData(
        databaseQuery = { db.userDao().getUser() },
        shouldFetch = { rateLimiter.shouldFetch(USER_KEY_RATE_LIMITER) },
        networkCall = { suspend { service.getUser() }.toResult() },
        saveCallResult = { db.userDao().insert(it) })

    override fun getTimeLines() = db.timeLineDao().getTimeLines().distinctUntilChanged()

    companion object {
        private const val USER_KEY_RATE_LIMITER = "USER_KEY_RATE_LIMITER"
    }
}
