package com.example.freeimagefeed.feature.image_feed_platform.viewholder

import com.example.freeimagefeed.base.base_classes.BaseViewHolder
import com.example.freeimagefeed.base.extensions.loadImage
import com.example.freeimagefeed.databinding.UserItemCardBinding
import com.example.freeimagefeed.domain.models.UserModel

class UserCardVH(
    val vb: UserItemCardBinding
): BaseViewHolder<UserItemCardBinding, UserModel>(
    vb
) {
    override fun bindView(model: UserModel, pos: Int) {
        vb.userName.text = "${model.title}. ${model.firstName} ${model.lastName}"
        vb.userIv.loadImage(model.picture)
        vb.descTv.text = model.id

        vb.friendTv.visibility = if(model.isFriend) {
            android.view.View.VISIBLE
        } else {
            android.view.View.GONE
        }
    }
}