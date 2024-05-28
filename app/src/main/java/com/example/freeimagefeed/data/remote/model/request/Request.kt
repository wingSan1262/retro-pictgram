package com.example.freeimagefeed.data.remote.model.request

import com.example.freeimagefeed.base.base_classes.BasePaginationModel

data class UserListRequest (
    override var page: Int = 1,
    override var limit: Int = 20,
) : BasePaginationModel

data class UserPostRequest (
    var userId: String,
    override var page: Int = 1,
    override var limit: Int = 20,
) : BasePaginationModel

data class PostByTagRequest (
    var tag: String,
    override var page: Int = 1,
    override var limit: Int = 20,
) : BasePaginationModel