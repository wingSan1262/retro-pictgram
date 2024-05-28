package com.example.freeimagefeed.base.base_classes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.freeimagefeed.base.base_entity.Event
import com.example.freeimagefeed.base.base_entity.ResourceState
import com.example.freeimagefeed.data.remote.model.request.UserListRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

interface BaseModel

interface BasePaginationModel : BaseModel {
    var page: Int
    var limit: Int
}


/**
 * val userPageRequest = UserListRequest(1, 20);
 *     var isRequesting = false;
 *     val userPageData = userListPageUseCase.currentData.map {
 *         it.mContent.let { res ->
 *             isRequesting = false
 *             when{
 *                 res?.isSuccess == true -> userPageRequest.page++
 *                 res?.isError == true -> userPageRequest.page--
 *                 else->{}
 *             }
 *         }
 *     }
 *
 *     fun fetchUserPage() : Boolean {
 *         if(!isRequesting){
 *             isRequesting = true
 *             viewModelScope.launch {
 *                 userListPageUseCase.run(userPageRequest)
 *             }
 *             return true
 *         }
 *         return false
 *     }
 */


data class PaginationControll<Content, Request : BasePaginationModel>(
    var request: Request,
    val reset: () -> Unit,
    val fetchNextPage: () -> Boolean,
    val isReachEnd: () -> Boolean,
    val data: StateFlow<Event<ResourceState<Content>>>
)

fun <Content, Request : BasePaginationModel> ViewModel.setupPaginationControl(
    initialRequest: Request,
    data: StateFlow<Event<ResourceState<Content>>>,
    isReachEndOfList: (Content) -> Boolean = { false },
    onRequest: suspend (Request) -> Unit
): PaginationControll<Content, Request> {
    val iteratePageFrom = initialRequest.page;
    val orginialLimit = initialRequest.limit;
    val userPageRequest: Request = initialRequest;
    var isRequesting = false

    var isReachEnd = false
    fun isReachEnd(): Boolean {
        return isReachEnd
    }
    val data: StateFlow<Event<ResourceState<Content>>> = data.map {
        it.mContent.let { res ->
            when {
                res?.isSuccess == true -> {
                    res.data?.let {
                        isReachEnd = isReachEndOfList(it)
                    }
                    userPageRequest.page++
                    isRequesting = false
                }
                res?.isError == true -> {
                    userPageRequest.page--
                    isRequesting = false
                }
                else -> {}
            }
        }
        return@map it
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        Event(
            ResourceState(
                false, false, false
            )
        )
    )

    fun reset() {
        userPageRequest.page = iteratePageFrom
        userPageRequest.limit = orginialLimit
        isRequesting = false
        isReachEnd = false
    }

    fun fetchNextPage(): Boolean {
        if (!isRequesting && !isReachEnd) {
            isRequesting = true
            viewModelScope.launch {
                onRequest(userPageRequest)
            }
            return true
        }
        return false
    }
    return PaginationControll(
        userPageRequest, ::reset, ::fetchNextPage, ::isReachEnd, data
    )
}