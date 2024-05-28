package com.example.freeimagefeed.data.remote.api

import com.example.freeimagefeed.data.remote.client.retrofitinterfaces.DummyApiInterface
import com.example.freeimagefeed.data.remote.model.request.UserListRequest
import com.example.freeimagefeed.domain.models.PostPageModel
import com.example.freeimagefeed.domain.models.UserDetailModel
import com.example.freeimagefeed.domain.models.UserModel
import com.example.freeimagefeed.domain.models.UserPageModel
import dagger.hilt.InstallIn
import retrofit2.http.Query
import javax.inject.Inject

interface DummyRepoApi {
    suspend fun getUsers(req: UserListRequest): UserPageModel
    suspend fun getUserDetail(userId: String): UserDetailModel
    suspend fun getUserPosts(userId: String, limit: Int = 10, page: Int = 1): PostPageModel
    suspend fun getLatestPost(
       limit: Int = 5,
       page: Int = 1
    ): PostPageModel

    suspend fun getPostByTag(
        tag: String,
        limit: Int = 5,
        page: Int = 1
    ): PostPageModel
}

class DummyRepoApiImpt @Inject constructor(
    private val api: DummyApiInterface
) : DummyRepoApi {
    override suspend fun getUsers(
        req: UserListRequest
    ): UserPageModel {
        return api.getUsers(req.page, req.limit).toDomainModel()
    }

    override suspend fun getUserDetail(userId: String): UserDetailModel {
        return api.getUserDetail(userId).toDomainModel()
    }

    override suspend fun getUserPosts(userId: String, limit: Int, page: Int): PostPageModel {
        return api.getUserPosts(userId, limit, page).toDomainModel()
    }

    override suspend fun getLatestPost(limit: Int, page: Int): PostPageModel {
        return api.getLatestPosts(limit, page).toDomainModel()
    }

    override suspend fun getPostByTag(tag: String, limit: Int, page: Int): PostPageModel {
        return api.getPostsByTag(tag, limit, page).toDomainModel()
    }


}