package com.liasica.a11y_service

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.embedding.engine.plugins.lifecycle.HiddenLifecycleReference
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.EventChannel.EventSink
import io.flutter.plugin.common.EventChannel.StreamHandler
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import java.util.concurrent.atomic.AtomicBoolean


/** A11yServicePlugin */
class A11yServicePlugin : FlutterPlugin, MethodCallHandler, DefaultLifecycleObserver, StreamHandler, ActivityAware {

    private lateinit var channel: MethodChannel

    private lateinit var context: Context

    private var _requestResult: Result? = null

    private var _supportOverlayWindow: Boolean = false

    private lateinit var activity: Activity

    companion object {
        private var es: EventSink? = null

        fun sendEvent(event: Any) {
            Handler(Looper.getMainLooper()).post {
                es?.success(event)
            }
        }
    }

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        context = flutterPluginBinding.applicationContext

        EventChannel(flutterPluginBinding.binaryMessenger, Constants.EVENT_CHANNEL_NAME).setStreamHandler(this)

        channel = MethodChannel(flutterPluginBinding.binaryMessenger, Constants.METHOD_CHANNEL_NAME)
        channel.setMethodCallHandler(this)
    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        when (call.method) {
            "isGranted" -> result.success(A11yService.isGranted)
            "requestPermission" -> requestPermission(result)
            "showOverlayWindow" -> showOverlayWindow(call.arguments as Map<*, *>?, result)
            "forceStopApp" -> forceStopApp(call.arguments as Map<*, *>?, result)
            "actionBack" -> result.success(context.back())
            "actionHome" -> result.success(context.home())
            "actionRecent" -> result.success(context.recent())
            "actionPowerDialog" -> result.success(context.powerDialog())
            "actionNotificationBar" -> result.success(context.notificationBar())
            "actionQuickSettings" -> result.success(context.quickSettings())
            "actionLockScreen" -> result.success(context.lockScreen())
            "actionSplitScreen" -> result.success(context.splitScreen())
            else -> result.notImplemented()
        }
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    private fun requestPermission(result: Result) {
        if (A11yService.isGranted) {
            result.success(true)
        } else {
            _requestResult = result
            context.requestPermission()
        }
    }

    private fun getActivityLifecycle(activityPluginBinding: ActivityPluginBinding): Lifecycle {
        val reference = activityPluginBinding.lifecycle as HiddenLifecycleReference
        return reference.lifecycle
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activity = binding.activity

        val lifecycle = getActivityLifecycle(binding)
        lifecycle.addObserver(this)
    }

    override fun onDetachedFromActivityForConfigChanges() {}
    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {}
    override fun onDetachedFromActivity() {}

    private fun sendPermissionResult(result: Boolean) {
        _requestResult?.success(result)
        _requestResult = null
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        sendPermissionResult(A11yService.isGranted)
    }

    override fun onListen(arguments: Any?, events: EventSink?) {
        es = events
    }

    override fun onCancel(arguments: Any?) {
        es = null
    }

    private fun showOverlayWindow(map: Map<*, *>?, result: Result) {
        if (!_supportOverlayWindow) {
            result.success(false)
        }
        TODO("Show overlay window")
    }

    private fun forceStopApp(map: Map<*, *>?, result: Result) {
        map?.let {
            try {
                val forceStop = it["forceStop"] as String
                val determine = it["determine"] as String
                val alertDialogName = it["alertDialogName"] as String? ?: Constants.NAME_ALERT_DIALOG
                val appDetailsName = it["appDetailsName"] as String? ?: Constants.NAME_APP_DETAILS
                val found = AtomicBoolean(false)
                A11yService.setAnalyzeTreeCallback { event, _ ->
                    if (event.className!! == appDetailsName && found.compareAndSet(false, true)) {
                        if (!event.source.findTextAndClick(forceStop)) {
                            result.success(false)
                            context.back()
                            A11yService.setAnalyzeTreeCallback(null)
                            return@setAnalyzeTreeCallback
                        }
                    }
                    if (event.className!! == alertDialogName) {
                        result.success(event.source.findTextAndClick(determine))
                        A11yService.setAnalyzeTreeCallback(null)
                        Thread.sleep(100)
                        context.back()
                        return@setAnalyzeTreeCallback
                    }
                }
                context.openAppSettings(it["name"] as String)
            } catch (e: Throwable) {
                result.success(false)
            }
        }
    }
}
