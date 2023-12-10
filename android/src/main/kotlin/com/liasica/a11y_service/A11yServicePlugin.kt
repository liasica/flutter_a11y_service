package com.liasica.a11y_service

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
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
                val found = AtomicBoolean(false)
                // TODO: 优化逻辑
                A11yService.setAnalyzeTreeCallback { event, analyzed ->
                    // Log.i("→→→", "$event")
                    if (event.packageName == Constants.NAME_SETTINGS_PACKAGE) {
                        if (found.compareAndSet(false, true)) {
                            if (!analyzed.findNodeByText(forceStop).click()) {
                                result.success(false)
                                A11yService.setAnalyzeTreeCallback(null)
                                return@setAnalyzeTreeCallback
                            }
                        }
                    }
                    if (event.className!! == Constants.NAME_ALERT_DIALOG) {
                        result.success(event.source.findTextAndClick(determine))
                        A11yService.setAnalyzeTreeCallback(null)
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
