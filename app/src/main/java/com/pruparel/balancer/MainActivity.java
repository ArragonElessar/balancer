package com.pruparel.balancer;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    public static final String TAG = "MainActivity";

    // Needed for sensors
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    // UI Elements
    TextView mXAxisVal, mYAxisVal, mZAxisVal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Initialise the sensors
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Initialize the text fields
        mXAxisVal = findViewById(R.id.x_axis_val);
        mYAxisVal = findViewById(R.id.y_axis_val);
        mZAxisVal = findViewById(R.id.z_axis_val);

        // Print out a list of available sensors
        List<Sensor> sensorList = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        for(Sensor sensor : sensorList){
            Log.d(TAG, "onCreate: " + sensor.getName());
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
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
            // Log this info out
            Log.d(TAG, "onSensorChanged: typeAccuracy: " + accuracy + " x: " + ax + " y: " + ay + " z: " + az);

            // Update the UI Elements
            String x_val = String.format("%.3f", ax);
            String y_val = String.format("%.3f", ay);
            String z_val = String.format("%.3f", az);

            mXAxisVal.setText(x_val);
            mYAxisVal.setText(y_val);
            mZAxisVal.setText(z_val);
        }
    }
}