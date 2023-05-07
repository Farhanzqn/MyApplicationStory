package com.zidan.myapplicationstory.api

import com.zidan.myapplicationstory.login.LoginUser
import com.zidan.myapplicationstory.register.RegisterResponse
import com.zidan.myapplicationstory.register.RegisterUser
import com.zidan.myapplicationstory.response.LoginResponse
import com.zidan.myapplicationstory.response.StoryResponse
import com.zidan.myapplicationstory.response.UploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {

    @POST("login")
    fun login(
        @Body loginUser: LoginUser
    ): Call<LoginResponse>


    @POST("register")
    fun register(
        @Body registerUser: RegisterUser
    ): Call<RegisterResponse>

    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") auth: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<StoryResponse>

    @Multipart
    @POST("stories")
    fun addStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") latitude: RequestBody,
        @Part("lon") longitude: RequestBody
    ): Call<UploadResponse>

    @GET("stories")
    fun getStoriesLocation(
        @Header("Authorization") auth: String,
        @Query("location")location: Int = 1
    ): Call<StoryResponse>
}