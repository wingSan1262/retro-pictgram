package com.example.freeimagefeed.domain.usecase

import com.example.freeimagefeed.base.base_classes.BaseUseCase
import com.example.freeimagefeed.data.local_db.CommentContentEntity
import com.example.freeimagefeed.data.local_db.CommentDao
import com.example.freeimagefeed.data.local_db.PostDao
import com.example.freeimagefeed.data.remote.api.DummyRepoApi
import com.example.freeimagefeed.data.remote.model.request.PostByTagRequest
import com.example.freeimagefeed.data.remote.model.response.PostDto
import com.example.freeimagefeed.domain.models.PostPageModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TaggedPostUseCase @Inject constructor(
    val api: DummyRepoApi,
    val likePostDao: PostDao,
    val commentDao: CommentDao
) : BaseUseCase<PostByTagRequest, PostPageModel>() {

    override suspend fun run(param: PostByTagRequest) {
        super.run(param)
        execute(
            {
                var likedPosts: List<PostDto> = emptyList()
                withContext(Dispatchers.IO) {
                    likedPosts = likePostDao.getAllPosts()
                }
                val obtainedPosts = api.getPostByTag(
                        tag = param.tag,
                        limit = param.limit,
                        page = param.page
                    )

                obtainedPosts.data = obtainedPosts.data.map {
                    it.copy(
                        isLiked = likedPosts.any { likedPost ->
                            likedPost.id == it.id
                        },
                        comment = commentDao.getCommentContentByPostId(it.id)
                            ?: CommentContentEntity()
                    )
                }

                obtainedPosts
            }
        ) {
            null
        }
    }
}