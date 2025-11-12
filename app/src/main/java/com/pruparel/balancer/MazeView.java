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
    private static final int FRAME_RATE = 2;
    private static final long FRAME_TIME_MS = 1000 / FRAME_RATE;

    // Ball related
    private Paint mBallPaint;
    private int mBallRadius;
    private float mBallX, mBallY; // Use float for smoother positioning

    // Maze related
    private float mMazeStartX = 200, mMazeStartY = 400;
    private int mMazeHeight, mMazeWidth;
    private float mMazeSquareSizePx; // Number of pixels that a square size exhibits, should be minimum 2x the Ball Size.
    private float mMazeStrokeWidth;
    private Paint mMazePaint;
    private Maze mMaze;

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
        // Characteristics of the Ball
        mBallPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBallPaint.setColor(Color.RED);
        mBallPaint.setStyle(Paint.Style.FILL);
        mBallRadius = 25;
        mBallX = 200; // Initial Position
        mBallY = 200;

        // Initialise the Maze object
        mMazeStrokeWidth = 5; // px

        // TODO These parameters should all come from either MAZE Design
        mMazeStartX = 100; // Arbitrary
        mMazeStartY = 500; // Arbitrary
        mMazeHeight = 2;
        mMazeWidth = 8;
        mMazeSquareSizePx = 100; // TODO This should come from screen parameters.
        Log.d(TAG, "Creating Maze with parameters: " + mMazeStartX + ", " + mMazeStartY + ", " + mMazeHeight + ", " + mMazeWidth);
        mMaze = new Maze(mMazeStartX, mMazeStartY, mMazeHeight, mMazeWidth, mMazeSquareSizePx); // TODO Can separate Square Size into height and width

        // Characteristics of the Maze
        mMazePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMazePaint.setColor(Color.WHITE);
        mMazePaint.setStrokeWidth(mMazeStrokeWidth); // 10 PX
        mMazePaint.setStyle(Paint.Style.STROKE); // Ensure we are drawing a line

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
        mMaze.draw(canvas, mMazePaint);
        canvas.drawCircle(mBallX, mBallY, mBallRadius, mBallPaint);
    }

    public void updateBallPosition( int x, int y ){
        mBallX = x;
        mBallY = y;
    }
}
