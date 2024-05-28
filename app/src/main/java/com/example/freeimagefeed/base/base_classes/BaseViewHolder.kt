package com.example.freeimagefeed.base.base_classes

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseViewHolder<VB : ViewBinding, Model : BaseModel>(
    val viewBinding: VB
) : RecyclerView.ViewHolder(viewBinding.root) {
    abstract fun bindView(model: Model, pos: Int)
}