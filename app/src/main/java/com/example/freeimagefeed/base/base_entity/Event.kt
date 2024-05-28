package com.example.freeimagefeed.base.base_entity

class Event<T>(content: T?) {
    val mContent: T?
    private var hasBeenHandled = false

    val contentIfNotHandled: T?
        get() = if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            mContent
        }

    fun hasBeenHandled(): Boolean {
        return hasBeenHandled
    }

    init {
        mContent = content
    }
}