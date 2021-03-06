package com.longle.di

import android.app.Application
import android.content.Context
import com.longle.data.api.ApiService
import com.longle.data.db.AppDatabase
import com.longle.util.RateLimiter
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module(includes = [CoreDataModule::class])
class AppModule {

    @Singleton
    @Provides
    fun provideContext(app: Application): Context = app

    @Singleton
    @Provides
    fun provideApiService(
        okHttpClient: OkHttpClient,
        converterFactory: GsonConverterFactory
    ) = provideService(okHttpClient, converterFactory, ApiService::class.java)

    @Singleton
    @Provides
    fun provideDb(app: Application) = AppDatabase.getInstance(app)

    @Singleton
    @Provides
    fun provideUserDao(db: AppDatabase) = db.userDao()

    @Singleton
    @Provides
    fun provideTimeLineDao(db: AppDatabase) = db.timeLineDao()

    @Singleton
    @Provides
    fun provideRateLimiter() = RateLimiter(60, TimeUnit.MINUTES)

    private fun <T> provideService(
        okHttpClient: OkHttpClient,
        converterFactory: GsonConverterFactory, clazz: Class<T>
    ): T {
        return createRetrofit(okHttpClient, converterFactory).create(clazz)
    }

    private fun createRetrofit(
        okHttpClient: OkHttpClient,
        converterFactory: GsonConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ApiService.ENDPOINT)
            .client(okHttpClient)
            .addConverterFactory(converterFactory)
            .build()
    }
}
