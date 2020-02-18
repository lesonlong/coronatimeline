package com.longle.ui.main.di

import com.longle.data.repository.MainRepository
import com.longle.data.repository.MainRepositoryImpl
import com.longle.di.ActivityScope
import dagger.Module
import dagger.Provides

@Module
class MainActivityModule {

    @ActivityScope
    @Provides
    fun provideMainRepository(repo: MainRepositoryImpl): MainRepository = repo
}
