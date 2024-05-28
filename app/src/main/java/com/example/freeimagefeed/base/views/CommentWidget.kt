package com.example.freeimagefeed.base.views

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.EditText
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.freeimagefeed.R
import com.example.freeimagefeed.base.base_classes.BaseModel
import com.example.freeimagefeed.base.base_classes.BaseRvAdapter
import com.example.freeimagefeed.base.base_classes.BaseViewHolder
import com.example.freeimagefeed.base.extensions.setupList
import com.example.freeimagefeed.data.local_db.CommentContentEntity
import com.example.freeimagefeed.data.local_db.CommentEntity
import com.example.freeimagefeed.databinding.CommentItemVhBinding
import com.example.freeimagefeed.databinding.CommentSectionWidgetBinding


class CommentViewHolder(
    val vb: CommentItemVhBinding
) : BaseViewHolder<CommentItemVhBinding, CommentEntity>(vb) {
    override fun bindView(model: CommentEntity, pos: Int) {
        vb.userNameTv.text = model.userName
        vb.commentTv.text = model.content
    }
}

class CommentAdapter(
) : BaseRvAdapter<CommentViewHolder, CommentEntity>() {
    override fun getShimmerViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        return null
    }

    override fun getViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        return CommentViewHolder(
            CommentItemVhBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? CommentViewHolder)?.bindView(items[position], position)
    }


}

@SuppressLint("ClickableViewAccessibility")
class CommentSectionWidget(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    var vb: CommentSectionWidgetBinding =
        CommentSectionWidgetBinding.inflate(LayoutInflater.from(context), this, true)
    val adapter = CommentAdapter()

    var onComment = { comment: CommentContentEntity -> }

    var isCollapsed = true
    fun updateArrowCollapse(){
        vb.collapseExpandIv.setImageResource(
            if (isCollapsed) R.drawable.arrow_triange_down
            else R.drawable.arrow_triangle_up
        )
    }
    init {
        vb.commentRecyclerView.setupList(adapter) {

        }
        vb.commentRecyclerView.setOnTouchListener { v, event ->
            v.parent.requestDisallowInterceptTouchEvent(true)
            v.onTouchEvent(event)
            true
        }
        vb.commentSectionTitle.text = "Comments (${adapter.items.size})"
        vb.addCommentButton.setOnClickListener {
            if (vb.addCommentEditText.text.isNotEmpty() && vb.userNameEt.text.isNotBlank()) {
                vb.commentErrorTv.visibility = GONE
                val comments = CommentEntity(
                    vb.userNameEt.text.toString(),
                    vb.addCommentEditText.text.toString()
                )
                adapter.addAll(
                    listOf(comments)
                )
                vb.addCommentEditText.text.clear()
                vb.userNameEt.text.clear()
                vb.commentSectionTitle.text = "Comments (${adapter.items.size})"

                onComment(
                    CommentContentEntity(
                        comments = adapter.items,
                    )
                )
            } else {
                vb.commentErrorTv.visibility = VISIBLE
            }
        }
        updateArrowCollapse()
        vb.collapseExpandIv.setOnClickListener {
            isCollapsed = !isCollapsed
            updateArrowCollapse()
            vb.commentRecyclerView.visibility = if (isCollapsed) GONE else VISIBLE
            vb.addCommentEditText.visibility = if (isCollapsed) GONE else VISIBLE
            vb.userNameEt.visibility = if (isCollapsed) GONE else VISIBLE
            vb.llCommentButton.visibility = if (isCollapsed) GONE else VISIBLE
        }
    }

    public fun setup(
        comments: List<CommentEntity>,
        onCommentAdded: (CommentContentEntity) -> Unit
    ) {
        onComment = onCommentAdded
        adapter.clear()
        adapter.addAll(comments)
        vb.commentSectionTitle.text = "Comments (${adapter.items.size})"
    }

}