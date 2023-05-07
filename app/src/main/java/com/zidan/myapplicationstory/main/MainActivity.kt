package com.zidan.myapplicationstory.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.zidan.myapplicationstory.R
import com.zidan.myapplicationstory.story.StoryActivity
import com.zidan.myapplicationstory.databinding.ActivityMainBinding
import com.zidan.myapplicationstory.login.LoginActivity
import com.zidan.myapplicationstory.maps.MapsActivity
import com.zidan.myapplicationstory.pagging.PagingAdapter
import com.zidan.myapplicationstory.story.StoryAdapter
import com.zidan.myapplicationstory.story.StoryViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var activityMainBinding: ActivityMainBinding
    private val userViewModel by viewModels<UserViewModel>()
    private val storyViewModel by viewModels<StoryViewModel>()
    private lateinit var storyAdapter: StoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        setupAdapter()
        setClick()
        checkUserStatus()

        storyViewModel.isLoading.observe(this){
            setProgressDialog(it)
        }
    }

    private fun checkUserStatus() {
        userViewModel.getDataSession().observe(this) {
            if (it.token.trim() == "") {
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            } else {
                setupData()
            }
        }
    }

    private fun setupData() {
        storyViewModel.allStory.observe(this) { pagingData ->
            storyAdapter.submitData(lifecycle, pagingData)
            setProgressDialog(false)
        }
    }


    private fun logoutUser() {
        userViewModel.clearDataSession()
        checkUserStatus()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_language -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }
            R.id.menu_logout -> {
                logoutUser()
            }
            R.id.menu_maps -> {
                startActivity(Intent(this@MainActivity, MapsActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setClick() {
        activityMainBinding.createAddStory.setOnClickListener {
            val intent = Intent(this@MainActivity, StoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupAdapter() {

        storyAdapter = StoryAdapter()
        activityMainBinding.snapStory.layoutManager = LinearLayoutManager(this@MainActivity)
        activityMainBinding.snapStory.adapter = storyAdapter.withLoadStateFooter(
            footer = PagingAdapter {
                storyAdapter.retry()
            }
        )
    }

    private fun setProgressDialog(isLoading : Boolean) {
        activityMainBinding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}