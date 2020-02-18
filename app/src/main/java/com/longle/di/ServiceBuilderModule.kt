package com.longle.di

import com.longle.location.LocationService
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class ServiceBuilderModule {

    @ServiceScope
    @ContributesAndroidInjector
    abstract fun contributeLocationService(): LocationService
}