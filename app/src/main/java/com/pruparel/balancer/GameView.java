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

public class GameView extends View {

    private static final String TAG = "GameView";
    private static final int FRAME_RATE = 30; // Desired frames per second
    private static final long FRAME_TIME_MS = 1000 / FRAME_RATE;
    private static final float COR = 0.7f; // Speed at which bounces back off the edges
    private static final float KINETIC_FRICTION_COEFF = 0.1f, G = 9.81f; // Coefficient for kinetic friction, Acceleration due to gravity
    private static final int X_ACC_FACTOR = -1; // Screen vs Accelerometer Sign
    private static final int Y_ACC_FACTOR = 1; // Screen vs Accelerometer Sign
    private static final float SIMULATION_FACTOR = 0.25f;

    private Paint mBallPaint;
    private Paint mBallDataPaint;
    private Paint mAccDataPaint;
    private int mBallRadius;
    private float mBallX, mBallY; // Use float for smoother positioning

    // Velocities for movement, to be updated by sensor data later
    private float mVelocityX = 0.0f;
    private float mVelocityY = 0.0f;
    private float mAccX = 0.0f, mAccY = 0.0f, mAccZ = 0.0f;
    private String mBallData, mAccelerationData;


    public GameView(Context context) {
        super(context);
        init(null);
    }

    public GameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public GameView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public static String roundOff(int digits, float val){
        String formatStr = "%." + digits + "f";
        return String.format(formatStr, val);
    }

    private void init(@Nullable AttributeSet attrs) {
        // Basic definition of the ball
        mBallPaint = new Paint(Paint.ANTI_ALIAS_FLAG); // Enable anti-aliasing for smooth edges
        mBallPaint.setColor(Color.RED);
        mBallPaint.setStyle(Paint.Style.FILL); // A solid ball is usually desired
        mBallRadius = 50; // Use a more manageable initial radius

        // Initialize the ball data paint
        mBallDataPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBallDataPaint.setColor(Color.GREEN);
        mBallDataPaint.setTextSize(50f);
        mBallDataPaint.setTextAlign(Paint.Align.CENTER);
        mBallData = "NOT INITIALIZED YET";

        // Initialize the acceleration data point
        mAccDataPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mAccDataPaint.setColor(Color.GREEN);
        mAccDataPaint.setTextSize(50f);
        mAccDataPaint.setTextAlign(Paint.Align.CENTER);
        mAccelerationData = "NOT INITIALIZED YET";
    }

    // Called when the view's size is first determined or changed
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // Initialize ball position to the center of the view
        // This is the correct place to get final dimensions
        mBallX = w / 2f;
        mBallY = h / 2f;
        Log.d(TAG, "onSizeChanged: View dimensions are " + w + "x" + h);
    }

    private final Runnable animationLoop = new Runnable() {
        @Override
        public void run() {
            // Update ball position based on velocity
            mBallX += mVelocityX;
            mBallY += mVelocityY;

            // Apply friction to Acceleration calculation
            if(mVelocityX > 0.1f) {
                mAccX += G * KINETIC_FRICTION_COEFF;
            }else if(mVelocityX < -0.1f){
                mAccX -=  G * KINETIC_FRICTION_COEFF;
            }

            if(mVelocityY > 0.1f) {
                mAccY -= G * KINETIC_FRICTION_COEFF;
            }
            else if(mVelocityY < -0.1f){
                mAccY +=  G * KINETIC_FRICTION_COEFF;
            }

            // Update the ball's velocity based on acceleration -> screen x, y are opposite to actual measurement axes
            mVelocityX += X_ACC_FACTOR * SIMULATION_FACTOR * mAccX;
            mVelocityY += Y_ACC_FACTOR * SIMULATION_FACTOR * mAccY;

            // Check for collisions with view boundaries
            // Left boundary
            if (mBallX - mBallRadius < 0) {
                mBallX = mBallRadius;
                mVelocityX *= -1 * COR; // Reverse direction
            }
            // Right boundary
            if (mBallX + mBallRadius > getWidth()) {
                mBallX = getWidth() - mBallRadius;
                mVelocityX *= -1 * COR; // Reverse direction
            }
            // Top boundary
            if (mBallY - mBallRadius < 0) {
                mBallY = mBallRadius;
                mVelocityY *= -1 * COR; // Reverse direction
            }
            // Bottom boundary
            if (mBallY + mBallRadius > getHeight()) {
                mBallY = getHeight() - mBallRadius;
                mVelocityY *= -1 * COR; // Reverse direction
            }

            // Update the ball and acceleration data text
            mBallData = "POS: ( " + roundOff(1, mBallX) + ", " + roundOff(1, mBallY) + "), V: (" + roundOff(1, mVelocityX) + " , " + roundOff(1, mVelocityY) + ")";
            mAccelerationData = "ACC: ( " + roundOff(1, mAccX) + ", " + roundOff(1, mAccY) + ", " + roundOff(1, mAccZ) + ")";

            // Request a redraw
            invalidate();

            // Schedule the next frame
            postDelayed(this, FRAME_TIME_MS);
        }
    };

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        // Start the animation loop when the view is attached to a window
        post(animationLoop);
    }

    @Override
    protected void onDetachedFromWindow() {
        // Stop the animation loop when the view is detached
        removeCallbacks(animationLoop);
        super.onDetachedFromWindow();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        // Draw the ball at its current position
        canvas.drawCircle(mBallX, mBallY, mBallRadius, mBallPaint);
        canvas.drawText(mBallData, getWidth() / 2f, 200, mBallDataPaint);
        canvas.drawText(mAccelerationData, getWidth() / 2f, 300, mAccDataPaint);
    }

    /**
     * Public method to update the ball's velocity based on external input (e.g., sensors).
     * @param newVelocityX The new velocity for the x-axis.
     * @param newVelocityY The new velocity for the y-axis.
     */
    public void updateVelocity(float newVelocityX, float newVelocityY) {
        this.mVelocityX = newVelocityX;
        this.mVelocityY = newVelocityY;
    }

    public void updateAcceleration(float x, float y, float z) {
        // Externally set the acceleration values
        mAccX = x;
        mAccY = y;
        mAccZ = z;
    }
}
