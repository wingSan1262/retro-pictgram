package com.example.freeimagefeed.domain.usecase

import com.example.freeimagefeed.base.base_classes.BaseUseCase
import com.example.freeimagefeed.data.local_db.FriendDao
import com.example.freeimagefeed.domain.models.UserModel
import javax.inject.Inject

class InsertFriendPostUseCase @Inject constructor(
    val api: FriendDao
): BaseUseCase<Unit, Unit>() {

    suspend fun insertFriend(user: UserModel) {
        execute(
            {
                api.insertFriend(user.toDto())
            }
        ){
            null
        }
    }

    suspend fun deleteFriend(user: UserModel) {
        execute(
            {
                api.deleteFriend(user.id)
            }
        ){
            null
        }
    }

}