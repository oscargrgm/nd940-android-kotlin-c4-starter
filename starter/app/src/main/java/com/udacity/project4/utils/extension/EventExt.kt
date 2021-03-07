package com.udacity.project4.utils.extension

import com.udacity.project4.utils.Event

fun <T> T.toEvent(): Event<T> = Event(this)