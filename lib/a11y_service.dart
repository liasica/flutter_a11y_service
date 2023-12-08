
import 'a11y_service_platform_interface.dart';

class A11yService {
  Future<String?> getPlatformVersion() {
    return A11yServicePlatform.instance.getPlatformVersion();
  }
}
