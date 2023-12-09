package com.liasica.a11y_service


fun CharSequence?.nullableString() = if (this.isNullOrBlank()) "" else this.toString()

fun String?.nullableString() = if (this.isNullOrBlank()) "" else this
