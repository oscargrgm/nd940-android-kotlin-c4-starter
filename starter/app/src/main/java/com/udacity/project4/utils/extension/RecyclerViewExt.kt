package com.udacity.project4.utils.extension

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.udacity.project4.base.BaseRecyclerViewAdapter

/**
 * Extension function to setup the RecyclerView
 */
fun <T> RecyclerView.setup(adapter: BaseRecyclerViewAdapter<T>) {
    this.apply {
        layoutManager = LinearLayoutManager(this.context)
        this.adapter = adapter
    }
}