package com.pruparel.balancer;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;

public class GameActivity extends Activity implements SensorEventListener  {

    private static final String TAG = "GameActivity";
    private RelativeLayout relativeLayout;
    private GameView mGameView;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // Log entry
        Log.d(TAG, "onCreate: ");
        setContentView(R.layout.activity_game);

        // Initialise the sensors
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // init the relative layout
        relativeLayout = findViewById(R.id.game);

        mGameView = new GameView(this);
        relativeLayout.addView(mGameView);

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
        int accuracy = event.accuracy;
        float ax = event.values[0];
        float ay = event.values[1];
        float az = event.values[2];

        // Check if this is our mAccelerometer object
        if (event.sensor.equals(mAccelerometer)){
            mGameView.updateAcceleration(ax, ay, az);
        }
    }
}
