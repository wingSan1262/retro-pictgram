package com.example.freeimagefeed.domain.models

import com.example.freeimagefeed.base.base_classes.BaseModel
import com.example.freeimagefeed.data.remote.model.response.UserDto


data class UserModel(
    val id: String = "",
    val title: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val picture: String = "",
    val isFriend: Boolean = false
) : BaseModel {
    fun toDto() = UserDto(
        id = this.id,
        title = this.title,
        firstName = this.firstName,
        lastName = this.lastName,
        picture = this.picture
    )
}

data class UserPageModel(
    var data: List<UserModel>,
    val total: Int,
    val page: Int,
    val limit: Int
) : BaseModel