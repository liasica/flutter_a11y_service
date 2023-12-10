package com.liasica.a11y_service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Context
import android.graphics.Path
import android.graphics.Rect
import android.os.Bundle
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
    val nodes: ArrayList<NodeData> = arrayListOf()
)

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


/**
 * 结点解析结果快速调用
 * */

/**
 * 根据文本查找结点列表
 *
 * @param text 匹配的文本
 * @param textAllMatch 文本全匹配
 * @param includeDesc 同时匹配desc
 * @param descAllMatch desc全匹配
 * @param enableRegular 是否启用正则
 * */
fun AnalyzedResult.findNodesByText(
    text: String,
    textAllMatch: Boolean = false,
    includeDesc: Boolean = false,
    descAllMatch: Boolean = false,
    enableRegular: Boolean = false,
): AnalyzedResult {
    val result = AnalyzedResult()
    if (enableRegular) {
        val regex = Regex(text)
        nodes.forEach { node ->
            if (!node.info.text.isNullOrBlank()) {
                if (regex.find(node.info.text!!) != null) {
                    result.nodes.add(node)
                    return@forEach
                }
            }
            if (includeDesc && !node.info.contentDescription.isNullOrBlank()) {
                if (regex.find(node.info.contentDescription!!) != null) {
                    result.nodes.add(node)
                    return@forEach
                }
            }
        }
    } else {
        nodes.forEach { node ->
            if (!node.info.text.isNullOrBlank()) {
                if (textAllMatch) {
                    if (text == node.info.text) {
                        result.nodes.add(node)
                        return@forEach
                    }
                } else {
                    if (node.info.text!!.contains(text)) {
                        result.nodes.add(node)
                        return@forEach
                    }
                }
            }
            if (includeDesc && !node.info.contentDescription.isNullOrBlank()) {
                if (descAllMatch) {
                    if (text == node.info.contentDescription) {
                        result.nodes.add(node)
                        return@forEach
                    }
                } else {
                    if (node.info.contentDescription!!.contains(text)) {
                        result.nodes.add(node)
                        return@forEach
                    }
                }
            }
        }
    }
    return result
}

/**
 * 根据id查找结点 (模糊匹配)
 *
 * @param id 结点id
 * */
fun AnalyzedResult.findNodeById(id: String): NodeData? {
    nodes.forEach { node ->
        if (!node.info.viewIdResourceName.isNullOrBlank()) {
            if (node.info.viewIdResourceName!!.contains(id)) return node
        }
    }
    return null
}

/**
 * 根据id查找结点列表 (模糊匹配)
 *
 * @param id 结点id
 * */
fun AnalyzedResult.findNodesById(id: String): AnalyzedResult {
    val result = AnalyzedResult()
    nodes.forEach { node ->
        if (!node.info.viewIdResourceName.isNullOrBlank()) {
            if (node.info.viewIdResourceName!!.contains(id)) result.nodes.add(node)
        }
    }
    return result
}

/**
 * 根据传入的表达式结果查找结点
 *
 * @param expression 匹配条件表达式
 * */
fun AnalyzedResult.findNodeByExpression(expression: (NodeData) -> Boolean): NodeData? {
    nodes.forEach { node ->
        if (expression.invoke(node)) return node
    }
    return null
}

/**
 * 根据传入的表达式结果查找结点列表
 *
 * @param expression 匹配条件表达式
 * */
fun AnalyzedResult.findNodesByExpression(expression: (NodeData) -> Boolean): AnalyzedResult {
    val result = AnalyzedResult()
    nodes.forEach { node ->
        if (expression.invoke(node)) result.nodes.add(node)
    }
    return result
}

/**
 * 查找所有文本不为空的结点
 * */
fun AnalyzedResult.findAllTextNode(includeDesc: Boolean = false): AnalyzedResult {
    val result = AnalyzedResult()
    nodes.forEach { node ->
        if (!node.info.text.isNullOrBlank()) {
            result.nodes.add(node)
            return@forEach
        }
        if (includeDesc && !node.info.contentDescription.isNullOrBlank()) {
            result.nodes.add(node)
            return@forEach
        }
    }
    return result
}

/**
 * 根据文本查找结点
 *
 * @param text 匹配的文本
 * @param textAllMatch 文本全匹配
 * @param includeDesc 同时匹配desc
 * @param descAllMatch desc全匹配
 * @param enableRegular 是否启用正则
 * */
fun AnalyzedResult.findNodeByText(
    text: String,
    textAllMatch: Boolean = false,
    includeDesc: Boolean = false,
    descAllMatch: Boolean = false,
    enableRegular: Boolean = false,
): NodeData? {
    if (enableRegular) {
        val regex = Regex(text)
        nodes.forEach { node ->
            if (!node.info.text.isNullOrBlank()) {
                if (regex.find(node.info.text!!) != null) return node
            }
            if (includeDesc && !node.info.contentDescription.isNullOrBlank()) {
                if (regex.find(node.info.contentDescription!!) != null) return node
            }
        }
    } else {
        nodes.forEach { node ->
            if (!node.info.text.isNullOrBlank()) {
                if (textAllMatch) {
                    if (text == node.info.text) return node
                } else {
                    if (node.info.text!!.contains(text)) return node
                }
            }
            if (includeDesc && !node.info.contentDescription.isNullOrBlank()) {
                if (descAllMatch) {
                    if (text == node.info.contentDescription) return node
                } else {
                    if (node.info.contentDescription!!.contains(text)) return node
                }
            }
        }
    }
    return null
}

