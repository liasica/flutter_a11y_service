import 'package:a11y_service/result.dart';
import 'package:flutter/services.dart';

class A11yService {
  final methodChannel = const MethodChannel('com.liasica.a11y_service/method');
  static const _eventChannel = EventChannel('com.liasica.a11y_service/event');
  static const _permissionChannel = EventChannel('com.liasica.a11y_service/permission');

  Future<bool?> requestPermission() async {
    return await methodChannel.invokeMethod<bool>('requestPermission');
  }

  Future<bool?> isGranted() async {
    return await methodChannel.invokeMethod<bool>('isGranted');
  }

  Stream<Result> get onAccessibilityEvent {
    return _eventChannel.receiveBroadcastStream().map((event) => Result.fromJson(Map.from(event)));
  }

  Stream<bool> get onPermissionChanged {
    return _permissionChannel.receiveBroadcastStream().map((event) => event as bool);
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

  /// Find text node and click
  /// if [expectedText] is not null, it will compare with the [expectedText] of the expected node found, equal to return true
  /// if [expectedText] is null, it will find [text] node found, and click it directly
  Future<bool?> actionFindTextAndClick({
    required String packageName,
    required String text,
    String? expectedText,
    int? timeout = 10000,
    bool? textAllMatch = true,
    bool? includeDesc = true,
    bool? descAllMatch = false,
    bool? enableRegular = false,
  }) =>
      methodChannel.invokeMethod<bool>('actionFindTextAndClick', {
        'packageName': packageName,
        'text': text,
        'expectedText': expectedText,
        'timeout': timeout,
        'textAllMatch': textAllMatch,
        'includeDesc': includeDesc,
        'descAllMatch': descAllMatch,
        'enableRegular': enableRegular,
      });
}
