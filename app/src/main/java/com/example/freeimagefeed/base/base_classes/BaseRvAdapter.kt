package com.example.freeimagefeed.base.base_classes

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.freeimagefeed.base.views.ShimmerModel
import com.example.freeimagefeed.base.views.ShimmerType

abstract class BaseRvAdapter<VH : RecyclerView.ViewHolder?, Item>(
    val shimmerCount : Int = 1
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items: ArrayList<Item> = ArrayList()
    var filterItems: ArrayList<Item> = ArrayList()
    val originalItems: ArrayList<Item> = ArrayList()

    fun resetFilter() {
        items.clear()
        items.addAll(originalItems)
        query = ""
        notifyDataSetChanged()
    }
    var query = ""
    fun filter(query: String) {
        this.query = query
        filterItems.clear()
        if (query.isEmpty()) {
            filterItems.addAll(originalItems)
        } else {
            for (item in originalItems) {
                val itemString = item.toString()
                if (itemString.contains(query, true)) {
                    filterItems.add(item)
                }
            }
        }
        items.clear()
        items.addAll(filterItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var holder = getShimmerViewHolder(parent, viewType)
        if (viewType == ShimmerType && holder != null) {
            return holder
        }
        holder = getViewHolder(parent, viewType)
        return holder!!
    }

    abstract fun getShimmerViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder?
    abstract fun getViewHolder(parent: ViewGroup, viewType: Int): VH

    override fun getItemViewType(position: Int): Int {
        val item = items[position]
        return if (item is ShimmerModel)
            ShimmerType
        else super.getItemViewType(position)
    }

    val size: Int
        get() = items.size

    @SuppressLint("NotifyDataSetChanged")
    fun removeAll() {
        items.clear()
        notifyDataSetChanged()
    }

    fun addAll(item: List<Item>?) {
        if (item.isNullOrEmpty()) return
        val orginalSize = items.size
        originalItems.addAll(item ?: listOf())
        if(query.isEmpty()){
            items.addAll(item ?: listOf())
            notifyItemInserted(orginalSize)
        } else {
            val originalItems = item ?: listOf()
            val filteredItems = ArrayList<Item>()
            for (item in originalItems) {
                val itemString = item.toString()
                if (itemString.contains(query, true)) {
                    filteredItems.add(item)
                }
            }
            items.addAll(filteredItems)
            filterItems.addAll(filteredItems)
            notifyItemInserted(orginalSize)
        }
    }

    fun replaceAll(item: List<Item>?) {
        val newItem = ArrayList(item ?: listOf())
        if (item.isNullOrEmpty()) return
        originalItems.clear()
        originalItems.addAll(newItem)
        items.clear()
        items.addAll(newItem)
        notifyDataSetChanged()
    }

    fun modifyItem(item: Item, callback: (Item) -> Item) {
        val pos = items.indexOf(item)
        val originalPos = originalItems.indexOf(item)
        val filterPos = filterItems.indexOf(item)

        val modified = callback(item)

        if(pos >= 0){
            items[pos] = modified
            notifyItemChanged(pos)
        }
        if(originalPos >= 0){
            originalItems[originalPos] = modified
        }
        if(filterPos >= 0){
            filterItems[filterPos] = modified
        }
    }

    fun modifyOneItem(checkItem : (Item)->Boolean, callback: (Item) -> Item) {
        for (item in items) {
            if(checkItem(item)){
                val pos = items.indexOf(item)
                val originalPos = originalItems.indexOf(item)
                val filterPos = filterItems.indexOf(item)
                val item = callback(item)

                if(pos >= 0){
                    items[pos] = item
                    notifyItemChanged(pos)
                }

                if(originalPos >= 0){
                    originalItems[originalPos] = item
                }

                if(filterPos >= 0){
                    filterItems[filterPos] = item
                }
                return
            }
        }
    }

    fun removeItem(item: Item) {
        val pos = items.indexOf(item)
        items.remove(item)
        notifyItemRemoved(pos)
    }


    var isShowSimmer = false
    fun showShimmerLoading() {
        if(isShowSimmer) return
        isShowSimmer = true
        for (i in 0 until shimmerCount) {
            items.add(ShimmerModel as Item)
        }
        notifyItemRangeInserted(items.size - shimmerCount, shimmerCount)
    }

    fun hideShimmerLoading() {
        if (!isShowSimmer) return
        isShowSimmer = false
        var shimmerStartPosition = items.size - shimmerCount
        var lastIndex = items.size - 1
        for (i in lastIndex downTo shimmerStartPosition) {
            try {
                if (items[i] is ShimmerModel) {
                    removeItem(items[i])
                }
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    fun clear() {
        items.clear()
        originalItems.clear()
        filterItems.clear()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return items.size
    }
}
