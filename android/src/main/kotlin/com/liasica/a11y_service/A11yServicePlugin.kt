package com.liasica.a11y_service

import android.content.Context
import android.content.Intent
import android.provider.Settings
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


/** A11yServicePlugin */
class A11yServicePlugin : FlutterPlugin, MethodCallHandler, DefaultLifecycleObserver, StreamHandler, ActivityAware {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var channel: MethodChannel

  private lateinit var context: Context

  private var _requestResult: Result? = null

  companion object {
    private var es: EventSink? = null

    val eventSink get() = es
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
      // A11yService.instance?.requestPermission(context, result)
      _requestResult = result
      context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      })
    }
  }

  private fun getActivityLifecycle(activityPluginBinding: ActivityPluginBinding): Lifecycle {
    val reference = activityPluginBinding.lifecycle as HiddenLifecycleReference
    return reference.lifecycle
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
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
}
