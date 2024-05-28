package com.example.freeimagefeed.data.local_db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.freeimagefeed.base.base_classes.BaseModel

data class CommentEntity(
    val userName: String, val content: String
) : BaseModel

@Entity(tableName = "post_comments")
data class CommentContentEntity(
    @PrimaryKey
    val postId: String = "",
    val comments: List<CommentEntity> = listOf()
) : BaseModel