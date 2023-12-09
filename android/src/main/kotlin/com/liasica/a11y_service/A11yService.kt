package com.liasica.a11y_service

import android.accessibilityservice.AccessibilityService
import android.util.LruCache
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import io.flutter.Log
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class A11yService : AccessibilityService() {
  private val executor: ExecutorService = Executors.newFixedThreadPool(4)

  companion object {
    // Exported Accessibility Service Instance
    var instance: A11yService? = null

    // Is Accessibility Service Granted
    val isGranted: Boolean get() = instance != null
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

      if (className.isNotBlank() && packageName.isNotBlank()) {
        Log.i(Constants.LOG_TAG, "className: $className, packageName: $packageName, eventType: $eventType")
        executor.execute {
          Thread.sleep(100)
        }
      }
    }
  }

  private fun analyzeTree(current: EventData, node: AccessibilityNodeInfo) {
  }
}
