package com.example.freeimagefeed.base.base_entity


data class ResourceState<T>(
    val isSuccess: Boolean,
    val isError: Boolean,
    val isLoading: Boolean,
    val data: T? = null,
    val code: Int = 200,
    val error: Throwable? = null,
)