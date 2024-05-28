package com.example.freeimagefeed.base.views

import androidx.viewbinding.ViewBinding
import com.example.freeimagefeed.base.base_classes.BaseModel
import com.example.freeimagefeed.base.base_classes.BaseViewHolder

object ShimmerModel : BaseModel

val ShimmerType = 999;

class ShimmerViewHolder< Vb : ViewBinding>(
    val vb: Vb
) : BaseViewHolder<Vb, ShimmerModel>(vb){
    override fun bindView(model: ShimmerModel, pos: Int) {

    }
}