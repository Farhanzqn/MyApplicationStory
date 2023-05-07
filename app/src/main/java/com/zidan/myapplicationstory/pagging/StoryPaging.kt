package com.zidan.myapplicationstory.pagging

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.zidan.myapplicationstory.api.ApiService
import com.zidan.myapplicationstory.data.DataPreferences
import com.zidan.myapplicationstory.data.wrapEspressoIdlingResource
import com.zidan.myapplicationstory.response.ListStoryItem
import kotlinx.coroutines.flow.first
import java.lang.Exception
import javax.inject.Inject

class StoryPaging @Inject constructor(
    private val apiService: ApiService,
    private val dataPreferences: DataPreferences,
) : PagingSource<Int, ListStoryItem>() {

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { position ->
            val anchorPage = state.closestPageToPosition(position)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val position = params.key ?: INIT_PAGE_INDEX
            val token = dataPreferences.getDataSetting().first().token
            wrapEspressoIdlingResource {
                if (token.trim().isNotEmpty()) {
                    Log.d("token", "load: $token")
                    val responseData =
                        apiService.getStories("Bearer $token", position, params.loadSize)
                    Log.d("tag", "load: ${responseData.message()}")
                    if (responseData.isSuccessful) {
                        _loading.value = false
                        Log.d("token", "load no error: ${responseData.body()}")
                        LoadResult.Page(
                            data = responseData.body()?.listStory ?: emptyList(),
                            prevKey = if (position == 1) null else position - 1,
                            nextKey = if (responseData.body()?.listStory.isNullOrEmpty()) null else position + 1
                        )
                    } else {
                        Log.d("token", "load Error: $token")
                        LoadResult.Error(Exception("fail"))
                    }
                } else {
                    Log.d("tag", "load: Data Error2")
                    LoadResult.Error(Exception("Gagal"))
                }
            }
        } catch (e: Exception) {
            Log.d("exception", "load: Error ${e.message}")
            return LoadResult.Error(e)
        }
    }

    private companion object {
        const val INIT_PAGE_INDEX = 1
    }
}