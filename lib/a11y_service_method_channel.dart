import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'a11y_service_platform_interface.dart';

/// An implementation of [A11yServicePlatform] that uses method channels.
class MethodChannelA11yService extends A11yServicePlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('a11y_service');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }
}
