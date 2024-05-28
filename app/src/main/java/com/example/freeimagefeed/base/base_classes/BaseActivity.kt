package com.example.freeimagefeed.base.base_classes

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<Binding: ViewBinding> : AppCompatActivity() {

    lateinit var vb : Binding

    abstract fun getViewBinding() : Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vb = getViewBinding()
        setContentView(vb.root)
    }
}