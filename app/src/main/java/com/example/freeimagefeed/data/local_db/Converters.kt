package com.example.freeimagefeed.data.local_db

import androidx.room.TypeConverter
import com.example.freeimagefeed.data.remote.model.response.UserDto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class StringListConverter {
    @TypeConverter
    fun fromString(value: String): List<String> {
        return value.split(",").map { it.trim() }
    }

    @TypeConverter
    fun fromList(list: List<String>): String {
        return list.joinToString(",")
    }
}

class UserDtoConverter {
    @TypeConverter
    fun fromUserDto(userDto: UserDto): String {
        return Gson().toJson(userDto)
    }

    @TypeConverter
    fun toUserDto(data: String): UserDto {
        return Gson().fromJson(data, UserDto::class.java)
    }
}

class CommentEntityListConverter {
    @TypeConverter
    fun fromCommentEntityList(commentEntityList: List<CommentEntity>?): String? {
        if (commentEntityList == null) {
            return null
        }
        val gson = Gson()
        val type = object : TypeToken<List<CommentEntity>>() {}.type
        return gson.toJson(commentEntityList, type)
    }

    @TypeConverter
    fun toCommentEntityList(commentEntityListString: String?): List<CommentEntity>? {
        if (commentEntityListString == null) {
            return null
        }
        val gson = Gson()
        val type = object : TypeToken<List<CommentEntity>>() {}.type
        return gson.fromJson(commentEntityListString, type)
    }
}