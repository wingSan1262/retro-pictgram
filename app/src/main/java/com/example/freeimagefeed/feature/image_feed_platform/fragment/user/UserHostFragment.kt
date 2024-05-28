package com.example.freeimagefeed.feature.image_feed_platform.fragment.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.freeimagefeed.base.base_classes.BaseFragment
import com.example.freeimagefeed.databinding.FragmentUserHostBinding
import com.example.freeimagefeed.feature.image_feed_platform.viewmodel.DummyApiViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserHostFragment : BaseFragment<FragmentUserHostBinding>() {

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentUserHostBinding {

        return FragmentUserHostBinding.inflate(
            inflater,
            container,
            false
        )

    }

    override fun onReadyToRender() {

    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UserHostFragment().apply {
                arguments = Bundle()
            }
    }
}