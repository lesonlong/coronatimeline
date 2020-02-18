package com.longle.di

import com.longle.ui.facemask.FacemaskFragment
import com.longle.ui.facemask.di.FacemaskFragmentModule
import com.longle.ui.knowledge.KnowledgeFragment
import com.longle.ui.statistic.StatisticFragment
import com.longle.ui.timeline.TimelineFragment
import com.longle.ui.timeline.di.TimelineFragmentModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class FragmentBuildersModule {

    @FragmentScope
    @ContributesAndroidInjector(modules = [TimelineFragmentModule::class])
    abstract fun contributeTimelineFragment(): TimelineFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [FacemaskFragmentModule::class])
    abstract fun contributeFacemaskFragment(): FacemaskFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributeStatisticFragment(): StatisticFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributeKnowledgeFragment(): KnowledgeFragment
}
