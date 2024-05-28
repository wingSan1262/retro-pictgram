package com.example.freeimagefeed.domain.usecase

import com.example.freeimagefeed.base.base_classes.BaseUseCase
import com.example.freeimagefeed.data.local_db.CommentContentEntity
import com.example.freeimagefeed.data.local_db.CommentDao
import com.example.freeimagefeed.data.local_db.PostDao
import com.example.freeimagefeed.domain.models.PostModel
import javax.inject.Inject

class InsertDeletePostUseCase @Inject constructor(
    val api: PostDao
): BaseUseCase<Unit, Unit>() {

    suspend fun insertPost(post: PostModel) {
        execute(
            {
                api.insert(
                    post.toDto()
                )
            }
        ){
            null
        }
    }

    suspend fun deletePost(post: PostModel) {
        execute(
            {
                api.deletePost(
                    post.id
                )
            }
        ){
            null
        }
    }

}

class GetLikedPostUseCase @Inject constructor(
    val api: PostDao,
    val commentDao: CommentDao,
): BaseUseCase<Unit, List<PostModel>>() {
    override suspend fun run(param: Unit) {
        super.run(param)
        execute(
            {
                api.getAllPosts().map {
                    it.toDomainModel().apply {
                        isLiked = true
                        comment = commentDao.getCommentContentByPostId(it.id) ?: CommentContentEntity()
                    }
                }
            }
        ){
            null
        }
    }
}