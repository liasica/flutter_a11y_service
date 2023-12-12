package com.liasica.a11y_service

import android.accessibilityservice.AccessibilityService
import android.graphics.Rect
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import io.flutter.Log
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class A11yService : AccessibilityService() {
    private val executor: ExecutorService = Executors.newFixedThreadPool(4)

    // var callback: ((AccessibilityEvent?, AnalyzedResult) -> Unit)? = null

    companion object {
        // Exported Accessibility Service Instance
        var instance: A11yService? = null

        // Is Accessibility Service Granted
        val isGranted: Boolean get() = instance != null

        private var analyzeTreeCallback: ((AccessibilityEvent, AnalyzedResult) -> Unit)? = null

        fun setAnalyzeTreeCallback(callback: ((AccessibilityEvent, AnalyzedResult) -> Unit)?) {
            analyzeTreeCallback = callback
        }
    }

    // Get Root Node, if get error, return null
    override fun getRootInActiveWindow() = try {
        super.getRootInActiveWindow()
    } catch (e: Throwable) {
        e.printStackTrace()
        null
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
    }

    override fun onDestroy() {
        super.onDestroy()

        instance = null
        executor.shutdown()
    }

    override fun onInterrupt() {}

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event?.let {
            val className = it.className.nullableString()
            val packageName = it.packageName.nullableString()
            val eventType = it.eventType
            val text = it.text.joinToString(separator = " ~~ ")
            val description = it.contentDescription.nullableString()

            Log.d(Constants.LOG_TAG, "event: $packageName / $className [ $eventType ] : { text = $text, description = $description }")
            if (className.isNotBlank() && packageName.isNotBlank()) {
                executor.execute {
                    // val start = System.currentTimeMillis()
                    val result = analyze(event)
                    analyzeTreeCallback?.invoke(it, result)
                    A11yServicePlugin.sendEvent(result.toMap())
                    // Log.d(Constants.LOG_TAG, "analyze tree cost ${System.currentTimeMillis() - start}ms")
                }
            }
        }
    }

    fun analyze(event: AccessibilityEvent? = null): AnalyzedResult {
        val result = AnalyzedResult(event = event)
        analyzeTree(rootInActiveWindow, result)
        return result
    }

    private fun analyzeTree(node: AccessibilityNodeInfo?, list: AnalyzedResult, depth: List<Int>? = null) {
        if (node == null) return

        val bounds = Rect()
        node.getBoundsInScreen(bounds)

        val trace = depth ?: listOf(0)
        val data = NodeData(
            depth = trace,
            info = node,
            bounds = bounds,
        )
        list.nodes[trace.joinToString("-")] = data
        // Log.i(Constants.LOG_TAG, data.toString())

        // send event to flutter
        // A11yServicePlugin.sendEvent(data.toMap())

        if (node.childCount > 0) {
            for (i in 0 until node.childCount) {
                analyzeTree(node.getChild(i), list, trace + i)
            }
        }
    }
}
