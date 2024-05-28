package com.example.freeimagefeed.feature.image_feed_platform.fragment.user

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.freeimagefeed.R
import com.example.freeimagefeed.base.base_classes.BaseFragment
import com.example.freeimagefeed.base.extensions.collectResource
import com.example.freeimagefeed.base.extensions.getContent
import com.example.freeimagefeed.base.extensions.isHaveContent
import com.example.freeimagefeed.base.extensions.onReachBottomList
import com.example.freeimagefeed.base.extensions.setupGrid
import com.example.freeimagefeed.base.extensions.showToast
import com.example.freeimagefeed.databinding.FragmentUserHostBinding
import com.example.freeimagefeed.databinding.FragmentUserListBinding
import com.example.freeimagefeed.feature.image_feed_platform.adapter.UserCardListAdapter
import com.example.freeimagefeed.feature.image_feed_platform.viewmodel.DummyApiViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserListFragment : BaseFragment<FragmentUserListBinding>() {

    val dummyApiVm by activityViewModels<DummyApiViewModel>()

    private val userListControl by lazy {
        dummyApiVm.userListControl
    }

    private val adapter by lazy {
        UserCardListAdapter {
            dummyApiVm.userId = it.id
            dummyApiVm.userPostControl.request.userId = it.id
            findNavController().navigate(
                R.id.action_userListFragment_to_userDetailFragment
            )
        }
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentUserListBinding {
        return FragmentUserListBinding.inflate(
            inflater,
            container,
            false
        )
    }


    override fun onReadyToRender() {
        observeData()
        viewBinding.run {
            swipeRefreshLayout.setOnRefreshListener {
                lifecycleScope.launch {
                    viewBinding.swipeRefreshLayout.isRefreshing = true
                    adapter.clear()
                    adapter.showShimmerLoading()
                    delay(500)
                    userListControl.reset()
                    userListControl.fetchNextPage()
                }
            }
            userRv.setupGrid(adapter, 2) {
                if(dummyApiVm.userListControl.isReachEnd()) return@setupGrid
                lifecycleScope.launch {
                    viewBinding.swipeRefreshLayout.isRefreshing = true
                    adapter.showShimmerLoading()
                    delay(500)
                    userListControl.fetchNextPage()
                }
            }
        }
        if (!userListControl.data.isHaveContent()) {
            lifecycleScope.launch {
                viewBinding.swipeRefreshLayout.isRefreshing = true
                adapter.showShimmerLoading()
                delay(500)
                userListControl.fetchNextPage()
            }
        }
    }

    fun observeData() {
        collectResource(dummyApiVm.userListControl.data) {
            if (it.isLoading) return@collectResource
            if (it.isSuccess) {
                adapter.hideShimmerLoading()
                viewBinding.run {
                    swipeRefreshLayout.isRefreshing = false
                    adapter.addAll(it.data?.data)
                }
                return@collectResource
            }

            if (it.isError) {
                showToast(it.error?.message ?: "Error")
                adapter.hideShimmerLoading()
                viewBinding.swipeRefreshLayout.isRefreshing = false
                return@collectResource
            }
        }
    }
}