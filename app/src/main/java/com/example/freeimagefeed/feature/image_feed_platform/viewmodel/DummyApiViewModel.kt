package com.example.freeimagefeed.feature.image_feed_platform.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.freeimagefeed.base.base_classes.PaginationControll
import com.example.freeimagefeed.base.base_classes.setupPaginationControl
import com.example.freeimagefeed.base.base_entity.Event
import com.example.freeimagefeed.base.base_entity.ResourceState
import com.example.freeimagefeed.base.extensions.runVmAction
import com.example.freeimagefeed.data.local_db.CommentContentEntity
import com.example.freeimagefeed.data.remote.model.request.PostByTagRequest
import com.example.freeimagefeed.data.remote.model.request.UserListRequest
import com.example.freeimagefeed.data.remote.model.request.UserPostRequest
import com.example.freeimagefeed.domain.models.PostModel
import com.example.freeimagefeed.domain.models.PostPageModel
import com.example.freeimagefeed.domain.models.UserDetailModel
import com.example.freeimagefeed.domain.models.UserModel
import com.example.freeimagefeed.domain.models.UserPageModel
import com.example.freeimagefeed.domain.usecase.CommentPostUseCase
import com.example.freeimagefeed.domain.usecase.GetLikedPostUseCase
import com.example.freeimagefeed.domain.usecase.InsertDeletePostUseCase
import com.example.freeimagefeed.domain.usecase.InsertFriendPostUseCase
import com.example.freeimagefeed.domain.usecase.LatestPostUseCase
import com.example.freeimagefeed.domain.usecase.TaggedPostUseCase
import com.example.freeimagefeed.domain.usecase.UserDetailPageUseCase
import com.example.freeimagefeed.domain.usecase.UserListPageUseCase
import com.example.freeimagefeed.domain.usecase.UserPostUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DummyApiViewModel @Inject constructor(
    val userListPageUseCase: UserListPageUseCase,
    val userPostUseCase: UserPostUseCase,
    val userDetailUseCase: UserDetailPageUseCase,
    val latestPostUseCase: LatestPostUseCase,
    val taggedPostUseCase: TaggedPostUseCase,
    val likedPostUseCase: GetLikedPostUseCase,
    val insertLikedPostUseCase: InsertDeletePostUseCase,
    val insertFriendPostUseCase: InsertFriendPostUseCase,
    val commentPostUseCase: CommentPostUseCase
) : ViewModel() {

    val userDetailData = MutableStateFlow<Event<ResourceState<UserDetailModel>>>(Event(ResourceState(
        isLoading = false,
        isSuccess = false,
        isError = false,
    )))

    init {
        observeUserDetailData()
    }

    private fun observeUserDetailData() {
        viewModelScope.launch {
            userDetailUseCase.currentData.collect {
                userDetailData.value = it
            }
        }
    }

    val userListControl : PaginationControll<UserPageModel, UserListRequest> = setupPaginationControl(
        UserListRequest(0, 20),
        userListPageUseCase.currentData,
        isReachEndOfList = {
            it.data.isEmpty()
        }
    ) {
        userListPageUseCase.run(it)
    }

    val userPostControl : PaginationControll<PostPageModel, UserPostRequest> = setupPaginationControl(
        UserPostRequest("", 0, 20),
        userPostUseCase.currentData,
        isReachEndOfList = {
            it.data.isEmpty()
        }
    ) { userPostUseCase.run(it) }

    val latestPostControl : PaginationControll<PostPageModel, UserListRequest> = setupPaginationControl(
        UserListRequest(0, 20),
        latestPostUseCase.currentData,
        isReachEndOfList = {
            it.data.isEmpty()
        }
    ) { latestPostUseCase.run(it) }

    val taggedPostControl : PaginationControll<PostPageModel, PostByTagRequest> = setupPaginationControl(
        PostByTagRequest("",0, 20),
        taggedPostUseCase.currentData,
        isReachEndOfList = {
            it.data.isEmpty()
        }
    ) { taggedPostUseCase.run(it) }

    fun toggleFriend (
        getNewStatus : (Boolean) -> Boolean = { it }
    ) {
        viewModelScope.launch {
            var orginalStatus = false
            var status = false
            userDetailData.value.run {
                orginalStatus = this.mContent?.data?.isFriend ?: false
                status = getNewStatus(orginalStatus)
                if(this.mContent?.data?.id == null){
                    return@launch
                }
                if(!status){
                    insertFriendPostUseCase.deleteFriend(
                        UserModel(id = this.mContent.data.id)
                    )
                } else {
                    insertFriendPostUseCase.insertFriend(
                        UserModel(id = this.mContent.data.id)
                    )
                }
            }
            userDetailData.value = Event(userDetailData.value.mContent?.copy(
                data = userDetailData.value.mContent?.data?.copy(
                    isFriend = status
                )
            ))
        }

    }

    var userId = ""
    fun getUserDetail() {
        runVmAction { userDetailUseCase.run(userId) }
    }

    val likedPostData = likedPostUseCase.currentData
    fun getLikedPost() {
        runVmAction { likedPostUseCase.run(Unit) }
    }

    val insertDeleteLikedPostData = insertLikedPostUseCase.currentData
    fun insertLikedPost(post: PostModel) {
        runVmAction { insertLikedPostUseCase.insertPost(post) }
    }
    fun deleteLikedPost(post: PostModel) {
        runVmAction { insertLikedPostUseCase.deletePost(post) }
    }

    fun insertComment(comment: CommentContentEntity) {
        runVmAction { commentPostUseCase.insertComment(comment) }
    }

}