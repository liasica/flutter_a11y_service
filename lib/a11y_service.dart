import 'package:flutter/services.dart';

class A11yService {
  final methodChannel = const MethodChannel('com.liasica.a11y_service/method');

  Future<bool?> requestPermission() async {
    return await methodChannel.invokeMethod<bool>('requestPermission');
  }
}
