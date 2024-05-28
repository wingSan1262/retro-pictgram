package com.example.freeimagefeed.domain.usecase

import com.example.freeimagefeed.base.base_classes.BaseUseCase
import com.example.freeimagefeed.data.local_db.FriendDao
import com.example.freeimagefeed.data.remote.api.DummyRepoApi
import com.example.freeimagefeed.data.remote.model.request.UserListRequest
import com.example.freeimagefeed.domain.models.UserDetailModel
import com.example.freeimagefeed.domain.models.UserPageModel
import javax.inject.Inject

class UserDetailPageUseCase @Inject constructor(
    val api: DummyRepoApi,
    val friendDao: FriendDao
): BaseUseCase<String, UserDetailModel>() {
    override suspend fun run(param: String) {
        super.run(param)
        execute(
            {
                val friendList = friendDao.getFriendById(param)
                api.getUserDetail(param).copy(
                    isFriend = friendList != null
                )
            }
        ){
            null
        }
    }
}