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
}
