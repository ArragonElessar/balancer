package com.pruparel.balancer;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;

public class GameActivity extends Activity {

    private static final String TAG = "GameActivity";
    private RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // Log entry
        Log.d(TAG, "onCreate: ");

        setContentView(R.layout.activity_game);

        // init the relative layout
        relativeLayout = findViewById(R.id.game);

        GameView gameView = new GameView(this);
        relativeLayout.addView(gameView);

    }

}
