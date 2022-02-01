import 'dart:async';

import 'package:flutter/material.dart';
import 'package:steps_android/steps_android.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String steps = '';

  @override
  void initState() {
    super.initState();
    StepsAndroid.registerListener();
  }

  @override
  void dispose() {
    super.dispose();
    StepsAndroid.unregisterListener();
  }

  Future getSteps() async {
    int? step = await StepsAndroid.getSteps;
    if (step == -1) {
      ScaffoldMessenger.of(context).showSnackBar(const SnackBar(
        content: Text('Your device does not support step counting'),
        backgroundColor: Colors.red,
      ));
      return;
    }
    setState(() {
      steps = '$step steps';
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Steps counting example'),
        ),
        body: Center(
          child: Text(steps),
        ),
      ),
    );
  }
}
