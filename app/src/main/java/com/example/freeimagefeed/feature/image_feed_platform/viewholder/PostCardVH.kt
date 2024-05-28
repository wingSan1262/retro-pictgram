package com.example.freeimagefeed.feature.image_feed_platform.viewholder

import com.example.freeimagefeed.base.base_classes.BaseViewHolder
import com.example.freeimagefeed.base.extensions.loadImage
import com.example.freeimagefeed.base.extensions.setChips
import com.example.freeimagefeed.data.local_db.CommentContentEntity
import com.example.freeimagefeed.databinding.PostCardViewHolderBinding
import com.example.freeimagefeed.databinding.UserItemCardBinding
import com.example.freeimagefeed.domain.models.PostModel
import com.example.freeimagefeed.domain.models.UserModel

class PostCardVH(
    val vb: PostCardViewHolderBinding,
    val onPostLiked: (PostModel, Boolean) -> Unit = {a,b ->},
    val onCommenting: (CommentContentEntity) -> Unit = {},
    val onChipClicked: (String) -> Unit,
): BaseViewHolder<PostCardViewHolderBinding, PostModel>(
    vb
) {
    var isLiked = false

    fun setLike(isLiked: Boolean, model: PostModel) {
        this.isLiked = isLiked
        vb.tvLikes.text = "${(model.likes + if (isLiked) 1 else 0)} Likes"
        vb.likeIv.setImageResource(
            if(isLiked) {
                com.example.freeimagefeed.R.drawable.ic_fav_fill_red
            } else {
                com.example.freeimagefeed.R.drawable.ic_fav_border
            }
        )
    }
    override fun bindView(model: PostModel, pos: Int) {
        isLiked = model.isLiked
        vb.run {
            userIv.loadImage(model.owner.picture)
            postIv.loadImage(model.image)
            tvUserName.text = "${model.owner.title}. ${model.owner.firstName} ${model.owner.lastName}"
            tvLikes.text = "${(model.likes + if (isLiked) 1 else 0).toString()} Likes"
            tvPostDescription.text = model.text
            tvDate.text = model.publishDate
            tvPostLink.text = "https://www.dummyapi.com"
            chipGroupPost.setChips(model.tags,{
                it
            }){
                onChipClicked(it)
            }
            setLike(isLiked, model)
            likeIv.setOnClickListener {
                setLike(!isLiked, model)
                onPostLiked(model, isLiked)
            }
            commentSectionWidget.setup(
                model.comment.comments
            ){
                val comment = it.copy(postId = model.id)
                onCommenting(comment)
            }
        }
    }
}