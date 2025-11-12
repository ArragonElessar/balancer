package com.pruparel.balancer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MazeView extends View {

    private static final String TAG = "MazeView";
    private static final int FRAME_RATE = 30;
    private static final long FRAME_TIME_MS = 1000 / FRAME_RATE;

    // Simple Ball
    private Maze mMaze;

    private Vector3D<Float> mBallAcceleration; // TODO This should be populated by the accelerometer

    // Ball related
    public MazeView(Context context){
        super(context);
        init(null);
    }
    public MazeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public MazeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs){

        // Assign a constant +y acceleration to the ball
        mBallAcceleration = new Vector3D<>(0.0f, 0.0f, 0.0f);

        Vector3D<Float> mazeStartPos = new Vector3D<>(100.0f, 500.0f, 0.0f);
        Vector3D<Integer> numMazeSquares = new Vector3D<>(8, 3, 0);
        float mazeSquareSize = 100.0f;
        int mazeColor = Color.WHITE;
        int ballColor = Color.RED;
        float wallWidth = 2.0f;

        mMaze = new Maze(mazeStartPos, numMazeSquares, mazeSquareSize, mazeColor, ballColor, wallWidth);

        // update the ball's acceleration
        mMaze.updateBallAcceleration(mBallAcceleration);
    }

    public void updateBallAcceleration( Vector3D<Float> acceleration ){
        // Get the acceleration values from the sensor and update the ball acceleration
        mBallAcceleration = acceleration;
        mMaze.updateBallAcceleration(acceleration);
    }

    // Called when the view's size is first determined or changed
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d(TAG, "onSizeChanged: View dimensions are " + w + "x" + h);
    }

    private final Runnable animationLoop = new Runnable() {
        @Override
        public void run() {

            // Simulate this world
            mMaze.updateBallAcceleration(mBallAcceleration); // TODO should be updated using sensor
            mMaze.updateBallVelocityAndPosition();

            // Redraw
            invalidate();
            postDelayed(this, FRAME_TIME_MS);
        }
    };

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        // Start the animation loop when the view is attached to a window
        Log.d(TAG, "onAttachedToWindow: Starting animation loop");
        post(animationLoop);
    }

    @Override
    protected void onDetachedFromWindow() {
        // Stop the animation loop when the view is detached
        removeCallbacks(animationLoop);
        Log.d(TAG, "onDetachedFromWindow: Stopping animation loop");
        super.onDetachedFromWindow();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        mMaze.draw(canvas);
    }
}
