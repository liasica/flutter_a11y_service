package com.liasica.a11y_service

import android.graphics.Rect
import android.view.accessibility.AccessibilityNodeInfo
import java.util.UUID

data class EventData(
  var packageName: String,
  var className: String,
  var eventType: Int
) {
  override fun toString() = "$packageName / $className : $eventType"
}

data class NodeData(
  var uuid: UUID = UUID.randomUUID(),
  var packageName: String,
  var className: String,
  var eventType: Int,
  var text: String? = null,
  var id: String? = null,
  var bounds: Rect? = null,
  var description: String? = null,
  var clickable: Boolean = false,
  var scrollable: Boolean = false,
  var editable: Boolean = false,
  var nodeInfo: AccessibilityNodeInfo? = null
) {
  override fun toString() = "$packageName / $className : $eventType { $className → $text → $id → $description → $bounds → $clickable → $scrollable → $editable }"
}

data class AnalyzedResult(
  val current: EventData? = null,
  val nodes: ArrayList<NodeData> = arrayListOf()
)
