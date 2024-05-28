package com.example.freeimagefeed.feature.image_feed_platform.fragment.liked_post

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.freeimagefeed.R
import com.example.freeimagefeed.base.base_classes.BaseFragment
import com.example.freeimagefeed.base.extensions.collectResource
import com.example.freeimagefeed.base.extensions.isHaveContent
import com.example.freeimagefeed.base.extensions.setChips
import com.example.freeimagefeed.base.extensions.setupList
import com.example.freeimagefeed.base.extensions.showToast
import com.example.freeimagefeed.data.local_db.CommentContentEntity
import com.example.freeimagefeed.databinding.FragmentNewPostHostBinding
import com.example.freeimagefeed.domain.models.PostModel
import com.example.freeimagefeed.feature.image_feed_platform.adapter.UserPostAdapter
import com.example.freeimagefeed.feature.image_feed_platform.viewmodel.DummyApiViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LikedPostHostFragment : BaseFragment<FragmentNewPostHostBinding>() {

    val viewModel: DummyApiViewModel by activityViewModels()

    val adapterPost = UserPostAdapter(
        onItemLiked = ::onItemLiked,
        onCommenting = ::onCommenting
    ){

    }



    fun onCommenting(comment: CommentContentEntity) {
        adapterPost.modifyOneItem({
            it.id == comment.postId
        }){
            it.copy(comment = comment)
        }
        viewModel.insertComment(comment)
    }

    fun onItemLiked(model: PostModel, liked: Boolean) {
        if (!liked){
            viewModel.deleteLikedPost(model)
            adapterPost.removeItem(model)
        }
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentNewPostHostBinding {
        return FragmentNewPostHostBinding.inflate(inflater, container, false)
    }

    override fun onReadyToRender() {
        observeData()
        setupPostRv()
        if(!viewModel.likedPostData.isHaveContent()) {
            lifecycleScope.launch {
                resetUiVmAndRefetch()
            }
        }
    }


    private fun setupPostRv() {
        viewBinding.swipeRefreshLayout.setOnRefreshListener {
            resetUiVmAndRefetch()
        }
        viewBinding.recyclerView.setupList(
            adapterPost
        ){}

        viewBinding.titleTv.text = "Liked Post"
    }
    private fun resetUiVmAndRefetch() {
        lifecycleScope.launch {
            adapterPost.clear()
            adapterPost.showShimmerLoading()
            delay(1000)
            viewModel.getLikedPost()
        }
    }

    private fun observeData() {
        collectResource(viewModel.likedPostData) {
            viewBinding.swipeRefreshLayout.isRefreshing = false
            if(it.isSuccess) {
                adapterPost.hideShimmerLoading()
                adapterPost.addAll(it.data)
            }
            if(it.isError){
                adapterPost.hideShimmerLoading()
                showToast("Error: " + it.error?.message.toString())
            }
        }
    }
}