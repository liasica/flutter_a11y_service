package com.liasica.a11y_service

import android.graphics.Rect
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

data class NodeData(
  var depth: List<Int>,
  var info: AccessibilityNodeInfo,
  var bounds: Rect? = null,
) {
  override fun toString() =
    "$depth : ${info.packageName} / ${info.className} { ${info.viewIdResourceName} | ${info.text} | ${info.contentDescription} | $bounds | ${info.isClickable} | ${info.isScrollable} | ${info.isEditable} }"

  fun toMap() = mapOf(
    "depth" to depth,
    "bounds" to bounds?.let {
      listOf(it.left, it.top, it.right, it.bottom)
    },
    "id" to info.viewIdResourceName.nullableString(),
    "text" to info.text.nullableString(),
    "className" to info.className.nullableString(),
    "packageName" to info.packageName.nullableString(),
    "description" to info.contentDescription.nullableString(),
    "clickable" to info.isClickable,
    "scrollable" to info.isScrollable,
    "editable" to info.isEditable,
  )
}

data class AnalyzedResult(
  val current: AccessibilityEvent,
  val nodes: ArrayList<NodeData> = arrayListOf()
)
