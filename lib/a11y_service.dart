import 'package:a11y_service/a11y_service_node.dart';
import 'package:flutter/services.dart';

class A11yService {
  final methodChannel = const MethodChannel('com.liasica.a11y_service/method');
  static const _eventChannel = EventChannel('com.liasica.a11y_service/event');

  Future<bool?> requestPermission() async {
    return await methodChannel.invokeMethod<bool>('requestPermission');
  }

  Future<bool?> isGranted() async {
    return await methodChannel.invokeMethod<bool>('isGranted');
  }

  Stream<A11yServiceNode> get onAccessibilityEvent {
    return _eventChannel.receiveBroadcastStream().map((event) => A11yServiceNode.fromJson(Map.from(event)));
  }

  Future<bool?> forceStopApp(
    String name, {
    String forceStop = '强行停止',
    String determine = '确定',
    String alertDialogName = 'android.app.AlertDialog',
    String appDetailsName = 'com.android.settings.applications.InstalledAppDetailsTop',
  }) async {
    return await methodChannel.invokeMethod<bool>('forceStopApp', {'name': name, 'forceStop': forceStop, 'determine': determine});
  }

  Future<bool?> actionBack() => methodChannel.invokeMethod<bool>('actionBack');

  Future<bool?> actionHome() => methodChannel.invokeMethod<bool>('actionHome');

  Future<bool?> actionRecent() => methodChannel.invokeMethod<bool>('actionRecent');

  Future<bool?> actionPowerDialog() => methodChannel.invokeMethod<bool>('actionPowerDialog');

  Future<bool?> actionNotificationBar() => methodChannel.invokeMethod<bool>('actionNotificationBar');

  Future<bool?> actionQuickSettings() => methodChannel.invokeMethod<bool>('actionQuickSettings');

  Future<bool?> actionLockScreen() => methodChannel.invokeMethod<bool>('actionLockScreen');

  Future<bool?> actionSplitScreen() => methodChannel.invokeMethod<bool>('actionSplitScreen');
}
