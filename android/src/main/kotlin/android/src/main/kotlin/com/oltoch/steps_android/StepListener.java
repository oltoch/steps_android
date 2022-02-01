package android.src.main.kotlin.com.oltoch.steps_android;

public interface StepListener {
    void step(AccelerationData accelerationData, StepType stepType);
}
