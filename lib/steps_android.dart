import 'dart:async';

import 'package:flutter/services.dart';

class StepsAndroid {
  static const MethodChannel _channel = MethodChannel('steps_android');

  /// This method uses the accelerometer sensor of the device to calculate the
  /// steps the user has taken. It returns an integer.
  /// Note that, the device has to have the accelerometer sensor as well as the
  /// Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT.
  /// Also, you need to call StepsAndroid.start before calling getSteps or it
  /// will return null
  static Future<int?> get getSteps async {
    int? steps = await _channel.invokeMethod('getSteps');
    return steps;
  }

  ///This registers the sensor to start listening for acceleration.
  ///Note: Call this before getting steps .
  static Future<void> registerListener() async {
    await _channel.invokeMethod('registerListener');
  }

  ///This unregisters the sensor to stop listening for acceleration.
  ///Note: Call this after getting steps.
  static Future<void> unregisterListener() async {
    await _channel.invokeMethod('unregisterListener');
  }
}
