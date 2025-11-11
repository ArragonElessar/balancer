package com.pruparel.balancer;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;

public class MazeActivity extends Activity {

    private static final String TAG = "MazeActivity";
    private RelativeLayout mRelativeLayout;
    private MazeView mMazeView;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // Log Entry
        Log.d(TAG, "onCreate: ");
        setContentView(R.layout.activity_maze);

        // init the relative layout
        mRelativeLayout = findViewById(R.id.maze);
        mMazeView = new MazeView(this);
        mRelativeLayout.addView(mMazeView);
    }

}
