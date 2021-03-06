package com.udacity.project4.utils.extension

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

fun CoroutineScope.async(context: CoroutineContext = Dispatchers.Main, block: suspend () -> Unit) {
    launch {
        withContext(context) {
            block()
        }
    }
}