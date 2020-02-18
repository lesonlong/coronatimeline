package com.longle.ui.timeline

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.longle.data.model.Timeline
import com.longle.data.repository.MainRepository
import com.longle.di.FragmentScope
import com.longle.util.BaseLiveEvent
import javax.inject.Inject

/**
 * The ViewModel for [TimelineFragment].
 */
@FragmentScope
class TimelineViewModel @Inject constructor(
    repository: MainRepository
) : ViewModel() {

    val uiEvent = BaseLiveEvent<MessageEvent>()

    val timeLines: LiveData<List<Timeline>> = repository.getTimeLines()

    fun onTimelineItemClicked(timeline: Timeline) {
    }

    fun onOpenMapButtonClicked(timeline: Timeline) {
        uiEvent.sendEvent { showMap(timeline) }
    }

    interface MessageEvent {
        fun showMap(timeline: Timeline)
    }
}
