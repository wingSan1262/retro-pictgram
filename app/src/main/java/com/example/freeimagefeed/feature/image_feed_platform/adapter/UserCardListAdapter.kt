package com.example.freeimagefeed.feature.image_feed_platform.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.freeimagefeed.base.base_classes.BaseRvAdapter
import com.example.freeimagefeed.base.views.ShimmerViewHolder
import com.example.freeimagefeed.databinding.UserItemCardBinding
import com.example.freeimagefeed.databinding.UserItemCardShimmerBinding
import com.example.freeimagefeed.domain.models.UserModel
import com.example.freeimagefeed.feature.image_feed_platform.viewholder.UserCardVH

class UserCardListAdapter(
    val onItemClick: (UserModel) -> Unit
): BaseRvAdapter<UserCardVH, UserModel>(6) {
    override fun getShimmerViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        return ShimmerViewHolder(
            UserItemCardShimmerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getViewHolder(parent: ViewGroup, viewType: Int): UserCardVH {
        return UserCardVH(
            UserItemCardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is UserCardVH) {
            holder.bindView(items[position], position)
            holder.viewBinding.root.setOnClickListener {
                onItemClick(items[position])
            }
        }
    }
}