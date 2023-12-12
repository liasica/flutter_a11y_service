package com.liasica.a11y_service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.graphics.Rect
import android.os.Bundle
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import java.util.SortedMap

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
    val event: AccessibilityEvent? = null,
    // val nodes: ArrayList<NodeData> = arrayListOf()
    val nodes: SortedMap<String, NodeData> = sortedMapOf(),
) {
    fun toMap() = mapOf(
        "event" to mapOf(
            "packageName" to event?.packageName.nullableString(),
            "className" to event?.className.nullableString(),
            "text" to event?.text?.joinToString(separator = " ~~ "),
            "description" to event?.contentDescription.nullableString(),
        ),
        "nodes" to nodes.mapValues { it.value.toMap() },
    )
}

/**
 * 结点操作快速调用
 * */
// 结点点击，现在很多APP屏蔽了结点点击，默认采用手势模拟
fun NodeData?.click(gestureClick: Boolean = false, duration: Long = 200L): Boolean {
    if (this == null || !A11yService.isGranted) return false
    if (gestureClick) {
        bounds?.let {
            val x = ((it.left + it.right) / 2).toFloat()
            val y = ((it.top + it.bottom) / 2).toFloat()
            return A11yService.instance!!.dispatchGesture(
                GestureDescription.Builder().apply {
                    addStroke(GestureDescription.StrokeDescription(Path().apply { moveTo(x, y) }, 0L, duration))
                }.build(), object : AccessibilityService.GestureResultCallback() {
                    override fun onCompleted(gestureDescription: GestureDescription?) {
                        super.onCompleted(gestureDescription)
                        // 手势执行完成回调
                    }
                }, null
            )
        }
    }

    info.let {
        var depthCount = 0  // 查找最大深度
        var tempNode = it
        while (depthCount < 10) {
            if (tempNode.isClickable) {
                return if (duration >= 1000L) {
                    tempNode.performAction(AccessibilityNodeInfo.ACTION_LONG_CLICK)
                } else {
                    tempNode.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                }
            } else {
                tempNode = tempNode.parent
                depthCount++
            }
        }
    }

    return false
}

// 结点长按
fun NodeData?.longClick(gestureClick: Boolean = false, duration: Long = 1000L): Boolean {
    return click(gestureClick, duration)
}

// 向前滑动
fun NodeData?.scrollForward(isForward: Boolean = true): Boolean {
    if (this == null || !A11yService.isGranted) return false
    info.let {
        var depthCount = 0  // 查找最大深度
        var tempNode = it
        while (depthCount < 10) {
            if (tempNode.isScrollable) {
                return tempNode.performAction(
                    if (isForward) AccessibilityNodeInfo.ACTION_SCROLL_FORWARD
                    else AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD
                )
            } else {
                tempNode = tempNode.parent
                depthCount++
            }
        }
    }
    return false
}

// 向后滑动
fun NodeData?.backward(): Boolean = scrollForward(false)

// 从一个坐标点滑动到另一个坐标点
fun NodeData?.swipe(
    startX: Int,
    startY: Int,
    endX: Int,
    endY: Int,
    duration: Long = 1000L
): Boolean {
    if (!A11yService.isGranted) return false
    return A11yService.instance!!.dispatchGesture(
        GestureDescription.Builder().apply {
            addStroke(GestureDescription.StrokeDescription(Path().apply {
                moveTo(startX.toFloat(), startY.toFloat())
                lineTo(endX.toFloat(), endY.toFloat())
            }, 0L, duration))
        }.build(), object : AccessibilityService.GestureResultCallback() {
            override fun onCompleted(gestureDescription: GestureDescription?) {
                super.onCompleted(gestureDescription)
                // 手势执行完成回调
            }
        }, null
    )
}

// 列表滑动一屏


// 文本填充
fun NodeData?.input(content: String): Boolean {
    if (this == null) return false
    info.let {
        var depthCount = 0  // 查找最大深度
        var tempNode = it
        while (depthCount < 10) {
            if (tempNode.isEditable) {
                return tempNode.performAction(
                    AccessibilityNodeInfo.ACTION_SET_TEXT, Bundle().apply {
                        putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, content)
                    }
                )
            } else {
                tempNode = tempNode.parent
                depthCount++
            }
        }
    }
    return false
}


enum class TextMatchType(var value: Int) {
    EQUALS(1),
    CONTAINS(2),
    REGEX(3);

    companion object {
        infix fun from(value: Int?): TextMatchType = TextMatchType.values().firstOrNull { it.value == (value ?: 0) } ?: EQUALS
    }
}

/**
 * Find node by viewIdResourceName
 *
 * @param id [String] node's [AccessibilityNodeInfo.getViewIdResourceName]
 * @param match [TextMatchType] matches type, default is [TextMatchType.EQUALS]
 * */
fun AnalyzedResult.findNodeById(id: String, match: TextMatchType = TextMatchType.EQUALS): NodeData? {
    val regex = Regex(id)
    nodes.forEach { (_, node) ->
        val nodeId = node.info.viewIdResourceName.nullableString()
        when (match) {
            TextMatchType.EQUALS -> {
                if (nodeId == id) return node
            }

            TextMatchType.CONTAINS -> {
                if (nodeId.contains(id)) return node
            }

            TextMatchType.REGEX -> {
                if (regex.matches(nodeId)) return node
            }
        }
    }
    return null
}

/**
 * Find nodes by viewIdResourceName
 *
 * @param id [String] node's [AccessibilityNodeInfo.getViewIdResourceName]
 * @param match [TextMatchType] matches type, default is [TextMatchType.EQUALS]
 * */
fun AnalyzedResult.findNodesById(id: String, match: TextMatchType = TextMatchType.EQUALS): AnalyzedResult {
    val result = AnalyzedResult()
    val regex = Regex(id)

    nodes.forEach { (treeId, node) ->
        val nodeId = node.info.viewIdResourceName.nullableString()
        if (
            when (match) {
                TextMatchType.EQUALS -> nodeId == id
                TextMatchType.CONTAINS -> nodeId.contains(id)
                TextMatchType.REGEX -> regex.matches(nodeId)
            }
        ) {
            result.nodes[treeId] = node
        }
    }
    return result
}

typealias NodeExpression = (NodeData) -> Boolean

/**
 * Find single node based on the expression results passed in
 *
 * @param expression [NodeExpression]
 * */
fun AnalyzedResult.findNodeByExpression(expression: NodeExpression): NodeData? {
    nodes.forEach { (_, node) ->
        if (expression.invoke(node)) return node
    }
    return null
}

/**
 * Find nodes based on the expression results passed in
 *
 * @param expression [NodeExpression]
 * */
fun AnalyzedResult.findNodesByExpression(expression: NodeExpression): AnalyzedResult {
    val result = AnalyzedResult()
    nodes.forEach { (treeId, node) ->
        if (expression.invoke(node)) result.nodes[treeId] = node
    }
    return result
}

/**
 * Find nodes if text or desc is not null or blank
 *
 * @param includeDesc [Boolean] include desc or not
 * */
fun AnalyzedResult.findAllTextNode(includeDesc: Boolean = false): AnalyzedResult {
    val result = AnalyzedResult()
    nodes.forEach { (treeId, node) ->
        when {
            !node.info.text.isNullOrBlank() -> result.nodes[treeId] = node
            includeDesc && !node.info.contentDescription.isNullOrBlank() -> result.nodes[treeId] = node
        }
    }
    return result
}

/**
 * Find single node by text
 *
 * @param text [String] to be found
 * @param includeDesc matches desc or not
 * @param match [TextMatchType] matches type, default is [TextMatchType.EQUALS]
 * */
fun AnalyzedResult.findNodeByText(text: String, includeDesc: Boolean = true, match: TextMatchType = TextMatchType.EQUALS): NodeData? {
    val regex = Regex(text)

    nodes.forEach { (_, node) ->
        val nodeText = node.info.text.nullableString()
        val desc = node.info.contentDescription.nullableString()

        if (
            when (match) {
                TextMatchType.EQUALS -> nodeText == text || (includeDesc && desc == text)
                TextMatchType.CONTAINS -> nodeText.contains(text) || (includeDesc && desc.contains(text))
                TextMatchType.REGEX -> regex.matches(nodeText) || (includeDesc && regex.matches(desc))
            }
        ) {
            return node
        }
    }
    return null
}

/**
 * Find all nodes by text
 *
 * @param text [String] to be found
 * @param includeDesc matches desc or not
 * @param match [TextMatchType] matches type, default is [TextMatchType.EQUALS]
 * */
fun AnalyzedResult.findNodesByText(text: String, includeDesc: Boolean = true, match: TextMatchType = TextMatchType.EQUALS): AnalyzedResult {
    val result = AnalyzedResult()
    val regex = Regex(text)

    nodes.forEach { (treeId, node) ->
        val nodeText = node.info.text.nullableString()
        val desc = node.info.contentDescription.nullableString()

        if (
            when (match) {
                TextMatchType.EQUALS -> nodeText == text || (includeDesc && desc == text)
                TextMatchType.CONTAINS -> nodeText.contains(text) || (includeDesc && desc.contains(text))
                TextMatchType.REGEX -> regex.matches(nodeText) || (includeDesc && regex.matches(desc))
            }
        ) {
            result.nodes[treeId] = node
        }
    }
    return result
}
