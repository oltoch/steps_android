import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:steps_android/steps_android.dart';

void main() {
  const MethodChannel channel = MethodChannel('steps_android');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return 42;
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getSteps', () async {
    expect(await StepsAndroid.getSteps, 42);
  });
}
