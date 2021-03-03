package com.udacity.project4.utils.extension

import android.app.Activity
import android.content.Intent
import android.os.Bundle

inline fun <reified T : Activity> Activity.launchActivityAndFinish(extras: Bundle? = null) {
    val intent = Intent(this, T::class.java).apply {
        extras?.let { putExtras(it) }
    }
    startActivity(intent)
    finish()
}