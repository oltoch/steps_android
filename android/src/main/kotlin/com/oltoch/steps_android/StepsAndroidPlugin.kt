package com.oltoch.steps_android

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.src.main.kotlin.com.oltoch.steps_android.*
import androidx.annotation.NonNull
import io.flutter.embedding.android.FlutterActivity

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

/** StepsAndroidPlugin */
class StepsAndroidPlugin: FlutterPlugin, MethodCallHandler, SensorEventListener, StepListener,FlutterActivity() {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var channel : MethodChannel
  private var pedometerModel: PedometerViewModel? = null
  private var sensorManager: SensorManager? = null
  private var output:Boolean = false

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "steps_android")
    channel.setMethodCallHandler(this)

  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {

    sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
    output = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT &&
            sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) !=null

    if(call.method == "getSteps"){
      if(output){
        startCounting()
        println("${pedometerModel?.amountOfSteps} here")
        result.success(pedometerModel?.amountOfSteps)
      }
      else{
        result.success(-1)
      }
    }
    if(call.method == "registerListener"){
      println("Listener registered")
      if(output){
        pedometerModel = PedometerViewModel()
        begin()
      }
    }
    if(call.method == "unregisterListener"){
      println("Listener unregistered")
      stopCounting()
    }
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }

  override fun onSensorChanged(sensorEvent: SensorEvent?) {
    val newAccelerationData = AccelerationData()
    if(sensorEvent !=null) {
      newAccelerationData.x = sensorEvent.values[0]
      newAccelerationData.y = sensorEvent.values[1]
      newAccelerationData.z = sensorEvent.values[2]
      newAccelerationData.time = sensorEvent.timestamp

      pedometerModel?.accelerationDataArrayList?.add(newAccelerationData)
      pedometerModel?.stepDetector?.addAccelerationData(newAccelerationData)
    }
//        if (mCounterSteps < 1) {
//            // initial value
//            if (p0 != null) {
//                mCounterSteps = p0.values[0].toInt()
//            }
//        }
//        if (p0 != null) {
//            mSteps = p0.values[0].toInt()-mCounterSteps
//        }
//        mSteps += mPreviousCounterSteps
  }

  override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
  }

  private fun begin(){
    if (pedometerModel?.sensorManager == null) {
      pedometerModel?.sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
    }
    if (pedometerModel?.accelerationSensor == null) {
      if (pedometerModel?.sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
        pedometerModel?.accelerationSensor = pedometerModel?.sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
      }
    }
    if (pedometerModel?.stepDetector== null) {
      pedometerModel?.stepDetector = StepDetector()
    }
    pedometerModel?.stepDetector?.registerStepListener(this)

    if (pedometerModel?.accelerationDataArrayList == null) {
      pedometerModel?.accelerationDataArrayList = ArrayList<AccelerationData>()
    }
  }

  private fun startCounting(){
    pedometerModel?.sensorManager?.registerListener(
      this,
      pedometerModel?.accelerationSensor,
      SensorManager.SENSOR_DELAY_NORMAL
    )
  }

  private fun stopCounting(){
    pedometerModel?.sensorManager?.unregisterListener(this)
  }

  override fun step(accelerationData: AccelerationData?, stepType: StepType?) {

    // Step event coming back from StepDetector
    pedometerModel?.amountOfSteps = pedometerModel?.amountOfSteps?.plus(1)!!
    when {
      stepType === StepType.WALKING -> {
        pedometerModel?.walkingSteps = pedometerModel?.walkingSteps?.plus(1)!!
      }
      stepType === StepType.JOGGING -> {
        pedometerModel?.joggingSteps = pedometerModel?.joggingSteps?.plus(1)!!
      }
      else -> {
        pedometerModel?.runningSteps = pedometerModel?.runningSteps?.plus(1)!!
      }
    }
  }
}
