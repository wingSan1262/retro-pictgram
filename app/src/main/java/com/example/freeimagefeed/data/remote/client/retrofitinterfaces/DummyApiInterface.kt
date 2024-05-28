package com.example.freeimagefeed.data.remote.client.retrofitinterfaces

import com.example.freeimagefeed.data.remote.model.response.PostResponseDto
import com.example.freeimagefeed.data.remote.model.response.UserDetailDto
import com.example.freeimagefeed.data.remote.model.response.UserResponse
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface DummyApiInterface {
    @Headers("app-id: 6653370f918ffa2c16f611f6")
    @GET("data/v1/user")
    suspend fun getUsers(
        @Query("page") page: Int,
        @Query("limit") limit: Int = 10
    ): UserResponse

    @Headers("app-id: 6653370f918ffa2c16f611f6")
    @GET("data/v1/user/{userId}")
    suspend fun getUserDetail(
        @Path("userId") userId: String
    ): UserDetailDto

    @Headers("app-id: 6653370f918ffa2c16f611f6")
    @GET("data/v1/user/{userId}/post")
    suspend fun getUserPosts(
        @Path("userId") userId: String,
        @Query("limit") limit: Int = 10,
        @Query("page") page: Int = 1
    ): PostResponseDto

    @Headers("app-id: 6653370f918ffa2c16f611f6")
    @GET("data/v1/post")
    suspend fun getLatestPosts(
        @Query("limit") limit: Int = 5,
        @Query("page") page: Int = 1
    ): PostResponseDto

    @Headers("app-id: 6653370f918ffa2c16f611f6")
    @GET("data/v1/tag/{tag}/post")
    suspend fun getPostsByTag(
        @Path("tag") tag: String,
        @Query("limit") limit: Int = 5,
        @Query("page") page: Int = 1
    ): PostResponseDto
}