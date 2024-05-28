package com.example.freeimagefeed.domain.usecase

import com.example.freeimagefeed.base.base_classes.BaseUseCase
import com.example.freeimagefeed.data.local_db.CommentContentEntity
import com.example.freeimagefeed.data.local_db.CommentDao
import com.example.freeimagefeed.data.local_db.FriendDao
import com.example.freeimagefeed.domain.models.UserModel
import javax.inject.Inject

class CommentPostUseCase @Inject constructor(
    val commentDao: CommentDao
): BaseUseCase<Unit, Unit>() {

    suspend fun insertComment(user: CommentContentEntity) {
        execute(
            {
                commentDao.insertCommentContent(user)
            }
        ){
            null
        }
    }

}