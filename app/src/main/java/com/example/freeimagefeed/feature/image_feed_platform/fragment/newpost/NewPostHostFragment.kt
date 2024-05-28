package com.example.freeimagefeed.feature.image_feed_platform.fragment.newpost

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
class NewPostHostFragment : BaseFragment<FragmentNewPostHostBinding>() {

    val viewModel: DummyApiViewModel by activityViewModels()

    val adapterPost = UserPostAdapter(
        ::setChip, onItemLiked = ::onItemLiked,
        onCommenting = ::onCommenting
    )


    fun onCommenting(comment: CommentContentEntity) {
        adapterPost.modifyOneItem({
            it.id == comment.postId
        }){
            it.copy(comment = comment)
        }
        viewModel.insertComment(comment)
    }

    fun onItemLiked(model: PostModel, liked: Boolean) {
        if (liked) {
            viewModel.insertLikedPost(model)
            adapterPost.modifyItem(model) {
                it.copy(isLiked = true)
            }
        } else {
            viewModel.deleteLikedPost(model)
            adapterPost.modifyItem(model) {
                it.copy(isLiked = false)
            }
        }
    }

    fun setChip(it: String) {
        viewModel.taggedPostControl.request.tag = it
        viewBinding.chipGroup.setChips(
            listOf(it),
            { it },
            true,
        ) {
            viewBinding.swipeRefreshLayout.isRefreshing = true
            viewBinding.chipGroup.setChips(listOf<String>(), { it }) {}
            resetUiVmAndRefetch()
        }
        viewBinding.swipeRefreshLayout.isRefreshing = true
        resetUiVmAndRefetchWithTag()
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentNewPostHostBinding {
        return FragmentNewPostHostBinding.inflate(inflater, container, false)
    }

    override fun onReadyToRender() {
        observeData()
        if (!viewModel.latestPostControl.data.isHaveContent()) {
            lifecycleScope.launch {
                adapterPost.showShimmerLoading()
                delay(500)
                resetUiVmAndRefetch()
            }
        }
        setupPostRv()
    }


    private fun setupPostRv() {
        viewBinding.swipeRefreshLayout.setOnRefreshListener {
            if (viewBinding.chipGroup.childCount > 0) {
                resetUiVmAndRefetchWithTag()
            } else {
                resetUiVmAndRefetch()
            }
        }
        viewBinding.recyclerView.setupList(
            adapterPost
        ) {
            lifecycleScope.launch {
                if (viewBinding.chipGroup.childCount > 0) {
                    if (viewModel.taggedPostControl.isReachEnd())
                        return@launch
                    adapterPost.showShimmerLoading()
                    delay(500)
                    viewModel.taggedPostControl.fetchNextPage()
                } else {
                    if (viewModel.latestPostControl.isReachEnd())
                        return@launch
                    adapterPost.showShimmerLoading()
                    delay(500)
                    viewModel.latestPostControl.fetchNextPage()
                }
            }
        }
    }

    private fun resetUiVmAndRefetchWithTag() {
        lifecycleScope.launch {
            adapterPost.clear()
            viewModel.taggedPostControl.reset()
            adapterPost.showShimmerLoading()
            delay(500)
            viewModel.taggedPostControl.fetchNextPage()
        }
    }

    private fun resetUiVmAndRefetch() {
        lifecycleScope.launch {
            adapterPost.clear()
            viewModel.latestPostControl.reset()
            adapterPost.showShimmerLoading()
            delay(500)
            viewModel.latestPostControl.fetchNextPage()
        }
    }

    private fun observeData() {
        collectResource(viewModel.latestPostControl.data) {
            viewBinding.swipeRefreshLayout.isRefreshing = false
            if (it.isSuccess) {
                adapterPost.hideShimmerLoading()
                adapterPost.addAll(it.data?.data)
            }
            if (it.isError) {
                adapterPost.hideShimmerLoading()
                showToast("Error: " + it.error?.message.toString())
            }
        }

        collectResource(viewModel.taggedPostControl.data) {
            viewBinding.swipeRefreshLayout.isRefreshing = false
            if (it.isSuccess) {
                adapterPost.hideShimmerLoading()
                adapterPost.addAll(it.data?.data)
            }
            if (it.isError) {
                adapterPost.hideShimmerLoading()
                showToast("Error: " + it.error?.message.toString())
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            NewPostHostFragment()
    }
}