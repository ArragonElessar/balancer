package com.pruparel.balancer;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;

public class MazeActivity extends Activity implements SensorEventListener {

    private static final String TAG = "MazeActivity";
    private RelativeLayout mRelativeLayout;
    private MazeView mMazeView;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // Log Entry
        Log.d(TAG, "onCreate: ");
        setContentView(R.layout.activity_maze);

        // Initialise the sensors
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // init the relative layout
        mRelativeLayout = findViewById(R.id.maze);
        mMazeView = new MazeView(this);
        mRelativeLayout.addView(mMazeView);
    }

    @Override
    protected void onPause(){
        super.onPause();
        Log.d(TAG, "onPause: unregistering accelerometer");
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.d(TAG, "onResume: Registering accelerometer listener");
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(TAG, "onAccuracyChanged: NOT IMPLEMENTED");
    }

    public void onSensorChanged(SensorEvent event) {
        // parse the information
        float ax = -1 * event.values[0]; // Screen Pixel X axis is inverse to accelerometer X axis
        float ay = event.values[1];
        float az = 0.0f;
        Vector3D<Float> acceleration = new Vector3D<>(ax, ay, az);
        // Check if this is our mAccelerometer object
        if (event.sensor.equals(mAccelerometer)){
            mMazeView.updateBallAcceleration(acceleration);
        }
    }
}
