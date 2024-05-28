package com.example.freeimagefeed.data.local_db

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import com.example.freeimagefeed.base.base_classes.BaseModel
import com.example.freeimagefeed.data.remote.model.response.PostDto
import com.example.freeimagefeed.data.remote.model.response.UserDto

@Dao
interface PostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: PostDto)

    @Query("SELECT * FROM posts")
    suspend fun getAllPosts(): List<PostDto>

    // select specific post by id
    @Query("SELECT * FROM posts WHERE id = :postId")
    suspend fun getPostById(postId: String): PostDto?

    @Query("DELETE FROM posts")
    suspend fun deleteAllPosts()

    // remove specific post
    @Query("DELETE FROM posts WHERE id = :postId")
    suspend fun deletePost(postId: String)
}

@Dao
interface FriendDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFriend(user: UserDto)

    @Query("SELECT * FROM users")
    suspend fun getAllFriends(): List<UserDto>

    // select specific friend by id
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getFriendById(userId: String): UserDto?

    @Query("DELETE FROM users")
    suspend fun deleteAllFriends()

    // delete item
    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteFriend(userId: String)
}


@Dao
interface CommentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommentContent(commentContent: CommentContentEntity)

    @Query("SELECT * FROM post_comments WHERE postId = :postId")
    suspend fun getCommentContentByPostId(postId: String): CommentContentEntity?

    @Query("DELETE FROM post_comments WHERE postId = :postId")
    suspend fun deleteCommentContentByPostId(postId: String)
}