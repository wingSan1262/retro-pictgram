package com.example.freeimagefeed.feature.image_feed_platform.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.freeimagefeed.base.base_classes.BaseRvAdapter
import com.example.freeimagefeed.base.views.ShimmerViewHolder
import com.example.freeimagefeed.data.local_db.CommentContentEntity
import com.example.freeimagefeed.databinding.PostCardViewHolderBinding
import com.example.freeimagefeed.databinding.UserItemCardBinding
import com.example.freeimagefeed.databinding.UserItemCardShimmerBinding
import com.example.freeimagefeed.domain.models.PostModel
import com.example.freeimagefeed.domain.models.PostPageModel
import com.example.freeimagefeed.domain.models.UserModel
import com.example.freeimagefeed.feature.image_feed_platform.viewholder.PostCardVH
import com.example.freeimagefeed.feature.image_feed_platform.viewholder.UserCardVH

class UserPostAdapter(
    val onChipClick: (String) -> Unit = {},
    val onCommenting: (CommentContentEntity) -> Unit = {},
    val onItemLiked: (PostModel, Boolean) -> Unit = {a,b ->},
    val onItemClick: (PostModel) -> Unit = {},
): BaseRvAdapter<PostCardVH, PostModel>(3) {
    override fun getShimmerViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        return ShimmerViewHolder(
            UserItemCardShimmerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getViewHolder(parent: ViewGroup, viewType: Int): PostCardVH {
        return PostCardVH(
            PostCardViewHolderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),{ model, isLiked ->
                onItemLiked(model, isLiked)
            },
            onCommenting = onCommenting
        ){
            onChipClick(it)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is PostCardVH) {
            holder.bindView(items[position], position)
            holder.viewBinding.root.setOnClickListener {
                onItemClick(items[position])
            }
        }
    }
}