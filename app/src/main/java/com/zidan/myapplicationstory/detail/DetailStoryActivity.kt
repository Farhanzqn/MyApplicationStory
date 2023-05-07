package com.zidan.myapplicationstory.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.zidan.myapplicationstory.databinding.ActivityDetailStoryBinding
import com.zidan.myapplicationstory.response.ListStoryItem


class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Detail"
        bind()
    }

    private fun bind() {
        val story = intent.getParcelableExtra<ListStoryItem>("ListStoryItem") as ListStoryItem
        binding.apply {
            Glide.with(applicationContext)
                .load(story.photoUrl)
                .into(binding.storyPhoto)
            binding.nameAccount.text = story.name
            binding.tvDescription.text = story.description
        }
    }
}