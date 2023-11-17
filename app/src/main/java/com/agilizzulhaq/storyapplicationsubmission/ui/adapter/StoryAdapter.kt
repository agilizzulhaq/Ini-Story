package com.agilizzulhaq.storyapplicationsubmission.ui.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.agilizzulhaq.storyapplicationsubmission.data.responses.ListStoryItem
import com.agilizzulhaq.storyapplicationsubmission.databinding.ItemStoryBinding
import com.agilizzulhaq.storyapplicationsubmission.ui.activity.DetailStoryActivity
import com.agilizzulhaq.storyapplicationsubmission.utils.withDateFormat
import com.bumptech.glide.Glide

class StoryAdapter : PagingDataAdapter<ListStoryItem, StoryAdapter.MyViewHolderStory>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolderStory {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolderStory(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolderStory, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    class MyViewHolderStory(private val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ListStoryItem) {
            Glide.with(binding.root.context)
                .load(data.photoUrl)
                .into(binding.imgItemPhoto)
            binding.tvItemName.text = data.name
            binding.tvItemCreated.text = data.createdAt.withDateFormat()
            binding.tvItemDescription.text = data.description
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailStoryActivity::class.java)
                intent.putExtra(DetailStoryActivity.NAME, data.name)
                intent.putExtra(DetailStoryActivity.CREATE_AT, data.createdAt)
                intent.putExtra(DetailStoryActivity.DESCRIPTION, data.description)
                intent.putExtra(DetailStoryActivity.PHOTO_URL, data.photoUrl)
                intent.putExtra(DetailStoryActivity.LONGITUDE, data.lon.toString())
                intent.putExtra(DetailStoryActivity.LATITUDE, data.lat.toString())
                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        androidx.core.util.Pair(binding.imgItemPhoto, "photo"),
                        androidx.core.util.Pair(binding.tvItemName, "name"),
                        androidx.core.util.Pair(binding.tvItemCreated, "createdate"),
                        androidx.core.util.Pair(binding.tvItemDescription, "description"),
                    )
                itemView.context.startActivity(intent, optionsCompat.toBundle())
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}