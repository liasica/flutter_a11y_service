package com.liasica.a11y_service

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.RequiresApi

fun Context.performAction(action: Int) = require().performGlobalAction(action)

// 返回
fun Context.back() = performAction(AccessibilityService.GLOBAL_ACTION_BACK)

// Home键
fun Context.home() = performAction(AccessibilityService.GLOBAL_ACTION_HOME)

// 最近任务
fun Context.recent() = performAction(AccessibilityService.GLOBAL_ACTION_RECENTS)

// 电源菜单
fun Context.powerDialog() = performAction(AccessibilityService.GLOBAL_ACTION_POWER_DIALOG)

// 通知栏
fun Context.notificationBar() = performAction(AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS)

// 通知栏 → 快捷设置
fun Context.quickSettings() = performAction(AccessibilityService.GLOBAL_ACTION_QUICK_SETTINGS)

// 锁屏
@RequiresApi(Build.VERSION_CODES.P)
fun Context.lockScreen() = performAction(AccessibilityService.GLOBAL_ACTION_LOCK_SCREEN)

// 应用分屏
fun Context.splitScreen() = performAction(AccessibilityService.GLOBAL_ACTION_TOGGLE_SPLIT_SCREEN)

// 休眠
fun Context.sleep(millis: Long) = Thread.sleep(millis)

fun CharSequence?.nullableString() = if (this.isNullOrBlank()) "" else this.toString()

fun String?.nullableString() = if (this.isNullOrBlank()) "" else this

fun Context.requestPermission() {
    startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    })
}

fun Context.require() = run { if (!A11yService.isGranted) requestPermission(); A11yService.instance!! }

fun Context.openAppSettings(name: String) {
    startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", name, null)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    })
}

fun List<AccessibilityNodeInfo>?.click() : Boolean {
    if (this == null) return false
    for (node in this) {
        if (node.isClickable && node.isEnabled) {
            return node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        }
    }
    return false
}

fun AccessibilityNodeInfo?.findTextAndClick(text: String): Boolean {
    this?.let {
        val nodes = this.findAccessibilityNodeInfosByText(text)
        for (node in nodes) {
            if (node.isClickable && node.isEnabled) {
                return node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            }
        }
    }

    return false
}