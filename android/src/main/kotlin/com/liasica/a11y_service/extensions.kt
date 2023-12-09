package com.liasica.a11y_service

import android.content.Context
import android.content.Intent
import android.provider.Settings


fun CharSequence?.nullableString() = if (this.isNullOrBlank()) "" else this.toString()

fun String?.nullableString() = if (this.isNullOrBlank()) "" else this

fun Context.requestPermission() {
  startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
  })
}
