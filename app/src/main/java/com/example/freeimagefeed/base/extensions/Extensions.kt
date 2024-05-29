
package com.example.freeimagefeed.base.extensions

import android.app.Activity
import android.graphics.Typeface
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.StyleSpan
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.freeimagefeed.R
import com.example.freeimagefeed.base.base_entity.Event
import com.example.freeimagefeed.base.base_entity.ResourceState
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

fun ImageView.loadImage(url: String) {
    Glide.with(this)
        .load(url)
        .error(R.drawable.broken_place_holder) // replace with your placeholder image resource
        .into(this)
}

fun <Data> Fragment.collectResource(
    data : StateFlow<Event<ResourceState<Data>>>,
    onNewData : suspend (ResourceState<Data>) -> Unit
){
    lifecycleScope.launch {
        data.collect{
            it.mContent?.let {
                launch(Dispatchers.Main){
                    onNewData(it)
                }
            }
        }
    }
}

fun <Data> Fragment.collectEvent(
    data : StateFlow<Event<ResourceState<Data>>>,
    onNewData : (ResourceState<Data>) -> Unit
){
    lifecycleScope.launch {
        data.collect{
            it.contentIfNotHandled?.let {
                onNewData(it)
            }
        }
    }
}

fun <T> ChipGroup.setChips(
    items: List<T>,
    mapToText: (T) -> String,
    isCloseIcon: Boolean = false,
    onChipClicked: (T) -> Unit,
) {
    this.removeAllViews()
    for (item in items) {
        val chip = Chip(this.context).apply {
            text = mapToText(item)
            isCloseIconVisible = isCloseIcon
            setOnClickListener { onChipClicked(item) }
        }
        this.addView(chip)
    }
}


fun RecyclerView.setupList(
    listAdapter: RecyclerView.Adapter<*>,
    onReachBottomGrid: () -> Unit
){
    layoutManager = LinearLayoutManager(this.context)
    adapter = listAdapter
    onReachBottomList {
        onReachBottomGrid()
    }
}

fun RecyclerView.setupGrid(
    listAdapter: RecyclerView.Adapter<*>,
    spanCount: Int = 2,
    onReachBottomGrid: () -> Unit
){
    layoutManager = GridLayoutManager(this.context, spanCount)
    adapter = listAdapter
    onReachBottomGrid {
        onReachBottomGrid()
    }
}

fun RecyclerView.onReachBottomGrid(action: () -> Unit) {
    var wasLastItemVisible = false

    this.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as GridLayoutManager
            val totalItemCount = layoutManager.itemCount
            val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

            if (totalItemCount - 1 == lastVisibleItem) {
                if (!wasLastItemVisible) {
                    // The last item just became visible
                    action()
                }
                wasLastItemVisible = true
            } else {
                // The last item is not visible
                wasLastItemVisible = false
            }
        }
    })
}

fun RecyclerView.onReachBottomList(action: () -> Unit) {

    var wasLastItemVisible = false
    this.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val totalItemCount = layoutManager.itemCount
            val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

            if (totalItemCount - 1 == lastVisibleItem) {
                if (!wasLastItemVisible) {
                    // The last item just became visible
                    action()
                }
                wasLastItemVisible = true
            } else {
                // The last item is not visible
                wasLastItemVisible = false
            }
        }
    })
}

fun ViewModel.runVmAction(action: suspend () -> Unit){
    viewModelScope.launch {
        action()
    }
}

fun <Data> StateFlow<Event<ResourceState<Data>>>.getContent(): Data? {

    return this.value.mContent?.data
}

fun <Data> StateFlow<Event<ResourceState<Data>>>.isHaveContent(): Boolean {
    return this.getContent() != null
}

fun Activity.showToast(
    message: String
){
    makeText(this, message, LENGTH_SHORT).show()
}

fun Fragment.showToast(
    message: String
){
    requireActivity().showToast(message)
}

fun TextView.setSpannableText(text: String, spanText: String) {
    val spannable = SpannableString(text)
    val start = text.indexOf(spanText)
    if (start != -1) {
        spannable.setSpan(
            StyleSpan(Typeface.BOLD),
            start,
            start + spanText.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    this.setText(spannable)
}

fun EditText.textChanges(): Flow<CharSequence?> {
    return callbackFlow<CharSequence?> {
        val listener = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = Unit
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) =
                Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                trySend(s).isSuccess        //TODO: Replace offer with trySend when we update kotlin coroutine version
            }
        }
        addTextChangedListener(listener)
        awaitClose { removeTextChangedListener(listener) }
    }.onStart { emit(text) }
}

fun RecyclerView.scrollToEnd() {
    val adapter = this.adapter
    if (adapter != null && adapter.itemCount > 0) {
        this.smoothScrollToPosition(adapter.itemCount - 1)
    }
}

