import 'package:flutter/material.dart';
import 'package:a11y_service/a11y_service.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  final _plugin = A11yService();

  @override
  void initState() {
    super.initState();

    _plugin.onAccessibilityEvent.listen((event) {
      // print('$event');
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Flutter A11y Service Example'),
        ),
        body: SafeArea(
          child: Center(
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              mainAxisSize: MainAxisSize.min,
              children: [
                ElevatedButton(
                  onPressed: () async {
                    final isGranted = await _plugin.requestPermission();
                    print('isGranted: $isGranted');
                  },
                  child: const Text('Request Permission'),
                ),
                ElevatedButton(
                  onPressed: () async {
                    final forceStopApp = await _plugin.forceStopApp('com.android.chrome');
                    print('forceStopApp: $forceStopApp');
                  },
                  child: const Text('Force stop app'),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
