package com.example.freeimagefeed.base.base_classes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.example.freeimagefeed.R

abstract class BaseFragment<Binding: ViewBinding> : Fragment() {

    lateinit var viewBinding : Binding

    abstract fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) : Binding

    abstract fun onReadyToRender()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = getViewBinding(inflater, container)
        onReadyToRender()
        return viewBinding.root
    }

}