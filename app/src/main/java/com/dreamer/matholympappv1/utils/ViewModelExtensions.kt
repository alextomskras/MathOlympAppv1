package com.dreamer.matholympappv1.utils

import androidx.lifecycle.LiveData

class ViewModelExtensions {
    fun <T> LiveData<T>.requireValue(): T {
        return this.value ?: throw IllegalStateException("Value is empty")
    }
}