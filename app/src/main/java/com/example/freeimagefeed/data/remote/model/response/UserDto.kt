package com.example.freeimagefeed.data.remote.model.response

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.freeimagefeed.domain.models.UserModel
import com.example.freeimagefeed.domain.models.UserPageModel


@Entity(tableName = "users")
data class UserDto(
    @PrimaryKey val id: String,
    val title: String,
    val firstName: String,
    val lastName: String,
    val picture: String
) {
    fun toDomainModel(): UserModel {
        return UserModel(
            id = this.id,
            title = this.title,
            firstName = this.firstName,
            lastName = this.lastName,
            picture = this.picture
        )
    }
}

data class UserResponse(
    val data: List<UserDto>,
    val total: Int,
    val page: Int,
    val limit: Int
) {
    fun toDomainModel(): UserPageModel {
        return UserPageModel(
            data = this.data.map { it.toDomainModel() },
            total = this.total,
            page = this.page,
            limit = this.limit
        )
    }
}