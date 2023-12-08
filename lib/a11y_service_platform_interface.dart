import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'a11y_service_method_channel.dart';

abstract class A11yServicePlatform extends PlatformInterface {
  /// Constructs a A11yServicePlatform.
  A11yServicePlatform() : super(token: _token);

  static final Object _token = Object();

  static A11yServicePlatform _instance = MethodChannelA11yService();

  /// The default instance of [A11yServicePlatform] to use.
  ///
  /// Defaults to [MethodChannelA11yService].
  static A11yServicePlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [A11yServicePlatform] when
  /// they register themselves.
  static set instance(A11yServicePlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
