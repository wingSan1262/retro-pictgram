package com.example.freeimagefeed.data.remote.model.response

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.freeimagefeed.domain.models.PostModel
import com.example.freeimagefeed.domain.models.PostPageModel

data class PostResponseDto(
    val data: List<PostDto>,
    val total: Int,
    val page: Int,
    val limit: Int
){
    fun toDomainModel(): PostPageModel {
        return PostPageModel(
            data = this.data.map { it.toDomainModel() },
            total = this.total,
            page = this.page,
            limit = this.limit
        )
    }
}



@Entity(tableName = "posts")
data class PostDto(
    @PrimaryKey val id: String,
    val image: String,
    val likes: Int,
    val tags: List<String>,
    val text: String,
    val publishDate: String,
    val owner: UserDto
){
    fun toDomainModel(): PostModel{
        return PostModel(
            id = this.id,
            image = this.image,
            likes = this.likes,
            tags = this.tags,
            text = this.text,
            publishDate = this.publishDate,
            owner = this.owner.toDomainModel()
        )
    }
}