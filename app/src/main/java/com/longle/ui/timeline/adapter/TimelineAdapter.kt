package com.longle.ui.timeline.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.longle.data.model.Timeline
import com.longle.databinding.ItemTimelineBinding
import com.longle.ui.common.DataBoundListAdapter
import com.longle.ui.timeline.TimelineViewModel

class TimelineAdapter(
    private val viewModel: TimelineViewModel
) : DataBoundListAdapter<Timeline, ItemTimelineBinding>(object : DiffUtil.ItemCallback<Timeline>() {
    override fun areItemsTheSame(oldItem: Timeline, newItem: Timeline): Boolean {
        return oldItem.date == newItem.date
                && oldItem.address == newItem.address
                && oldItem.stayFrom == newItem.stayFrom
    }

    override fun areContentsTheSame(oldItem: Timeline, newItem: Timeline): Boolean {
        return oldItem == newItem
    }
}) {

    override fun createBinding(parent: ViewGroup): ItemTimelineBinding {
        val binding = ItemTimelineBinding.inflate(LayoutInflater.from(parent.context))
        binding.root.setOnClickListener {
            binding.timeline?.let {
                viewModel.onTimelineItemClicked(it)
            }
        }
        binding.btMap.setOnClickListener {
            binding.timeline?.let {
                viewModel.onOpenMapButtonClicked(it)
            }
        }
        return binding
    }

    override fun bind(binding: ItemTimelineBinding, item: Timeline) {
        binding.timeline = item
    }
}
