package com.udacity.project4.utils.extension

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View

// animate changing the view visibility
fun View.fadeIn() {
    visibility = View.VISIBLE
    alpha = 0f
    animate().alpha(1f).setListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            alpha = 1f
        }
    })
}

// animate changing the view visibility
fun View.fadeOut() {
    animate().alpha(0f).setListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            alpha = 1f
            visibility = View.GONE
        }
    })
}
