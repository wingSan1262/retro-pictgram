package com.example.freeimagefeed.domain.usecase

import com.example.freeimagefeed.base.base_classes.BaseUseCase
import com.example.freeimagefeed.data.local_db.CommentDao
import com.example.freeimagefeed.data.local_db.FriendDao
import com.example.freeimagefeed.data.remote.api.DummyRepoApi
import com.example.freeimagefeed.data.remote.model.request.UserListRequest
import com.example.freeimagefeed.data.remote.model.response.UserDto
import com.example.freeimagefeed.domain.models.UserPageModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserListPageUseCase @Inject constructor(
    val api: DummyRepoApi,
    val friendDao: FriendDao,
): BaseUseCase<UserListRequest, UserPageModel>() {

    override suspend fun run(param: UserListRequest) {
        super.run(param)
        execute(
            {
                val obtainedUser = api.getUsers(param)
                obtainedUser.data = obtainedUser.data.map {
                    it.copy(
                        isFriend = friendDao.getFriendById(it.id) != null

                    )
                }
                obtainedUser
            }
        ){
            null
        }
    }
}