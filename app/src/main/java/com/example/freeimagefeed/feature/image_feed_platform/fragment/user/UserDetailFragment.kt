package com.example.freeimagefeed.feature.image_feed_platform.fragment.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.freeimagefeed.R
import com.example.freeimagefeed.base.base_classes.BaseFragment
import com.example.freeimagefeed.base.extensions.collectEvent
import com.example.freeimagefeed.base.extensions.collectResource
import com.example.freeimagefeed.base.extensions.isHaveContent
import com.example.freeimagefeed.base.extensions.loadImage
import com.example.freeimagefeed.base.extensions.setSpannableText
import com.example.freeimagefeed.base.extensions.setupGrid
import com.example.freeimagefeed.base.extensions.setupList
import com.example.freeimagefeed.base.extensions.showToast
import com.example.freeimagefeed.base.extensions.textChanges
import com.example.freeimagefeed.data.local_db.CommentContentEntity
import com.example.freeimagefeed.databinding.FragmentUserDetailBinding
import com.example.freeimagefeed.domain.models.PostModel
import com.example.freeimagefeed.domain.models.UserDetailModel
import com.example.freeimagefeed.domain.models.UserModel
import com.example.freeimagefeed.feature.image_feed_platform.adapter.UserPostAdapter
import com.example.freeimagefeed.feature.image_feed_platform.viewmodel.DummyApiViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserDetailFragment : BaseFragment<FragmentUserDetailBinding>() {

    val viewModel: DummyApiViewModel by activityViewModels()

    val adapterPost = UserPostAdapter(onItemLiked = ::onItemLiked,
        onCommenting = ::onCommenting, onCollapsed = ::onItemCollapsed
    )

    fun onItemCollapsed(comment: CommentContentEntity, collapsed: Boolean) {
        adapterPost.modifyOneItem({ it.id == comment.postId }){
            it.copy(isCollapsed = collapsed)
        }
    }
    fun onCommenting(comment: CommentContentEntity) {
        adapterPost.modifyOneItem({ it.id == comment.postId }){
            it.copy(comment = comment)
        }
        viewModel.insertComment(comment)
    }

    fun onItemLiked(model: PostModel, liked: Boolean) {
        if (liked){
            viewModel.insertLikedPost(model)
            adapterPost.modifyItem(model){
                it.copy(isLiked = true)
            }
        } else {
            viewModel.deleteLikedPost(model)
            adapterPost.modifyItem(model){
                it.copy(isLiked = false)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentUserDetailBinding {
        return FragmentUserDetailBinding.inflate(
            inflater,
            container,
            false
        )
    }

    override fun onReadyToRender() {
        observeData()
        fetchData()
        setupPostRv()
        setupSearchStream()
    }


    private fun setupSearchStream() {
        lifecycleScope.launch {
            viewBinding.searchEditText.textChanges().debounce(500).collect{
                if(it.isNullOrEmpty()){
                    adapterPost.resetFilter()
                } else {
                    adapterPost.filter(it.toString())
                }
            }
        }
    }

    private fun setupPostRv() {
        viewBinding.swipeRefreshLayout.setOnRefreshListener {
            resetUiVmAndRefetch()
        }
        viewBinding.backIv.setOnClickListener {
            findNavController().popBackStack()
        }
        viewBinding.recyclerView.setupList(
            adapterPost
        ){
            lifecycleScope.launch {
                if(viewModel.userPostControl.isReachEnd()){
                    return@launch
                }
                adapterPost.showShimmerLoading()
                delay(500)
                viewModel.userPostControl.fetchNextPage()
            }
        }
        viewBinding.fabScrollUp.setOnClickListener {
            viewBinding.recyclerView.smoothScrollToPosition(0)
        }
    }

    private fun resetUiVmAndRefetch() {
        lifecycleScope.launch {
            adapterPost.clear()
            viewModel.userPostControl.reset()
            adapterPost.showShimmerLoading()
            delay(500)
            viewModel.userPostControl.fetchNextPage()
            viewModel.getUserDetail()
        }
    }

    private fun fetchData() {
        viewModel.getUserDetail()
        adapterPost.showShimmerLoading()
        viewModel.userPostControl.reset()
        viewModel.userPostControl.fetchNextPage()
    }


    private fun showUserData(
        data: UserDetailModel
    ){
        viewBinding.run {
            addFriendIv.setOnClickListener {
                viewModel.toggleFriend{ !it }
            }
            addFriendIv.setImageResource(
                if(data.isFriend) {
                    R.drawable.ic_added_friend
                } else {
                    R.drawable.ic_add_firend
                }
            )
            profileIv.loadImage(data.picture)
            userNameTv.text = "${data.title} ${data.firstName} ${data.lastName}"
            genderTv.setSpannableText(
                "Gender: ${data.gender}","Gender"
            )
            emailTv.setSpannableText(
                "Email: ${data.email}","Email"
            )
            dateBirthTv.setSpannableText(
                "Date of Birth: ${data.dateOfBirth}","Date of Birth"
            )
            registerDateTv.setSpannableText(
                "Register Date: ${data.registerDate}","Register Date"
            )
            addressTv.setSpannableText(
                "Address: ${data.location.street} ${data.location.city} ${data.location.state} ${data.location.country}","Address"
            )
        }
    }


    private fun observeData() {
        collectResource(viewModel.userDetailData) {
            if(it.isSuccess) {
                showUserData(it.data ?: UserDetailModel())
                viewModel.toggleFriend()
            }
            if(it.isError){
                showToast("Error: " + it.error?.message.toString())
            }
        }
        collectEvent(viewModel.userPostControl.data) {
            viewBinding.swipeRefreshLayout.isRefreshing = false

            if(it.isSuccess) {
                adapterPost.hideShimmerLoading()
                adapterPost.addAll(it.data?.data)
            }
            if(it.isError){
                adapterPost.hideShimmerLoading()
                showToast("Error: " + it.error?.message.toString())
            }
        }

        collectEvent(viewModel.insertDeleteLikedPostData) {
            if(it.isError){
                showToast("Error: " + it.error?.message.toString())
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UserDetailFragment()
    }
}