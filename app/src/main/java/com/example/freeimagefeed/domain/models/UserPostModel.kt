package com.example.freeimagefeed.domain.models

import com.example.freeimagefeed.base.base_classes.BaseModel
import com.example.freeimagefeed.data.local_db.CommentContentEntity
import com.example.freeimagefeed.data.local_db.CommentEntity
import com.example.freeimagefeed.data.remote.model.response.PostDto
import org.w3c.dom.Comment

data class PostPageModel(
    var data: List<PostModel>,
    val total: Int,
    val page: Int,
    val limit: Int
): BaseModel

data class PostModel(
    val id: String,
    val image: String,
    val likes: Int,
    val tags: List<String>,
    val text: String,
    val publishDate: String,
    val owner: UserModel,
    var isLiked : Boolean = false,
    var comment : CommentContentEntity =  CommentContentEntity(),
    var isCollapsed : Boolean = true
) : BaseModel {
    fun toDto() = PostDto(
        id = this.id,
        image = this.image,
        likes = this.likes,
        tags = this.tags,
        text = this.text,
        publishDate = this.publishDate,
        owner = this.owner.toDto()
    )
}