package com.longle.ui.timeline

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.longle.R
import com.longle.data.model.Timeline
import com.longle.databinding.FragmentTimelineBinding
import com.longle.di.Injectable
import com.longle.di.ViewModelFactory
import com.longle.ui.timeline.adapter.TimelineAdapter
import com.longle.util.autoCleared
import com.longle.util.removeAllItemDecoration
import xyz.sangcomz.stickytimelineview.RecyclerSectionItemDecoration
import xyz.sangcomz.stickytimelineview.model.SectionInfo
import javax.inject.Inject


class TimelineFragment : Fragment(R.layout.fragment_timeline), TimelineViewModel.MessageEvent,
    Injectable {

    @Inject
    lateinit var factory: ViewModelFactory<TimelineViewModel>

    private val viewModel: TimelineViewModel by viewModels { factory }
    private var binding by autoCleared<FragmentTimelineBinding>()
    private var adapter by autoCleared<TimelineAdapter>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView(view)
        subscribeUi()
    }

    private fun setUpView(view: View) {
        binding = FragmentTimelineBinding.bind(view)
        adapter = TimelineAdapter(viewModel)
        binding?.recyclerView?.adapter = adapter
    }

    private fun subscribeUi() {
        binding?.lifecycleOwner = viewLifecycleOwner
        binding?.viewModel = viewModel
        viewModel.uiEvent.setEventReceiver(viewLifecycleOwner, this)
        viewModel.timeLines.observe(viewLifecycleOwner, Observer {
            binding?.recyclerView?.removeAllItemDecoration()
            binding?.recyclerView?.addItemDecoration(getSectionCallback(it))
            adapter?.submitList(it)
        })
    }

    override fun showMap(timeline: Timeline) {
        val gmmIntentUri: Uri = Uri.parse("geo:${timeline.latitude},${timeline.longitude}")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
    }

    private fun getSectionCallback(singerList: List<Timeline>): RecyclerSectionItemDecoration.SectionCallback {
        return object : RecyclerSectionItemDecoration.SectionCallback {
            //In your data, implement a method to determine if this is a section.
            override fun isSection(position: Int): Boolean =
                singerList[position].date != singerList[position - 1].date

            //Implement a method that returns a SectionHeader.
            override fun getSectionHeader(position: Int): SectionInfo? =
                if (position >= 0) SectionInfo(
                    singerList[position].date,
                    singerList[position].dayOfWeek
                ) else null
        }
    }
}
